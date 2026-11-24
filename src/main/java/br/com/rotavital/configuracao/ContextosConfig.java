package br.com.rotavital.configuracao;

import br.com.rotavital.alocacao.negocio.aplicacao.ConfirmarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.ConsultarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.IniciarAlocacao;
import br.com.rotavital.alocacao.negocio.aplicacao.OfertasDisponiveis;
import br.com.rotavital.alocacao.negocio.aplicacao.PreparoDeExpedicao;
import br.com.rotavital.alocacao.negocio.aplicacao.ReservaDeEstoque;
import br.com.rotavital.alocacao.negocio.dominio.AlocacaoRepository;
import br.com.rotavital.estoque.negocio.aplicacao.ConsultarPosicoes;
import br.com.rotavital.estoque.negocio.aplicacao.LiberarReserva;
import br.com.rotavital.estoque.negocio.aplicacao.ReservarInsumo;
import br.com.rotavital.estoque.negocio.dominio.EstoqueRepository;
import br.com.rotavital.expedicao.negocio.aplicacao.CancelarTransferencia;
import br.com.rotavital.expedicao.negocio.aplicacao.ConsultarPlano;
import br.com.rotavital.expedicao.negocio.aplicacao.PrepararTransferencia;
import br.com.rotavital.expedicao.negocio.dominio.PlanoRepository;
import br.com.rotavital.identidade.negocio.aplicacao.ConsultarPermissoes;
import br.com.rotavital.identidade.negocio.dominio.ProvedorDeIdentidade;
import br.com.rotavital.requisicoes.negocio.aplicacao.ConsultarRequisicao;
import br.com.rotavital.requisicoes.negocio.aplicacao.RegistrarRequisicao;
import br.com.rotavital.requisicoes.negocio.dominio.RequisicaoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Composicao: instancia os casos de uso (que nao conhecem Spring) e liga as portas
 * as implementacoes da camada de dados. Unico lugar que enxerga todos os contextos.
 */
@Configuration
public class ContextosConfig {

    @Bean
    Clock relogio() {
        return Clock.systemUTC();
    }

    // Alocacao
    @Bean
    IniciarAlocacao iniciarAlocacao(AlocacaoRepository repo, OfertasDisponiveis ofertas) {
        return new IniciarAlocacao(repo, ofertas);
    }

    @Bean
    ConfirmarAlocacao confirmarAlocacao(AlocacaoRepository repo, OfertasDisponiveis ofertas,
                                        ReservaDeEstoque estoque, PreparoDeExpedicao expedicao) {
        return new ConfirmarAlocacao(repo, ofertas, estoque, expedicao);
    }

    @Bean
    ConsultarAlocacao consultarAlocacao(AlocacaoRepository repo) {
        return new ConsultarAlocacao(repo);
    }

    // Estoque
    @Bean
    ConsultarPosicoes consultarPosicoes(EstoqueRepository repo) {
        return new ConsultarPosicoes(repo);
    }

    @Bean
    ReservarInsumo reservarInsumo(EstoqueRepository repo) {
        return new ReservarInsumo(repo);
    }

    @Bean
    LiberarReserva liberarReserva(EstoqueRepository repo) {
        return new LiberarReserva(repo);
    }

    // Expedicao
    @Bean
    PrepararTransferencia prepararTransferencia(PlanoRepository repo, Clock relogio) {
        return new PrepararTransferencia(repo, relogio);
    }

    @Bean
    CancelarTransferencia cancelarTransferencia(PlanoRepository repo) {
        return new CancelarTransferencia(repo);
    }

    @Bean
    ConsultarPlano consultarPlano(PlanoRepository repo) {
        return new ConsultarPlano(repo);
    }

    // Requisicoes
    @Bean
    RegistrarRequisicao registrarRequisicao(RequisicaoRepository repo) {
        return new RegistrarRequisicao(repo);
    }

    @Bean
    ConsultarRequisicao consultarRequisicao(RequisicaoRepository repo) {
        return new ConsultarRequisicao(repo);
    }

    // Identidade
    @Bean
    ConsultarPermissoes consultarPermissoes(ProvedorDeIdentidade provedor) {
        return new ConsultarPermissoes(provedor);
    }
}
