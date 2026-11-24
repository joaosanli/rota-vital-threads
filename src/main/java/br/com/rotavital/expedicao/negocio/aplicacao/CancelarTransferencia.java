package br.com.rotavital.expedicao.negocio.aplicacao;

import br.com.rotavital.expedicao.negocio.dominio.PlanoRepository;

/** Caso de uso: cancelar o plano de uma tentativa (compensacao). Idempotente. */
public class CancelarTransferencia {

    private final PlanoRepository repositorio;

    public CancelarTransferencia(PlanoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(String tentativaId) {
        repositorio.buscar(tentativaId).ifPresent(plano -> {
            plano.cancelar();
            repositorio.salvar(plano);
        });
    }
}
