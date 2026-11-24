package br.com.rotavital.alocacao.negocio.aplicacao;

import br.com.rotavital.alocacao.negocio.dominio.Oferta;

import java.util.List;

/** Porta: ofertas candidatas para um insumo, ja no modelo da Alocacao. */
public interface OfertasDisponiveis {
    List<Oferta> para(String insumoId);
}
