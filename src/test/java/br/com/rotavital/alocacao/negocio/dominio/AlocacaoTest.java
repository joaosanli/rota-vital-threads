package br.com.rotavital.alocacao.negocio.dominio;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlocacaoTest {

    private static final List<Oferta> OFERTAS = List.of(
            new Oferta("H-01", 8, 5), new Oferta("H-02", 15, 10), new Oferta("H-03", 30, 30));

    private Alocacao nova() {
        return new Alocacao("REQ-1", "I-01", 10);
    }

    @Test
    void invariante1_descartaOfertaInsuficiente() {
        Alocacao a = nova();
        var s = a.iniciarTentativa(List.of(new Oferta("H-01", 8, 5)));
        assertThat(s).isEmpty();
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.SEM_CANDIDATO);
    }

    @Test
    void invariante3_escolheMenorPontuacaoEReservaQuantidadeExata() {
        Alocacao a = nova();
        SolicitacaoReserva s = a.iniciarTentativa(OFERTAS).orElseThrow();
        assertThat(s.hospitalId()).isEqualTo("H-03");
        assertThat(s.quantidade()).isEqualTo(10);
        assertThat(a.tentativaAtiva().orElseThrow().pontuacao().valor()).isEqualTo(30);
    }

    @Test
    void invariante2_naoAbreSegundaTentativaComUmaAtiva() {
        Alocacao a = nova();
        a.iniciarTentativa(OFERTAS);
        assertThatThrownBy(() -> a.iniciarTentativa(OFERTAS)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void invariante4_soConfirmaAposReservaEPreparo() {
        Alocacao a = nova();
        String t = a.iniciarTentativa(OFERTAS).orElseThrow().tentativaId();
        a.registrarPreparoConcluido(t); // fora de ordem: ignorado
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.RESERVA_PENDENTE);
        a.registrarReservaConfirmada(t);
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.PREPARO_PENDENTE);
        a.registrarPreparoConcluido(t);
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.CONFIRMADA);
    }

    @Test
    void invariante5_novaTentativaSoAposEstadosTerminais() {
        Alocacao a = nova();
        String t = a.iniciarTentativa(OFERTAS).orElseThrow().tentativaId();
        a.registrarReservaConfirmada(t);
        a.registrarPreparoFalhou(t);
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.COMPENSANDO);
        assertThatThrownBy(() -> a.iniciarTentativa(OFERTAS)).isInstanceOf(IllegalStateException.class);
        a.registrarReservaLiberada(t);
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.ABERTA);

        // a proxima tentativa nao repete o hospital ja tentado
        SolicitacaoReserva proxima = a.iniciarTentativa(OFERTAS).orElseThrow();
        assertThat(proxima.hospitalId()).isEqualTo("H-02");
    }

    @Test
    void comandoAtrasadoNaoReabreTentativaEncerrada() {
        Alocacao a = nova();
        String t = a.iniciarTentativa(OFERTAS).orElseThrow().tentativaId();
        a.registrarReservaRecusada(t);
        a.registrarReservaConfirmada(t); // atrasado
        assertThat(a.tentativas().get(0).estado()).isEqualTo(EstadoTentativa.ENCERRADA);
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.ABERTA);
    }

    @Test
    void estoqueBaixoRecebePenalidadeDe80() {
        assertThat(Pontuacao.calcular(new Oferta("H-02", 15, 10), 10).valor()).isEqualTo(90);
        assertThat(Pontuacao.calcular(new Oferta("H-03", 30, 30), 10).valor()).isEqualTo(30);
    }

    @Test
    void empateDePontuacaoVaiParaOMenorId() {
        Alocacao a = nova();
        var s = a.iniciarTentativa(List.of(new Oferta("H-09", 30, 20), new Oferta("H-04", 30, 20)));
        assertThat(s.orElseThrow().hospitalId()).isEqualTo("H-04");
    }

    @Test
    void quantidadeNaoPositivaERejeitada() {
        assertThatThrownBy(() -> new Alocacao("REQ-1", "I-01", 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
