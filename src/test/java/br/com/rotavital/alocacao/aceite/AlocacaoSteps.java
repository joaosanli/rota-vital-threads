package br.com.rotavital.alocacao.aceite;

import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.EstadoAlocacao;
import br.com.rotavital.alocacao.negocio.dominio.EstadoTentativa;
import br.com.rotavital.alocacao.negocio.dominio.Oferta;
import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/** Executa o dominio diretamente: sem Spring, sem banco. */
public class AlocacaoSteps {

    private String insumo;
    private int quantidade;
    private final List<Oferta> ofertas = new ArrayList<>();
    private Alocacao alocacao;
    private Optional<SolicitacaoReserva> solicitacao = Optional.empty();
    private String primeiraTentativaId;

    @Dado("um pedido urgente de {int} unidades do insumo {word}")
    public void umPedidoUrgente(int quantidade, String insumo) {
        this.quantidade = quantidade;
        this.insumo = insumo;
    }

    @E("que {word} oferece {int} unidades a {int} km")
    public void queOferece(String hospital, int unidades, int km) {
        ofertas.add(new Oferta(hospital, unidades, km));
    }

    @Quando("a alocação ranquear as ofertas e iniciar uma tentativa")
    public void iniciar() {
        alocacao = new Alocacao("REQ-1", insumo, quantidade);
        solicitacao = alocacao.iniciarTentativa(ofertas);
        alocacao.tentativaAtiva().ifPresent(t -> primeiraTentativaId = t.id());
    }

    @Dado("uma alocação em RESERVA_PENDENTE para H-03")
    public void alocacaoReservaPendente() {
        quantidade = 10;
        insumo = "I-01";
        ofertas.add(new Oferta("H-01", 8, 5));
        ofertas.add(new Oferta("H-02", 15, 10));
        ofertas.add(new Oferta("H-03", 30, 30));
        iniciar();
        assertThat(alocacao.estado()).isEqualTo(EstadoAlocacao.RESERVA_PENDENTE);
        assertThat(alocacao.tentativaAtiva().orElseThrow().oferta().hospitalId()).isEqualTo("H-03");
    }

    @Quando("o Estoque confirmar a reserva")
    public void estoqueConfirma() {
        alocacao.registrarReservaConfirmada(primeiraTentativaId);
    }

    @E("o preparo da expedição falhar")
    public void preparoFalha() {
        alocacao.registrarPreparoFalhou(primeiraTentativaId);
    }

    @E("o Estoque liberar a reserva")
    public void estoqueLibera() {
        alocacao.registrarReservaLiberada(primeiraTentativaId);
    }

    @Então("a tentativa ativa deve selecionar {word} com pontuação {int}")
    public void tentativaSelecionou(String hospital, int pontuacao) {
        var tentativa = alocacao.tentativaAtiva().orElseThrow();
        assertThat(tentativa.oferta().hospitalId()).isEqualTo(hospital);
        assertThat(tentativa.pontuacao().valor()).isEqualTo(pontuacao);
    }

    @E("deve registrar uma solicitação de reserva de {int} unidades para {word}")
    public void solicitacaoRegistrada(int unidades, String hospital) {
        assertThat(solicitacao).isPresent();
        assertThat(solicitacao.get().quantidade()).isEqualTo(unidades);
        assertThat(solicitacao.get().hospitalId()).isEqualTo(hospital);
    }

    @Então("a alocação deve ficar em {word}")
    public void estado(String estado) {
        assertThat(alocacao.estado()).isEqualTo(EstadoAlocacao.valueOf(estado));
    }

    @E("a alocação não deve estar CONFIRMADA")
    public void naoConfirmada() {
        assertThat(alocacao.estado()).isNotEqualTo(EstadoAlocacao.CONFIRMADA);
    }

    @E("a tentativa anterior deve estar ENCERRADA")
    public void tentativaEncerrada() {
        var anterior = alocacao.tentativas().stream()
                .filter(t -> t.id().equals(primeiraTentativaId)).findFirst().orElseThrow();
        assertThat(anterior.estado()).isEqualTo(EstadoTentativa.ENCERRADA);
    }
}
