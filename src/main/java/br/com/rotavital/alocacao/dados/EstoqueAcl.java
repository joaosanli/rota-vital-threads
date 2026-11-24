package br.com.rotavital.alocacao.dados;

import br.com.rotavital.alocacao.negocio.aplicacao.OfertasDisponiveis;
import br.com.rotavital.alocacao.negocio.aplicacao.ReservaDeEstoque;
import br.com.rotavital.alocacao.negocio.dominio.Oferta;
import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;
import br.com.rotavital.estoque.negocio.aplicacao.ConsultarPosicoes;
import br.com.rotavital.estoque.negocio.aplicacao.LiberarReserva;
import br.com.rotavital.estoque.negocio.aplicacao.ReservarInsumo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Camada anticorrupcao: traduz "posicao de estoque" (modelo do Estoque) em "oferta"
 * (modelo da Alocacao) e as chamadas de reserva. A distancia nao pertence ao Estoque; vem
 * de uma tabela sintetica ate existir um servico de rotas.
 */
@Component
public class EstoqueAcl implements OfertasDisponiveis, ReservaDeEstoque {

    private static final int DISTANCIA_PADRAO_KM = 50;
    private static final Map<String, Integer> DISTANCIA_KM = Map.of("H-01", 5, "H-02", 10, "H-03", 30);

    private final ConsultarPosicoes consultar;
    private final ReservarInsumo reservar;
    private final LiberarReserva liberar;

    public EstoqueAcl(ConsultarPosicoes consultar, ReservarInsumo reservar, LiberarReserva liberar) {
        this.consultar = consultar;
        this.reservar = reservar;
        this.liberar = liberar;
    }

    @Override
    public List<Oferta> para(String insumoId) {
        return consultar.porInsumo(insumoId).stream()
                .map(p -> new Oferta(p.hospitalId(), p.saldo(),
                        DISTANCIA_KM.getOrDefault(p.hospitalId(), DISTANCIA_PADRAO_KM)))
                .toList();
    }

    @Override
    public boolean reservar(SolicitacaoReserva s) {
        return reservar.executar(s.hospitalId(), s.insumoId(), s.tentativaId(), s.quantidade());
    }

    @Override
    public void liberar(SolicitacaoReserva s) {
        liberar.executar(s.hospitalId(), s.insumoId(), s.tentativaId());
    }
}
