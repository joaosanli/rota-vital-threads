package br.com.rotavital.alocacao.negocio.dominio;

public enum EstadoTentativa {
    RESERVA_PENDENTE, RESERVADA, CONFIRMADA, COMPENSANDO, ENCERRADA;

    public boolean ativa() {
        return this != ENCERRADA && this != CONFIRMADA;
    }
}
