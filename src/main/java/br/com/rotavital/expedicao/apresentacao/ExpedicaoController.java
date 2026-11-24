package br.com.rotavital.expedicao.apresentacao;

import br.com.rotavital.expedicao.negocio.aplicacao.ConsultarPlano;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/expedicao")
public class ExpedicaoController {

    public record PlanoResposta(String requisicaoId, String tentativaId, String fornecedorId, String insumoId,
                                int quantidade, Instant coletaInicio, Instant coletaFim, String estado) { }

    private final ConsultarPlano consultar;

    public ExpedicaoController(ConsultarPlano consultar) {
        this.consultar = consultar;
    }

    @GetMapping("/planos/{tentativaId}")
    public PlanoResposta consultar(@PathVariable String tentativaId) {
        return consultar.executar(tentativaId)
                .map(p -> new PlanoResposta(p.requisicaoId(), p.tentativaId(), p.fornecedorId(), p.insumoId(),
                        p.quantidade(), p.janela().inicio(), p.janela().fim(), p.estado().name()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plano inexistente"));
    }
}
