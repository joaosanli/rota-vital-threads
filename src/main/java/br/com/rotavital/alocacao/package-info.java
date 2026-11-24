/**
 * Contexto delimitado ALOCACAO (subdominio CORE).
 *
 * <p>Responsabilidade: ranquear as ofertas, abrir tentativas e conduzir a reserva
 * ate a confirmacao ou a compensacao.
 *
 * <p>Linguagem ubiqua deste contexto:
 * <ul>
 *   <li><b>Alocacao</b>: decisao de qual fornecedor atende uma requisicao.</li>
 *   <li><b>Tentativa</b>: uma rodada de selecao de oferta; so uma fica ativa por alocacao.</li>
 *   <li><b>Oferta</b>: candidato a fornecedor, com distancia e pontuacao
 *       (no Estoque, "oferta" e apenas saldo disponivel).</li>
 *   <li><b>Pontuacao</b>: valor que ordena as ofertas; menor e melhor.</li>
 *   <li><b>Reserva</b>: aqui e a solicitacao enviada ao Estoque, cujo resultado se aguarda
 *       (no Estoque, "reserva" e a separacao atomica das unidades).</li>
 * </ul>
 *
 * <p>Relacoes: cliente do Estoque (via camada anticorrupcao {@code EstoqueAcl}) e da
 * Expedicao (via {@code ExpedicaoAcl}); fornecedor consome o pedido da Requisicoes.
 */
package br.com.rotavital.alocacao;
