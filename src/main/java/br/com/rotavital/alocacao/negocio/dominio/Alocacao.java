package br.com.rotavital.alocacao.negocio.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Raiz do agregado Alocacao (contexto core).
 *
 * <p>Invariantes protegidas:
 * <ol>
 *   <li>so entram no ranking ofertas com quantidade maior ou igual a solicitada;</li>
 *   <li>existe no maximo uma tentativa ativa;</li>
 *   <li>a tentativa ativa escolhe a oferta de menor pontuacao e reserva exatamente a
 *       quantidade solicitada;</li>
 *   <li>so confirma depois de reserva confirmada E preparo concluido;</li>
 *   <li>nova tentativa so comeca com reserva e preparo da anterior em estado terminal;
 *       comandos atrasados sobre tentativa terminal sao ignorados.</li>
 * </ol>
 */
public class Alocacao {

    private final String requisicaoId;
    private final String insumoId;
    private final int quantidadeSolicitada;
    private final List<Tentativa> tentativas = new ArrayList<>();
    private EstadoAlocacao estado = EstadoAlocacao.ABERTA;

    public Alocacao(String requisicaoId, String insumoId, int quantidadeSolicitada) {
        if (requisicaoId == null || requisicaoId.isBlank() || insumoId == null || insumoId.isBlank()) {
            throw new IllegalArgumentException("requisicaoId e insumoId obrigatorios");
        }
        if (quantidadeSolicitada <= 0) {
            throw new IllegalArgumentException("quantidade solicitada deve ser positiva");
        }
        this.requisicaoId = requisicaoId;
        this.insumoId = insumoId;
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    /** Ranqueia as ofertas e abre uma tentativa com a melhor. Retorna a solicitacao de reserva. */
    public Optional<SolicitacaoReserva> iniciarTentativa(List<Oferta> ofertas) {
        if (estado != EstadoAlocacao.ABERTA) {
            throw new IllegalStateException("Nao e possivel iniciar tentativa em " + estado);
        }
        Set<String> jaTentados = new HashSet<>();
        tentativas.forEach(t -> jaTentados.add(t.oferta().hospitalId()));

        Optional<Oferta> melhor = PoliticaDeRanking.selecionar(ofertas, quantidadeSolicitada, jaTentados);
        if (melhor.isEmpty()) {
            estado = EstadoAlocacao.SEM_CANDIDATO;
            return Optional.empty();
        }
        Oferta escolhida = melhor.get();
        String tentativaId = requisicaoId + "-T" + (tentativas.size() + 1);
        tentativas.add(new Tentativa(tentativaId, escolhida,
                Pontuacao.calcular(escolhida, quantidadeSolicitada), quantidadeSolicitada));
        estado = EstadoAlocacao.RESERVA_PENDENTE;
        return Optional.of(solicitacaoPendente());
    }

    public SolicitacaoReserva solicitacaoPendente() {
        Tentativa t = tentativaAtiva().orElseThrow(() -> new IllegalStateException("Sem tentativa ativa"));
        return new SolicitacaoReserva(requisicaoId, t.id(), t.oferta().hospitalId(), insumoId, t.quantidade());
    }

    public void registrarReservaConfirmada(String tentativaId) {
        Tentativa t = tentativa(tentativaId);
        t.reservaConfirmada();
        if (t.estado() == EstadoTentativa.RESERVADA) {
            estado = EstadoAlocacao.PREPARO_PENDENTE;
        }
    }

    public void registrarReservaRecusada(String tentativaId) {
        Tentativa t = tentativa(tentativaId);
        t.reservaRecusada();
        reavaliarAposEncerramento(t);
    }

    public void registrarPreparoConcluido(String tentativaId) {
        Tentativa t = tentativa(tentativaId);
        t.preparoConcluido();
        if (t.estado() == EstadoTentativa.CONFIRMADA) {
            estado = EstadoAlocacao.CONFIRMADA;
        }
    }

    public void registrarPreparoFalhou(String tentativaId) {
        Tentativa t = tentativa(tentativaId);
        t.preparoFalhou();
        if (t.estado() == EstadoTentativa.COMPENSANDO) {
            estado = EstadoAlocacao.COMPENSANDO;
        }
    }

    public void registrarReservaLiberada(String tentativaId) {
        Tentativa t = tentativa(tentativaId);
        t.reservaLiberada();
        reavaliarAposEncerramento(t);
    }

    private void reavaliarAposEncerramento(Tentativa t) {
        if (t.estado() == EstadoTentativa.ENCERRADA && estado != EstadoAlocacao.CONFIRMADA) {
            estado = EstadoAlocacao.ABERTA;
        }
    }

    public Optional<Tentativa> tentativaAtiva() {
        return tentativas.stream().filter(t -> t.estado().ativa()).findFirst();
    }

    private Tentativa tentativa(String id) {
        return tentativas.stream().filter(t -> t.id().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tentativa desconhecida: " + id));
    }

    public String requisicaoId() { return requisicaoId; }
    public String insumoId() { return insumoId; }
    public int quantidadeSolicitada() { return quantidadeSolicitada; }
    public EstadoAlocacao estado() { return estado; }
    public List<Tentativa> tentativas() { return Collections.unmodifiableList(tentativas); }
}
