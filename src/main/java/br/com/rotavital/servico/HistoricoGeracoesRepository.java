package br.com.rotavital.servico;

import br.com.rotavital.dominio.BaseDeDados;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * E1: persiste metadados das geracoes no PostgreSQL. Os arrays usados no benchmark
 * continuam em memoria para nao incluir latencia de I/O nos calculos de Threads II.
 */
@Repository
public class HistoricoGeracoesRepository {
    private final JdbcTemplate jdbc;

    public HistoricoGeracoesRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void registrar(BaseDeDados base, boolean jaEstavaEmCache) {
        jdbc.update("""
                INSERT INTO historico_geracoes
                    (requisicoes, hospitais, insumos, semente, ja_estava_em_cache)
                VALUES (?, ?, ?, ?, ?)
                """,
                base.numRequisicoes, base.numHospitais, base.numInsumos,
                base.seed, jaEstavaEmCache);
    }

    public List<Map<String, Object>> ultimasGeracoes() {
        return jdbc.query("""
                SELECT id, requisicoes, hospitais, insumos, semente,
                       ja_estava_em_cache, criado_em
                FROM historico_geracoes
                ORDER BY id DESC
                LIMIT 20
                """, (rs, numero) -> Map.<String, Object>of(
                "id", rs.getLong("id"),
                "requisicoes", rs.getInt("requisicoes"),
                "hospitais", rs.getInt("hospitais"),
                "insumos", rs.getInt("insumos"),
                "seed", rs.getLong("semente"),
                "jaEstavaEmCache", rs.getBoolean("ja_estava_em_cache"),
                "criadoEm", rs.getTimestamp("criado_em").toInstant().toString()
        ));
    }
}
