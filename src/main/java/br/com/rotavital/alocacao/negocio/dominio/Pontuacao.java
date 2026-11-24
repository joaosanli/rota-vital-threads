package br.com.rotavital.alocacao.negocio.dominio;

/** Objeto de valor: ordena as ofertas; menor e melhor. */
public record Pontuacao(int valor) implements Comparable<Pontuacao> {

    /** Penalidade (em km equivalentes) para quem ficaria com estoque baixo apos atender. */
    public static final int PENALIDADE_ESTOQUE_BAIXO = 30;

    /** O estoque e baixo quando a oferta nao chega ao dobro do solicitado. */
    public static Pontuacao calcular(Oferta oferta, int quantidadeSolicitada) {
        boolean estoqueBaixo = oferta.quantidade() < 2 * quantidadeSolicitada;
        return new Pontuacao(oferta.distanciaKm() + (estoqueBaixo ? PENALIDADE_ESTOQUE_BAIXO : 0));
    }

    @Override
    public int compareTo(Pontuacao outra) {
        return Integer.compare(valor, outra.valor);
    }
}
