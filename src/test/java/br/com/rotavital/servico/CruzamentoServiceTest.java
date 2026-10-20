package br.com.rotavital.servico;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CruzamentoServiceTest {
    @Test
    void modoInvalidoNaoDeveGerarBase() {
        var repositorio = new RepositorioDeDados();
        var servico = new CruzamentoService(repositorio);
        assertThrows(IllegalArgumentException.class, () -> servico.executar("inexistente", 4, 10));
        assertTrue(repositorio.todas().isEmpty(), "Rejeitar modo antes de alocar a base");
    }

    @Test
    void threadsInvalidasNaoDevemGerarBase() {
        var repositorio = new RepositorioDeDados();
        var servico = new CruzamentoService(repositorio);
        assertThrows(IllegalArgumentException.class, () -> servico.executar("plataforma", 3, 10));
        assertTrue(repositorio.todas().isEmpty());
    }
}
