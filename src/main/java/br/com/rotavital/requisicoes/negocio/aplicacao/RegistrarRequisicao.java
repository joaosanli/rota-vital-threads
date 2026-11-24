package br.com.rotavital.requisicoes.negocio.aplicacao;

import br.com.rotavital.requisicoes.negocio.dominio.Prioridade;
import br.com.rotavital.requisicoes.negocio.dominio.Requisicao;
import br.com.rotavital.requisicoes.negocio.dominio.RequisicaoRepository;

/** Caso de uso: registrar uma requisicao com identificador sequencial (REQ-n). */
public class RegistrarRequisicao {

    private final RequisicaoRepository repositorio;

    public RegistrarRequisicao(RequisicaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Requisicao executar(String hospitalSolicitante, String insumoId, int quantidade, Prioridade prioridade) {
        Requisicao requisicao = new Requisicao("REQ-" + repositorio.proximoNumero(),
                hospitalSolicitante, insumoId, quantidade, prioridade);
        repositorio.salvar(requisicao);
        return requisicao;
    }
}
