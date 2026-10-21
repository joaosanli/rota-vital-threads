package br.com.rotavital.servico;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertSame;

class RepositorioDeDadosTest {
    @Test
    void mesmaQuantidadeReutilizaBaseEmCache() {
        var repositorio = new RepositorioDeDados();
        var primeira = repositorio.obter(10);
        var segunda = repositorio.obter(10);
        assertSame(primeira, segunda);
    }
}
