CREATE TABLE IF NOT EXISTS historico_geracoes (
    id BIGSERIAL PRIMARY KEY,
    requisicoes INTEGER NOT NULL CHECK (requisicoes > 0),
    hospitais INTEGER NOT NULL CHECK (hospitais > 0),
    insumos INTEGER NOT NULL CHECK (insumos > 0),
    semente BIGINT NOT NULL,
    ja_estava_em_cache BOOLEAN NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
