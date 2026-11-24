package br.com.rotavital.alocacao.dados;

import br.com.rotavital.alocacao.negocio.aplicacao.PreparoDeExpedicao;
import br.com.rotavital.alocacao.negocio.dominio.SolicitacaoReserva;
import br.com.rotavital.expedicao.negocio.aplicacao.CancelarTransferencia;
import br.com.rotavital.expedicao.negocio.aplicacao.PrepararTransferencia;
import org.springframework.stereotype.Component;

/** Camada anticorrupcao: traduz a solicitacao da Alocacao para o plano de transferencia. */
@Component
public class ExpedicaoAcl implements PreparoDeExpedicao {

    private final PrepararTransferencia preparar;
    private final CancelarTransferencia cancelar;

    public ExpedicaoAcl(PrepararTransferencia preparar, CancelarTransferencia cancelar) {
        this.preparar = preparar;
        this.cancelar = cancelar;
    }

    @Override
    public boolean preparar(SolicitacaoReserva s) {
        return preparar.executar(s.requisicaoId(), s.tentativaId(), s.hospitalId(), s.insumoId(), s.quantidade());
    }

    @Override
    public void cancelar(SolicitacaoReserva s) {
        cancelar.executar(s.tentativaId());
    }
}
