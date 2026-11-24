package br.com.rotavital.expedicao.negocio.dominio;

import java.time.Instant;

/** Objeto de valor: intervalo em que o transportador retira o insumo. */
public record JanelaColeta(Instant inicio, Instant fim) {

    public JanelaColeta {
        if (inicio == null || fim == null || !fim.isAfter(inicio)) {
            throw new IllegalArgumentException("a janela de coleta deve terminar depois de comecar");
        }
    }
}
