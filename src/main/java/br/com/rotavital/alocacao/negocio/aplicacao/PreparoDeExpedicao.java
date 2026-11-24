package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;

/** Porta: preparo e cancelamento da transferencia na Expedicao. */
public interface PreparoDeExpedicao {
    /** @return true se o preparo foi concluido. */
    boolean preparar(SolicitacaoReserva solicitacao);

    void cancelar(SolicitacaoReserva solicitacao);
}
