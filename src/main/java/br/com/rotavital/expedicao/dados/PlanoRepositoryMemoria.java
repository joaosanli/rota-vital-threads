package br.com.rotavital.expedicao.dados;

import br.com.rotavital.expedicao.negocio.dominio.PlanoRepository;
import br.com.rotavital.expedicao.negocio.dominio.PlanoTransferencia;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PlanoRepositoryMemoria implements PlanoRepository {

    private final Map<String, PlanoTransferencia> planos = new ConcurrentHashMap<>();

    @Override
    public Optional<PlanoTransferencia> buscar(String tentativaId) {
        return Optional.ofNullable(planos.get(tentativaId));
    }

    @Override
    public void salvar(PlanoTransferencia plano) {
        planos.put(plano.tentativaId(), plano);
    }
}
