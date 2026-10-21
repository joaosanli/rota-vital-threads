package br.com.rotavital.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIT {
    @Autowired TestRestTemplate http;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach void limparBancoDeTeste() {
        jdbc.execute("TRUNCATE TABLE historico_geracoes RESTART IDENTITY");
    }

    @Test void gerarDadosRegistraNoBanco() {
        var resposta = http.postForEntity("/api/dados/gerar?requisicoes=37", null, Map.class);
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(37, resposta.getBody().get("requisicoes"));
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM historico_geracoes WHERE requisicoes=37", Integer.class));
        assertEquals(HttpStatus.BAD_REQUEST, http.postForEntity("/api/dados/gerar?requisicoes=0", null, Map.class).getStatusCode());
        assertEquals(1, jdbc.queryForObject("SELECT count(*) FROM historico_geracoes", Integer.class));
    }

    @Test void compararModosMantemResultados() {
        Map referencia = http.getForObject("/api/cruzamento?modo=sequencial&requisicoes=41", Map.class);
        for (String modo : List.of("plataforma", "virtual")) {
            Map resultado = http.getForObject("/api/cruzamento?modo=" + modo + "&threads=4&requisicoes=41", Map.class);
            for (String campo : List.of("checksum", "assinatura", "atendiveis", "naoAtendiveis", "porUf", "porPrioridade")) {
                assertEquals(referencia.get(campo), resultado.get(campo));
            }
        }
        assertEquals(HttpStatus.BAD_REQUEST, http.getForEntity("/api/cruzamento?modo=inexistente&requisicoes=41", Map.class).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, http.getForEntity("/api/cruzamento?threads=3&requisicoes=41", Map.class).getStatusCode());
    }

    @Test void consultarHistoricoRetornaVinteMaisRecentes() {
        for (int i = 0; i < 22; i++) {
            assertEquals(HttpStatus.OK, http.postForEntity("/api/dados/gerar?requisicoes=43", null, Map.class).getStatusCode());
        }
        List<Map<String,Object>> registros = http.getForObject("/api/historico/geracoes", List.class);
        assertEquals(20, registros.size());
        assertEquals(22, registros.getFirst().get("id"));
        assertEquals(3, registros.getLast().get("id"));
        assertEquals(true, registros.getFirst().get("jaEstavaEmCache"));
        assertEquals(22, jdbc.queryForObject("SELECT count(*) FROM historico_geracoes", Integer.class));
    }
}
