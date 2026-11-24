package br.com.rotavital.estoque.negocio.aplicacao;

import br.com.rotavital.estoque.negocio.dominio.EstoqueRepository;

/** Caso de uso: reserva atomica de unidades de um insumo em um hospital. */
public class ReservarInsumo {

    private final EstoqueRepository repositorio;

    public ReservarInsumo(EstoqueRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** @return true se a reserva foi confirmada (ou ja existia para a tentativa). */
    public boolean executar(String hospitalId, String insumoId, String tentativaId, int quantidade) {
        return repositorio.buscar(hospitalId, insumoId)
                .map(posicao -> posicao.reservar(tentativaId, quantidade))
                .orElse(false);
    }
}
