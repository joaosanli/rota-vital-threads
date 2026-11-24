package br.com.rotavital.servico;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositorioDeDadosTest {
    @Test
    void mesmaQuantidadeReutilizaBaseEmCache() {
        var repositorio = new RepositorioDeDados();
        var primeira = repositorio.obter(10);
        var segunda = repositorio.obter(10);
        assertSame(primeira, segunda);
    }

    @Test
    void mesmaSementeReproduzPedidosEEstoqueSemCacheCompartilhado() {
        var primeira = new RepositorioDeDados().obter(40);
        var segunda = new RepositorioDeDados().obter(40);
        assertEquals(42L, primeira.seed);
        assertEquals(primeira.seed, segunda.seed);
        assertArrayEquals(primeira.reqQuantidade, segunda.reqQuantidade);
        assertArrayEquals(primeira.reqInsumo, segunda.reqInsumo);
        assertArrayEquals(primeira.estoque, segunda.estoque);
    }
}
