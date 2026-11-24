/**
 * Contexto delimitado ESTOQUE (subdominio de SUPORTE).
 *
 * <p>Responsabilidade: ser a autoridade sobre saldos e executar reservas e liberacoes
 * de forma atomica.
 *
 * <p>Linguagem ubiqua: <b>Insumo</b> (item controlado, ex.: I-01); <b>Posicao de estoque</b>
 * (saldo de um insumo em um hospital); <b>Oferta</b> (aqui, quantidade disponivel em um
 * hospital); <b>Reserva</b> (separacao atomica de unidades, que reduz o saldo disponivel).
 *
 * <p>Relacao: fornecedor da Alocacao; a traducao para o modelo da Alocacao e feita pela
 * camada anticorrupcao {@code EstoqueAcl}.
 */
package br.com.rotavital.estoque;
