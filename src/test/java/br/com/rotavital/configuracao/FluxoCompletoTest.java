package br.com.rotavital.configuracao;

import br.com.rotavital.alocacao.dados.AlocacaoRepositoryMemoria;
import br.com.rotavital.alocacao.dados.EstoqueAcl;
import br.com.rotavital.alocacao.dados.ExpedicaoAcl;
import br.com.rotavital.alocacao.negocio.aplicacao.ConfirmarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.IniciarAlocacao;
import br.com.rotavital.alocacao.negocio.dominio.Alocacao;
import br.com.rotavital.alocacao.negocio.dominio.EstadoAlocacao;
import br.com.rotavital.estoque.dados.EstoqueRepositoryMemoria;
import br.com.rotavital.estoque.negocio.aplicacao.ConsultarPosicoes;
import br.com.rotavital.estoque.negocio.aplicacao.LiberarReserva;
import br.com.rotavital.estoque.negocio.aplicacao.ReservarInsumo;
import br.com.rotavital.expedicao.dados.PlanoRepositoryMemoria;
import br.com.rotavital.expedicao.negocio.aplicacao.CancelarTransferencia;
import br.com.rotavital.expedicao.negocio.aplicacao.PrepararTransferencia;
import br.com.rotavital.expedicao.negocio.dominio.PlanoTransferencia;
import br.com.rotavital.requisicoes.dados.RequisicaoRepositoryMemoria;
import br.com.rotavital.requisicoes.negocio.aplicacao.RegistrarRequisicao;
import br.com.rotavital.requisicoes.negocio.dominio.Prioridade;
import br.com.rotavital.requisicoes.negocio.dominio.Requisicao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/** Fluxo atravessando os contextos com a composicao real (sem Spring e sem banco). */
class FluxoCompletoTest {

    private EstoqueRepositoryMemoria estoqueRepo;
    private PlanoRepositoryMemoria planoRepo;
    private RegistrarRequisicao registrar;
    private IniciarAlocacao iniciar;
    private ConfirmarAlocacao confirmar;
    private EstoqueAcl estoqueAcl;

    @BeforeEach
    void montar() {
        ContextosConfig cfg = new ContextosConfig();
        Clock relogio = Clock.fixed(Instant.parse("2026-11-24T12:00:00Z"), ZoneOffset.UTC);
        estoqueRepo = new EstoqueRepositoryMemoria();
        planoRepo = new PlanoRepositoryMemoria();
        var alocacaoRepo = new AlocacaoRepositoryMemoria();
        var consultar = new ConsultarPosicoes(estoqueRepo);
        estoqueAcl = new EstoqueAcl(consultar, new ReservarInsumo(estoqueRepo), new LiberarReserva(estoqueRepo));
        var expedicaoAcl = new ExpedicaoAcl(new PrepararTransferencia(planoRepo, relogio),
                new CancelarTransferencia(planoRepo));
        registrar = cfg.registrarRequisicao(new RequisicaoRepositoryMemoria());
        iniciar = cfg.iniciarAlocacao(alocacaoRepo, estoqueAcl);
        confirmar = cfg.confirmarAlocacao(alocacaoRepo, estoqueAcl, estoqueAcl, expedicaoAcl);
    }

    private int saldo(String hospital) {
        return estoqueRepo.buscar(hospital, "I-01").orElseThrow().saldo();
    }

    @Test
    void requisicaoUrgenteViraAlocacaoConfirmadaComReservaEPlano() {
        Requisicao req = registrar.executar("H-09", "I-01", 10, Prioridade.URGENTE);
        Alocacao a = iniciar.executar(req.id(), req.insumoId(), req.quantidade());
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.RESERVA_PENDENTE);
        assertThat(saldo("H-03")).isEqualTo(30); // nada reservado ate confirmar

        a = confirmar.executar(req.id());

        assertThat(a.estado()).isEqualTo(EstadoAlocacao.CONFIRMADA);
        assertThat(saldo("H-03")).isEqualTo(20);
        PlanoTransferencia plano = planoRepo.buscar(a.tentativas().get(0).id()).orElseThrow();
        assertThat(plano.estado()).isEqualTo(PlanoTransferencia.Estado.PREPARADO);
        assertThat(plano.fornecedorId()).isEqualTo("H-03");
    }

    @Test
    void semOfertaSuficienteTerminaSemReservar() {
        Requisicao req = registrar.executar("H-09", "I-01", 50, Prioridade.URGENTE);
        iniciar.executar(req.id(), req.insumoId(), req.quantidade());
        Alocacao a = confirmar.executar(req.id());
        assertThat(a.estado()).isEqualTo(EstadoAlocacao.SEM_CANDIDATO);
        assertThat(saldo("H-03")).isEqualTo(30);
    }

    @Test
    void falhaNoPreparoCompensaELiberaReservaEUsaProximaOferta() {
        // expedicao que falha na primeira tentativa e funciona nas demais
        var falhaUmaVez = new br.com.rotavital.alocacao.negocio.aplicacao.PreparoDeExpedicao() {
            boolean primeira = true;
            final ExpedicaoAcl real = new ExpedicaoAcl(
                    new PrepararTransferencia(planoRepo, Clock.systemUTC()), new CancelarTransferencia(planoRepo));

            @Override
            public boolean preparar(br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva s) {
                if (primeira) {
                    primeira = false;
                    real.preparar(s);   // plano chegou a existir
                    return false;       // mas o preparo falhou
                }
                return real.preparar(s);
            }

            @Override
            public void cancelar(br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva s) {
                real.cancelar(s);
            }
        };
        var alocacaoRepo = new AlocacaoRepositoryMemoria();
        var cfg = new ContextosConfig();
        var iniciar2 = cfg.iniciarAlocacao(alocacaoRepo, estoqueAcl);
        var confirmar2 = cfg.confirmarAlocacao(alocacaoRepo, estoqueAcl, estoqueAcl, falhaUmaVez);

        iniciar2.executar("REQ-X", "I-01", 10);
        Alocacao a = confirmar2.executar("REQ-X");

        assertThat(a.estado()).isEqualTo(EstadoAlocacao.CONFIRMADA);
        assertThat(a.tentativas()).hasSize(2);
        assertThat(a.tentativas().get(0).oferta().hospitalId()).isEqualTo("H-03");
        assertThat(saldo("H-03")).isEqualTo(30);                  // reserva da 1a tentativa liberada
        assertThat(a.tentativas().get(1).oferta().hospitalId()).isEqualTo("H-02");
        assertThat(saldo("H-02")).isEqualTo(5);                   // 2a tentativa reservou 10 de 15
        assertThat(planoRepo.buscar("REQ-X-T1").orElseThrow().estado())
                .isEqualTo(PlanoTransferencia.Estado.CANCELADO);
    }

    @Test
    void iniciarEIdempotentePorRequisicao() {
        Alocacao a1 = iniciar.executar("REQ-7", "I-01", 10);
        Alocacao a2 = iniciar.executar("REQ-7", "I-01", 10);
        assertThat(a2).isSameAs(a1);
        assertThat(a1.tentativas()).hasSize(1);
    }
}
