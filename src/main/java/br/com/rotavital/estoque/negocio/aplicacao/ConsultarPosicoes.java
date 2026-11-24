package br.com.rotavital.estoque.negocio.aplicacao;

import br.com.rotavital.estoque.negocio.dominio.EstoqueRepository;
import br.com.rotavital.estoque.negocio.dominio.PosicaoEstoque;

import java.util.List;

/** Caso de uso: consultar o saldo de um insumo em todos os hospitais. */
public class ConsultarPosicoes {

    private final EstoqueRepository repositorio;

    public ConsultarPosicoes(EstoqueRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<PosicaoEstoque> porInsumo(String insumoId) {
        return repositorio.porInsumo(insumoId);
    }
}
