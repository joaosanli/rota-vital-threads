package br.com.rotavital.estoque.dados;

import br.com.rotavital.estoque.negocio.dominio.EstoqueRepository;
import br.com.rotavital.estoque.negocio.dominio.PosicaoEstoque;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio em memoria com saldos sinteticos do Rota Vital. */
@Repository
public class EstoqueRepositoryMemoria implements EstoqueRepository {

    private final Map<String, PosicaoEstoque> posicoes = new ConcurrentHashMap<>();

    public EstoqueRepositoryMemoria() {
        adicionar(new PosicaoEstoque("H-01", "I-01", 8));
        adicionar(new PosicaoEstoque("H-02", "I-01", 15));
        adicionar(new PosicaoEstoque("H-03", "I-01", 30));
    }

    private void adicionar(PosicaoEstoque p) {
        posicoes.put(chave(p.hospitalId(), p.insumoId()), p);
    }

    private static String chave(String hospitalId, String insumoId) {
        return hospitalId + "|" + insumoId;
    }

    @Override
    public List<PosicaoEstoque> porInsumo(String insumoId) {
        return posicoes.values().stream().filter(p -> p.insumoId().equals(insumoId))
                .sorted(java.util.Comparator.comparing(PosicaoEstoque::hospitalId)).toList();
    }

    @Override
    public Optional<PosicaoEstoque> buscar(String hospitalId, String insumoId) {
        return Optional.ofNullable(posicoes.get(chave(hospitalId, insumoId)));
    }
}
