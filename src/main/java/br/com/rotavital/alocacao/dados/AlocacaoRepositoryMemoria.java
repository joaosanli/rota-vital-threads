package br.com.rotavital.alocacao.dados;

import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.AlocacaoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repositorio em memoria (dados sinteticos; sem persistencia entre reinicios). */
@Repository
public class AlocacaoRepositoryMemoria implements AlocacaoRepository {

    private final Map<String, Alocacao> porRequisicao = new ConcurrentHashMap<>();

    @Override
    public Optional<Alocacao> buscar(String requisicaoId) {
        return Optional.ofNullable(porRequisicao.get(requisicaoId));
    }

    @Override
    public void salvar(Alocacao alocacao) {
        porRequisicao.put(alocacao.requisicaoId(), alocacao);
    }
}
