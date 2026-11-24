package br.com.rotavital.expedicao.negocio.dominio;

/**
 * Raiz do agregado da Expedicao. Invariante: um plano cancelado nunca volta a PREPARADO
 * (comando atrasado nao reabre a tentativa).
 */
public class PlanoTransferencia {

    public enum Estado { PREPARADO, CANCELADO }

    private final String requisicaoId;
    private final String tentativaId;
    private final String fornecedorId;
    private final String insumoId;
    private final int quantidade;
    private final JanelaColeta janela;
    private Estado estado = Estado.PREPARADO;

    public PlanoTransferencia(String requisicaoId, String tentativaId, String fornecedorId,
                              String insumoId, int quantidade, JanelaColeta janela) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser positiva");
        }
        this.requisicaoId = requisicaoId;
        this.tentativaId = tentativaId;
        this.fornecedorId = fornecedorId;
        this.insumoId = insumoId;
        this.quantidade = quantidade;
        this.janela = janela;
    }

    public void cancelar() {
        estado = Estado.CANCELADO;
    }

    public String requisicaoId() { return requisicaoId; }
    public String tentativaId() { return tentativaId; }
    public String fornecedorId() { return fornecedorId; }
    public String insumoId() { return insumoId; }
    public int quantidade() { return quantidade; }
    public JanelaColeta janela() { return janela; }
    public Estado estado() { return estado; }
}
