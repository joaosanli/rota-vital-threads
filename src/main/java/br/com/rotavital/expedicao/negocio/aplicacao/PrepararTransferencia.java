package br.com.rotavital.expedicao.negocio.aplicacao;

import br.com.rotavital.expedicao.negocio.dominio.JanelaColeta;
import br.com.rotavital.expedicao.negocio.dominio.PlanoRepository;
import br.com.rotavital.expedicao.negocio.dominio.PlanoTransferencia;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/** Caso de uso: preparar o plano de transferencia com janela de coleta de duas horas. */
public class PrepararTransferencia {

    private static final Duration ANTECEDENCIA = Duration.ofHours(1);
    private static final Duration DURACAO_JANELA = Duration.ofHours(2);

    private final PlanoRepository repositorio;
    private final Clock relogio;

    public PrepararTransferencia(PlanoRepository repositorio, Clock relogio) {
        this.repositorio = repositorio;
        this.relogio = relogio;
    }

    /** @return true se o plano esta preparado; false se ja foi cancelado ou os dados sao invalidos. */
    public boolean executar(String requisicaoId, String tentativaId, String fornecedorId,
                            String insumoId, int quantidade) {
        var existente = repositorio.buscar(tentativaId);
        if (existente.isPresent()) {
            return existente.get().estado() == PlanoTransferencia.Estado.PREPARADO;
        }
        if (quantidade <= 0) {
            return false;
        }
        Instant inicio = relogio.instant().plus(ANTECEDENCIA);
        repositorio.salvar(new PlanoTransferencia(requisicaoId, tentativaId, fornecedorId, insumoId,
                quantidade, new JanelaColeta(inicio, inicio.plus(DURACAO_JANELA))));
        return true;
    }
}
