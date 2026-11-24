package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.AlocacaoRepository;

import java.util.Optional;

/** Caso de uso: consultar uma alocacao pela requisicao. */
public class ConsultarAlocacao {

    private final AlocacaoRepository repositorio;

    public ConsultarAlocacao(AlocacaoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<Alocacao> executar(String requisicaoId) {
        return repositorio.buscar(requisicaoId);
    }
}
