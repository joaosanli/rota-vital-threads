package br.com.rotavital.alocacao.apresentacao;

import br.com.rotavital.alocacao.negocio.aplicacao.ConfirmarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.ConsultarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.IniciarAlocacao;
import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/alocacoes")
public class AlocacaoController {

    public record IniciarRequest(String requisicaoId, String insumoId, int quantidade) { }

    public record TentativaResposta(String id, String hospitalId, int pontuacao, int quantidade, String estado) { }

    public record AlocacaoResposta(String requisicaoId, String insumoId, int quantidade,
                                   String estado, List<TentativaResposta> tentativas) { }

    private final IniciarAlocacao iniciar;
    private final ConfirmarAlocacao confirmar;
    private final ConsultarAlocacao consultar;

    public AlocacaoController(IniciarAlocacao iniciar, ConfirmarAlocacao confirmar, ConsultarAlocacao consultar) {
        this.iniciar = iniciar;
        this.confirmar = confirmar;
        this.consultar = consultar;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlocacaoResposta iniciar(@RequestBody IniciarRequest req) {
        try {
            return resposta(iniciar.executar(req.requisicaoId(), req.insumoId(), req.quantidade()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{requisicaoId}/confirmar")
    public AlocacaoResposta confirmar(@PathVariable String requisicaoId) {
        try {
            return resposta(confirmar.executar(requisicaoId));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{requisicaoId}")
    public AlocacaoResposta consultar(@PathVariable String requisicaoId) {
        return consultar.executar(requisicaoId).map(AlocacaoController::resposta)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alocacao inexistente"));
    }

    private static AlocacaoResposta resposta(Alocacao a) {
        return new AlocacaoResposta(a.requisicaoId(), a.insumoId(), a.quantidadeSolicitada(),
                a.estado().name(),
                a.tentativas().stream().map(t -> new TentativaResposta(t.id(), t.oferta().hospitalId(),
                        t.pontuacao().valor(), t.quantidade(), t.estado().name())).toList());
    }
}
