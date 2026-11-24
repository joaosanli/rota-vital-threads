package br.com.rotavital.expedicao.negocio.dominio;

import java.util.Optional;

public interface PlanoRepository {
    Optional<PlanoTransferencia> buscar(String tentativaId);

    void salvar(PlanoTransferencia plano);
}
