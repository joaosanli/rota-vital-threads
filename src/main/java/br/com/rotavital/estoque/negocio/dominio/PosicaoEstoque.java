package br.com.rotavital.estoque.negocio.dominio;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Raiz do agregado do Estoque: saldo de um insumo em um hospital.
 *
 * <p>Invariantes: o saldo nunca fica negativo; reservar e liberar sao idempotentes por
 * tentativaId; uma tentativa ja liberada nao pode ser reaberta por comando atrasado.
 */
public class PosicaoEstoque {

    private final String hospitalId;
    private final String insumoId;
    private int saldo;
    private final Map<String, Integer> reservas = new HashMap<>();
    private final Set<String> liberadas = new HashSet<>();

    public PosicaoEstoque(String hospitalId, String insumoId, int saldo) {
        if (saldo < 0) {
            throw new IllegalArgumentException("saldo nao pode ser negativo");
        }
        this.hospitalId = hospitalId;
        this.insumoId = insumoId;
        this.saldo = saldo;
    }

    public synchronized boolean reservar(String tentativaId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser positiva");
        }
        if (reservas.containsKey(tentativaId)) {
            return true;
        }
        if (liberadas.contains(tentativaId) || saldo < quantidade) {
            return false;
        }
        saldo -= quantidade;
        reservas.put(tentativaId, quantidade);
        return true;
    }

    public synchronized void liberar(String tentativaId) {
        Integer quantidade = reservas.remove(tentativaId);
        if (quantidade != null) {
            saldo += quantidade;
        }
        liberadas.add(tentativaId);
    }

    public String hospitalId() { return hospitalId; }
    public String insumoId() { return insumoId; }
    public synchronized int saldo() { return saldo; }
}
