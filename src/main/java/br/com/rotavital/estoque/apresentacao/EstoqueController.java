package br.com.rotavital.estoque.apresentacao;

import br.com.rotavital.estoque.negocio.aplicacao.ConsultarPosicoes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    public record PosicaoResposta(String hospitalId, String insumoId, int saldo) { }

    private final ConsultarPosicoes consultar;

    public EstoqueController(ConsultarPosicoes consultar) {
        this.consultar = consultar;
    }

    @GetMapping("/{insumoId}")
    public List<PosicaoResposta> consultar(@PathVariable String insumoId) {
        return consultar.porInsumo(insumoId).stream()
                .map(p -> new PosicaoResposta(p.hospitalId(), p.insumoId(), p.saldo())).toList();
    }
}
