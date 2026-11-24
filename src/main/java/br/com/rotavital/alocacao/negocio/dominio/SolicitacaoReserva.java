package br.com.rotavital.alocacao.negocio.dominio;

/** Pedido de reserva ao Estoque, correlacionado por requisicaoId e tentativaId. */
public record SolicitacaoReserva(String requisicaoId, String tentativaId, String hospitalId,
                                 String insumoId, int quantidade) {
}
