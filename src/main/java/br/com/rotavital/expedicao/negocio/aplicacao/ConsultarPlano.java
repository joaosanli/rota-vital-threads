package br.com.rotavital.expedicao.negocio.aplicacao;

import br.com.rotavital.expedicao.negocio.dominio.PlanoRepository;
import br.com.rotavital.expedicao.negocio.dominio.PlanoTransferencia;

import java.util.Optional;

public class ConsultarPlano {

    private final PlanoRepository repositorio;

    public ConsultarPlano(PlanoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Optional<PlanoTransferencia> executar(String tentativaId) {
        return repositorio.buscar(tentativaId);
    }
}
