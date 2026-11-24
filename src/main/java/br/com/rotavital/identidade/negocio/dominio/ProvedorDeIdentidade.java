package br.com.rotavital.identidade.negocio.dominio;

import java.util.Optional;

/** Porta: o provedor OIDC informa o papel de um sujeito autenticado. */
public interface ProvedorDeIdentidade {
    Optional<Papel> papelDe(String sujeito);
}
