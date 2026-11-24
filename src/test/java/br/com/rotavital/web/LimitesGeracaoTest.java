package br.com.rotavital.web;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class LimitesGeracaoTest {
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 5_000_001})
    void rejeitaTamanhoAntesDeGerarDados(int quantidade) {
        var controller = new CruzamentoController(null, null, null);
        var erro = assertThrows(ResponseStatusException.class, () -> controller.gerar(quantidade));
        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }
}
