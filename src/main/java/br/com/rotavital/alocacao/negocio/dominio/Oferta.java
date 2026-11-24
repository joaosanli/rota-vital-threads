package br.com.rotavital.alocacao.negocio.dominio;

/** Objeto de valor: candidato a fornecedor, com quantidade disponivel e distancia. */
public record Oferta(String hospitalId, int quantidade, int distanciaKm) {

    public Oferta {
        if (hospitalId == null || hospitalId.isBlank()) {
            throw new IllegalArgumentException("hospitalId obrigatorio");
        }
        if (quantidade < 0 || distanciaKm < 0) {
            throw new IllegalArgumentException("quantidade e distancia nao podem ser negativas");
        }
    }
}
