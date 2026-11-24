package br.com.rotavital.alocacao.negocio.dominio;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Servico de dominio: reproduz a selecao do MotorCruzamento. Descarta oferta com saldo
 * menor que a quantidade, pontua (distancia + penalidade de estoque baixo) e desempata
 * pelo menor ID de fornecedor.
 */
public final class PoliticaDeRanking {

    private PoliticaDeRanking() {
    }

    public static Optional<Oferta> selecionar(List<Oferta> ofertas, int quantidade, Set<String> jaTentados) {
        return ofertas.stream()
                .filter(o -> o.quantidade() >= quantidade)
                .filter(o -> !jaTentados.contains(o.hospitalId()))
                .min(Comparator.comparing((Oferta o) -> Pontuacao.calcular(o, quantidade))
                        .thenComparing(Oferta::hospitalId));
    }
}
