package br.com.rotavital.identidade.negocio.dominio;

import java.util.EnumSet;
import java.util.Set;

import static br.com.rotavital.identidade.negocio.dominio.Permissao.*;

/** Perfil do operador e as permissoes que ele concede. */
public enum Papel {
    OPERADOR(EnumSet.of(CONSULTAR_ESTOQUE, REGISTRAR_REQUISICAO)),
    COORDENADOR(EnumSet.of(CONSULTAR_ESTOQUE, REGISTRAR_REQUISICAO, INICIAR_ALOCACAO, CONFIRMAR_ALOCACAO)),
    ADMINISTRADOR(EnumSet.allOf(Permissao.class));

    private final Set<Permissao> permissoes;

    Papel(Set<Permissao> permissoes) {
        this.permissoes = permissoes;
    }

    public Set<Permissao> permissoes() {
        return EnumSet.copyOf(permissoes);
    }

    public boolean permite(Permissao permissao) {
        return permissoes.contains(permissao);
    }
}
