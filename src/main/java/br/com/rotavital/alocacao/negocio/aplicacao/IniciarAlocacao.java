package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.AlocacaoRepository;

/** Caso de uso: ranquear as ofertas e abrir a primeira tentativa (fica RESERVA_PENDENTE). */
public class IniciarAlocacao {

    private final AlocacaoRepository repositorio;
    private final OfertasDisponiveis ofertas;

    public IniciarAlocacao(AlocacaoRepository repositorio, OfertasDisponiveis ofertas) {
        this.repositorio = repositorio;
        this.ofertas = ofertas;
    }

    /** Idempotente por requisicaoId: repetir o comando devolve a alocacao existente. */
    public Alocacao executar(String requisicaoId, String insumoId, int quantidade) {
        return repositorio.buscar(requisicaoId).orElseGet(() -> {
            Alocacao alocacao = new Alocacao(requisicaoId, insumoId, quantidade);
            alocacao.iniciarTentativa(ofertas.para(insumoId));
            repositorio.salvar(alocacao);
            return alocacao;
        });
    }
}
