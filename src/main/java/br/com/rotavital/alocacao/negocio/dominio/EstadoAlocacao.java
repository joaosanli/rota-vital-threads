package br.com.rotavital.alocacao.negocio.dominio;

public enum EstadoAlocacao {
    /** Sem tentativa ativa; pronta para iniciar. */
    ABERTA,
    /** Tentativa ativa aguardando a resposta do Estoque. */
    RESERVA_PENDENTE,
    /** Reserva confirmada; aguardando o preparo da Expedicao. */
    PREPARO_PENDENTE,
    /** Reserva e preparo concluidos. Estado terminal. */
    CONFIRMADA,
    /** Falha de preparo; aguardando os estados terminais de reserva e preparo. */
    COMPENSANDO,
    /** Nenhuma oferta suficiente. Estado terminal. */
    SEM_CANDIDATO
}
