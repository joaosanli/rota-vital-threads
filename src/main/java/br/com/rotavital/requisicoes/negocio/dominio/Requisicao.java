package br.com.rotavital.requisicoes.negocio.dominio;

/** Pedido de uma quantidade de um insumo por um hospital solicitante. */
public record Requisicao(String id, String hospitalSolicitante, String insumoId,
                         int quantidade, Prioridade prioridade) {

    public Requisicao {
        if (id == null || id.isBlank() || hospitalSolicitante == null || hospitalSolicitante.isBlank()
                || insumoId == null || insumoId.isBlank()) {
            throw new IllegalArgumentException("id, hospital solicitante e insumo sao obrigatorios");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade deve ser positiva");
        }
        if (prioridade == null) {
            throw new IllegalArgumentException("prioridade obrigatoria");
        }
    }
}
