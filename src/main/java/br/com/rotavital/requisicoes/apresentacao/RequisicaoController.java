package br.com.rotavital.requisicoes.apresentacao;

import br.com.rotavital.requisicoes.negocio.aplicacao.ConsultarRequisicao;
import br.com.rotavital.requisicoes.negocio.aplicacao.RegistrarRequisicao;
import br.com.rotavital.requisicoes.negocio.dominio.Prioridade;
import br.com.rotavital.requisicoes.negocio.dominio.Requisicao;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/requisicoes")
public class RequisicaoController {

    public record RegistrarRequest(String hospitalSolicitante, String insumoId, int quantidade, Prioridade prioridade) { }

    private final RegistrarRequisicao registrar;
    private final ConsultarRequisicao consultar;

    public RequisicaoController(RegistrarRequisicao registrar, ConsultarRequisicao consultar) {
        this.registrar = registrar;
        this.consultar = consultar;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Requisicao registrar(@RequestBody RegistrarRequest req) {
        try {
            return registrar.executar(req.hospitalSolicitante(), req.insumoId(), req.quantidade(), req.prioridade());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Requisicao consultar(@PathVariable String id) {
        return consultar.executar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requisicao inexistente"));
    }
}
