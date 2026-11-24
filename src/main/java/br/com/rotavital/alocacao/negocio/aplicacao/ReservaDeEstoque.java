package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;

/** Porta: reserva e liberacao no Estoque. Operacoes idempotentes por tentativaId. */
public interface ReservaDeEstoque {
    /** @return true se o Estoque confirmou a reserva. */
    boolean reservar(SolicitacaoReserva solicitacao);

    void liberar(SolicitacaoReserva solicitacao);
}
