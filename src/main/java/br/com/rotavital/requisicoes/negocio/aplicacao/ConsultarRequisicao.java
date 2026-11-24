package br.com.rotavital.requisicoes.negocio.aplicacao;

import br.com.rotavital.requisicoes.negocio.dominio.Requisicao;
import br.com.rotavital.requisicoes.negocio.dominio.RequisicaoRepository;

import java.util.Optional;

public class ConsultarRequisicao {

    private final RequisicaoRepository repositorio;

    public ConsultarRequisicao(RequisicaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<Requisicao> executar(String id) {
        return repositorio.buscar(id);
    }
}
