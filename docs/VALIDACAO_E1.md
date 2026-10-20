# Validação da E1 — Container automatizado

**Projeto:** Rota Vital — CESAR School, Infraestrutura de Software (2026.2)  
**Data das evidências:** 20/10/2026  
**Repositório:** https://github.com/joaosanli/rota-vital-threads

## Verificações de configuração

- **Dockerfile multi-stage:** estágio de build com Maven e Java 21, incluindo `mvn clean verify`; estágio de execução com JRE e artefato JAR.
- **Compose:** serviços `api` e `db` (PostgreSQL), `depends_on` condicionado à saúde do banco, healthchecks e rede entre serviços.
- **Persistência:** volume nomeado `postgres_data` para os arquivos do PostgreSQL.
- **Configuração:** `.env.example` versionado; `.env` local excluído do repositório pelo `.gitignore`.

## Validação prática feita pelo grupo

O grupo executou a aplicação localmente em Windows/PowerShell com Docker Desktop, usando `docker compose up -d --build`. Foram registradas as seguintes evidências:

| Evidência | Arquivo | Resultado observado |
| --- | --- | --- |
| Inicialização do Compose | [`01-compose-ps.png`](evidencias/01-compose-ps.png) | Contêineres `api` e `db` iniciados; na captura, a API ainda estava em `Health: starting`. |
| Saúde da API | [`02-api-health.png`](evidencias/02-api-health.png) | `GET /actuator/health` respondeu `{"status":"UP"}`. |
| Persistência após recriação | [`03-persistencia.png`](evidencias/03-persistencia.png) | Após `docker compose down` e `docker compose up -d`, o histórico consultado na API conservou os registros anteriores. |

A consulta a `GET /api/historico/geracoes` mostrou os IDs **1** e **2**, ambos relativos a 1.000 requisições, 1.000 hospitais, 30 insumos e `seed=42`. A permanência desses registros após a recriação dos contêineres evidencia a durabilidade dos **metadados gravados no PostgreSQL**, não dos snapshots de processamento, que permanecem em memória.

## Limitações e pendências de evidência

- **Healthchecks:** existe evidência da resposta saudável da API e de inicialização dos serviços, mas o print de `docker compose ps` ainda não exibe **simultaneamente** `api (healthy)` e `db (healthy)`. Recomenda-se capturar uma versão atualizada.
- **Testes:** o Dockerfile está configurado para executar `mvn clean verify` durante o build, mas não há print ou log específico do resultado dos testes Java neste conjunto de evidências.
- **CI/CD e deploy público:** pipeline com execuções vermelha/verde, URL pública e rollback **não foram implementados nem validados nesta E1**. Fazem parte das entregas E2/E3.

As evidências citadas são capturas de execução real fornecidas pelo grupo, não imagens simuladas. O README apresenta os comandos para reproduzir a execução.
