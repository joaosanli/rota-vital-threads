package br.com.rotavital.alocacao.negocio.dominio;

import java.util.Optional;

/** Porta de persistencia da raiz; implementada na camada de dados. */
public interface AlocacaoRepository {
    Optional<Alocacao> buscar(String requisicaoId);

    void salvar(Alocacao alocacao);
}
