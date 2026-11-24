package br.com.rotavital.alocacao.negocio.dominio;

/** Entidade do agregado Alocacao: uma rodada de selecao de oferta. */
public class Tentativa {

    private final String id;
    private final Oferta oferta;
    private final Pontuacao pontuacao;
    private final int quantidade;
    private EstadoTentativa estado = EstadoTentativa.RESERVA_PENDENTE;
    private boolean reservaTerminal;
    private boolean preparoTerminal;

    Tentativa(String id, Oferta oferta, Pontuacao pontuacao, int quantidade) {
        this.id = id;
        this.oferta = oferta;
        this.pontuacao = pontuacao;
        this.quantidade = quantidade;
    }

    public String id() { return id; }
    public Oferta oferta() { return oferta; }
    public Pontuacao pontuacao() { return pontuacao; }
    public int quantidade() { return quantidade; }
    public EstadoTentativa estado() { return estado; }
    public boolean reservaTerminal() { return reservaTerminal; }
    public boolean preparoTerminal() { return preparoTerminal; }

    void reservaConfirmada() {
        if (estado == EstadoTentativa.RESERVA_PENDENTE) {
            estado = EstadoTentativa.RESERVADA;
        }
    }

    /** Reserva recusada: nada foi reservado nem preparado; ambos ja sao terminais. */
    void reservaRecusada() {
        if (estado == EstadoTentativa.RESERVA_PENDENTE) {
            reservaTerminal = true;
            preparoTerminal = true;
            estado = EstadoTentativa.ENCERRADA;
        }
    }

    void preparoConcluido() {
        if (estado == EstadoTentativa.RESERVADA) {
            estado = EstadoTentativa.CONFIRMADA;
        }
    }

    void preparoFalhou() {
        if (estado == EstadoTentativa.RESERVADA) {
            preparoTerminal = true;
            estado = EstadoTentativa.COMPENSANDO;
        }
    }

    void reservaLiberada() {
        if (estado == EstadoTentativa.COMPENSANDO) {
            reservaTerminal = true;
            if (preparoTerminal) {
                estado = EstadoTentativa.ENCERRADA;
            }
        }
    }
}
