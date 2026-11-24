/**
 * Contexto delimitado IDENTIDADE E ACESSO (subdominio GENERICO).
 *
 * <p>Responsabilidade: autenticar operadores e informar seus papeis e permissoes aos
 * demais contextos, delegando a autenticacao a um provedor OIDC.
 *
 * <p>Linguagem ubiqua: <b>Papel</b> (perfil do operador) e <b>Permissao</b> (acao permitida).
 *
 * <p>Relacao: conformista; os demais contextos aceitam o modelo do provedor sem traduzi-lo.
 */
package br.com.rotavital.identidade;
