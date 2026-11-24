package br.com.rotavital.estoque.negocio.dominio;

import java.util.List;
import java.util.Optional;

/** Porta de persistencia das posicoes de estoque. */
public interface EstoqueRepository {
    List<PosicaoEstoque> porInsumo(String insumoId);

    Optional<PosicaoEstoque> buscar(String hospitalId, String insumoId);
}
