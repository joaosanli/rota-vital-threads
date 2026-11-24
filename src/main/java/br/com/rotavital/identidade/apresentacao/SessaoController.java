package br.com.rotavital.identidade.apresentacao;

import br.com.rotavital.identidade.negocio.aplicacao.ConsultarPermissoes;
import br.com.rotavital.identidade.negocio.dominio.Permissao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/sessao")
public class SessaoController {

    private final ConsultarPermissoes consultar;

    public SessaoController(ConsultarPermissoes consultar) {
        this.consultar = consultar;
    }

    @GetMapping("/permissoes")
    public Set<Permissao> permissoes(@RequestParam String sujeito) {
        return consultar.executar(sujeito);
    }
}
