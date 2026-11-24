package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.AlocacaoRepository;
import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;

import java.util.NoSuchElementException;

/**
 * Caso de uso: orquestra a saga reserva -> preparo. Em falha, compensa (cancela o preparo,
 * libera a reserva) e tenta a proxima oferta, ate confirmar ou nao haver oferta suficiente.
 */
public class ConfirmarAlocacao {

    private final AlocacaoRepository repositorio;
    private final OfertasDisponiveis ofertas;
    private final ReservaDeEstoque estoque;
    private final PreparoDeExpedicao expedicao;

    public ConfirmarAlocacao(AlocacaoRepository repositorio, OfertasDisponiveis ofertas,
                             ReservaDeEstoque estoque, PreparoDeExpedicao expedicao) {
        this.repositorio = repositorio;
        this.ofertas = ofertas;
        this.estoque = estoque;
        this.expedicao = expedicao;
    }

    public Alocacao executar(String requisicaoId) {
        Alocacao alocacao = repositorio.buscar(requisicaoId)
                .orElseThrow(() -> new NoSuchElementException("Alocacao inexistente: " + requisicaoId));
        boolean seguir = true;
        while (seguir) {
            switch (alocacao.estado()) {
                case ABERTA -> alocacao.iniciarTentativa(ofertas.para(alocacao.insumoId()));
                case RESERVA_PENDENTE -> executarTentativa(alocacao);
                default -> seguir = false;
            }
        }
        repositorio.salvar(alocacao);
        return alocacao;
    }

    private void executarTentativa(Alocacao alocacao) {
        SolicitacaoReserva solicitacao = alocacao.solicitacaoPendente();
        String tentativaId = solicitacao.tentativaId();

        if (!estoque.reservar(solicitacao)) {
            alocacao.registrarReservaRecusada(tentativaId);
            return;
        }
        alocacao.registrarReservaConfirmada(tentativaId);

        if (expedicao.preparar(solicitacao)) {
            alocacao.registrarPreparoConcluido(tentativaId);
            return;
        }
        alocacao.registrarPreparoFalhou(tentativaId);
        expedicao.cancelar(solicitacao);
        estoque.liberar(solicitacao);
        alocacao.registrarReservaLiberada(tentativaId);
    }
}
