package br.com.rotavital.requisicoes.negocio.dominio;

import java.util.Optional;

public interface RequisicaoRepository {
    void salvar(Requisicao requisicao);

    Optional<Requisicao> buscar(String id);

    long proximoNumero();
}
