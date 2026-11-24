package br.com.rotavital.identidade.negocio.aplicacao;

import br.com.rotavital.identidade.negocio.dominio.Permissao;
import br.com.rotavital.identidade.negocio.dominio.ProvedorDeIdentidade;

import java.util.EnumSet;
import java.util.Set;

/** Caso de uso: permissoes de um sujeito (vazio se desconhecido). */
public class ConsultarPermissoes {

    private final ProvedorDeIdentidade provedor;

    public ConsultarPermissoes(ProvedorDeIdentidade provedor) {
        this.provedor = provedor;
    }

    public Set<Permissao> executar(String sujeito) {
        return provedor.papelDe(sujeito).map(p -> p.permissoes()).orElse(EnumSet.noneOf(Permissao.class));
    }
}
