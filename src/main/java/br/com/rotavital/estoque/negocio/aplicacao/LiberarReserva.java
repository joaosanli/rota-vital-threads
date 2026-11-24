package br.com.rotavital.estoque.negocio.aplicacao;

import br.com.rotavital.estoque.negocio.dominio.EstoqueRepository;

/** Caso de uso: libera a reserva de uma tentativa (compensacao). Idempotente. */
public class LiberarReserva {

    private final EstoqueRepository repositorio;

    public LiberarReserva(EstoqueRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(String hospitalId, String insumoId, String tentativaId) {
        repositorio.buscar(hospitalId, insumoId).ifPresent(p -> p.liberar(tentativaId));
    }
}
