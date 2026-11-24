package br.com.rotavital.requisicoes.dados;

import br.com.rotavital.requisicoes.negocio.dominio.Requisicao;
import br.com.rotavital.requisicoes.negocio.dominio.RequisicaoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RequisicaoRepositoryMemoria implements RequisicaoRepository {

    private final Map<String, Requisicao> requisicoes = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    @Override
    public void salvar(Requisicao requisicao) {
        requisicoes.put(requisicao.id(), requisicao);
    }

    @Override
    public Optional<Requisicao> buscar(String id) {
        return Optional.ofNullable(requisicoes.get(id));
    }

    @Override
    public long proximoNumero() {
        return sequencia.incrementAndGet();
    }
}
