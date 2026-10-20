# Rota Vital — Projeto Integrador | Entrega E1 (Container automatizado)

**Curso:** Análise e Desenvolvimento de Sistemas — CESAR School  
**Disciplina:** Infraestrutura de Software — Unidade 2 / 2026.2  
**Professor:** Raoni Monteiro de Oliveira  
**Integrantes:** João Lucas Santos Lira, Mariana Ferreira, Thaissa Fernandes e Priscila Maciel de Lima.

> **Status da entrega:** a E1 foi executada localmente com Docker Compose. Há evidências reais da API respondendo, dos contêineres iniciando e da permanência dos registros do PostgreSQL após `docker compose down` e `docker compose up -d`. Para fechar a documentação, recomenda-se capturar `docker compose ps` quando **ambos** os serviços estiverem `healthy`. A URL pública e os pipelines de E2/E3 ainda não estão disponíveis.

## Contexto do projeto

O Rota Vital simula requisições de insumos hospitalares e procura fornecedores adequados
considerando estoque e distância. Compara o processamento sequencial com threads de
plataforma e virtuais em Java 21. Na E1, a API existente passou a ser executada com
PostgreSQL via Docker Compose, sem modificar as regras de cruzamento.

## Integrantes

| Integrante | Papel nesta entrega |
| --- | --- |
| João Lucas Santos Lira | Integrante do grupo — definir contribuição na apresentação |
| Mariana Ferreira | Integrante do grupo — definir contribuição na apresentação |
| Thaissa Fernandes | Integrante do grupo — definir contribuição na apresentação |
| Priscila Maciel de Lima | Integrante do grupo — definir contribuição na apresentação |

## Como rodar (E1)

**Pré-requisitos:** Docker Desktop em execução (com Compose V2) ou Docker Engine + Compose plugin.
Não é necessário instalar Java, Maven ou PostgreSQL na máquina.

1. Na primeira utilização, preparar as variáveis de ambiente:

   ```bash
   cp .env.example .env
   ```

   Edite `.env` e substitua `ALTERE_ESTA_SENHA_LOCAL` por uma senha **local**.
   No PowerShell, utilize `Copy-Item .env.example .env`. O arquivo `.env` está no
   `.gitignore` e nunca deve ser enviado ao repositório.

2. Para **subir a aplicação e o banco com um único comando**:

   ```bash
   docker compose up -d --build
   ```

   O build é multi-stage: o Maven executa `clean verify` (incluindo os testes)
   **antes** de criar a imagem final. Se algum teste falhar, o build é interrompido.

3. Conferir os serviços:

   ```bash
   docker compose ps
   curl http://localhost:8080/actuator/health
   curl http://localhost:8080/api/info
   curl -X POST 'http://localhost:8080/api/dados/gerar?requisicoes=1000'
   curl http://localhost:8080/api/historico/geracoes
   ```

   A porta padrão é 8080. Se alterar `APP_PORT` em `.env`, ajuste a porta nas URLs.
   Aguarde os dois contêineres ficarem `healthy`. O endpoint de saúde retorna
   `{"status":"UP"}` quando a API e a conexão com o banco estão saudáveis.
   No Windows PowerShell, `curl` pode ser um alias; use `curl.exe` ou `Invoke-RestMethod`.

4. Parar ou remover a aplicação:

   ```bash
   docker compose down           # para serviços; preserva volume e banco
   docker compose up -d          # reabre mantendo historico de geracoes
   # CUIDADO: docker compose down -v apaga os dados gravados
   ```

Para validar e coletar saídas de terminal em Linux/macOS/WSL: `bash scripts/validar_e1.sh`.
Os prints reais já obtidos estão em `docs/evidencias/` (ver seção **Evidências da E1**).

### Persistência implementada e verificada localmente

O Rota Vital usa **duas camadas de dados com finalidades distintas**:

- **Snapshots em memória:** os arrays gerados pela semente fixa continuam em memória,
  evitando que acesso a disco distorça os benchmarks de concorrência do PI.
- **PostgreSQL:** o `POST /api/dados/gerar` grava metadados (quantidade de requisições,
  hospitais, insumos, semente e uso de cache) na tabela `historico_geracoes`.
  Consulte `GET /api/historico/geracoes` para ver os 20 registros mais recentes.
  Esses metadados sobrevivem ao reinício por causa do volume `postgres_data`.

O esquema é criado automaticamente por `src/main/resources/schema.sql`, de forma
idempotente. O banco não é publicado em uma porta da máquina; somente a API é acessível.

## Endpoints do Rota Vital

| Método | Rota | Finalidade |
| --- | --- | --- |
| GET | `/actuator/health` | Healthcheck da API e do banco |
| GET | `/api/info` | Informações da JVM e das bases em cache |
| POST | `/api/dados/gerar?requisicoes=1000` | Gerar snapshot e registrar metadados no PostgreSQL |
| GET | `/api/historico/geracoes` | Consultar últimos 20 registros persistidos |
| GET | `/api/cruzamento?modo=sequencial&requisicoes=1000` | Cruzamento sequencial |
| GET | `/api/cruzamento?modo=plataforma&threads=4&requisicoes=1000` | Cruzamento com threads de plataforma |
| GET | `/api/cruzamento?modo=virtual&threads=4&requisicoes=1000` | Cruzamento com threads virtuais |

> O modo `inseguro` existente é apenas didático (race condition). Não é usado no fluxo de produção.

## Testes

O projeto já possuía `MotorCruzamentoTest`, com testes de equivalência de resultados
entre processamento sequencial, plataforma e virtual, e verificação de particionamento.
A E1 **reutiliza esses testes**, executados durante `mvn clean verify` no primeiro
estágio do Dockerfile. Para rodar apenas os testes, com JDK 21 disponível:

```bash
./mvnw test
# Windows: .\mvnw.cmd test
```

**Limite de escopo:** a abordagem TDD exigida na **E2** (story map, histórias, tarefas,
testes por nível, commits red → green) ainda não foi implementada nesta E1.

## Pipeline e deploy

| Entrega | Situação | Escopo |
| --- | --- | --- |
| E1 | Implementada e executada localmente | Build multi-stage, Compose, PostgreSQL, healthcheck, configuração e evidências reais |
| E2 | Não implementada | Story map, TDD, testes unitários/integrados/aceitação na esteira |
| E3 | Não implementada | CI/CD, branch protection, deploy público e rollback |

O `mvn verify` **dentro do Dockerfile não substitui uma esteira de CI/CD**. A URL pública
e as execuções vermelha/verde do pipeline dependem de E3; nenhuma URL é inventada aqui.

**Repositório do grupo:** https://github.com/joaosanli/rota-vital-threads  
**URL pública:** [preencher na E3]

## Decisões técnicas e justificativas

| Decisão | Escolha | Alternativas consideradas | Justificativa |
| --- | --- | --- | --- |
| Linguagem | Java 21 + Spring Boot 3.5 | Python / Node.js | Preserva a tecnologia e os algoritmos existentes do PI. |
| Base do build | `maven:3.9-eclipse-temurin-21` | JDK isolado + Maven Wrapper | Maven já pronto para compilar e executar testes, sem instalação local. |
| Base da execução | `eclipse-temurin:21-jre-noble` | Imagem com JDK completo / Alpine | JRE sem compilador reduz componentes de runtime; Debian/Ubuntu favorece compatibilidade com Java e `curl`. |
| Multi-stage | Build Maven separado do JRE | Dockerfile de estágio único | Leva à imagem final somente o JAR e dependências de runtime, sem Maven e testes. |
| Banco | PostgreSQL 16 Alpine | MySQL / H2 / MongoDB | Banco relacional com driver JDBC nativo; volume persistente e imagem compacta. |
| Dados | Volume nomeado `postgres_data` | Bind mount / banco efêmero | Preserva o histórico após `docker compose down` e independe do diretório do host. |
| Rede | Rede `interna` do Compose, hostname `db` | `localhost` / IP fixo | DNS do Compose resolve `db`; só a API publica a porta, isolando o PostgreSQL. |
| Ordem/saúde | `depends_on: service_healthy` + pg_isready + Actuator | `depends_on` simples / sleep fixo | A API só inicia após banco pronto, e os healthchecks detectam falhas reais. |
| Configuração | `.env` local + `.env.example` rastreado | Senha no Java/Compose | Centraliza variáveis sem publicar credenciais; a senha é obrigatória no Compose. |
| Persistência de metadados | Spring JDBC + `schema.sql` | JPA / Flyway | Implementação pequena para E1, sem introduzir ORM; evoluir migrations na próxima etapa. |
| Testes nesta fase | `mvn clean verify` no build | Build sem testes | Interrompe o build se os testes existentes falharem; TDD completo fica para E2. |

## Evidências da E1

As capturas abaixo foram fornecidas pelo grupo após a execução local no Windows/PowerShell (20/10/2026), e não são imagens simuladas:

| Evidência | Arquivo | O que demonstra |
| --- | --- | --- |
| Docker Compose | [`docs/evidencias/01-compose-ps.png`](docs/evidencias/01-compose-ps.png) | Os serviços `api` e `db` foram criados e iniciados. **A captura foi feita durante a inicialização da API**, portanto não demonstra `api (healthy)`; produzir print complementar com ambos saudáveis. |
| Endpoint de saúde | [`docs/evidencias/02-api-health.png`](docs/evidencias/02-api-health.png) | Resposta `{"status":"UP"}` da API em `localhost:8080`. |
| Recriação e persistência | [`docs/evidencias/03-persistencia.png`](docs/evidencias/03-persistencia.png) | Comandos `docker compose down` e `docker compose up -d`, seguidos da consulta ao histórico; os registros anteriores permanecem disponíveis no PostgreSQL. |

Também foi observada a resposta de `GET /api/historico/geracoes` com dois registros (`id` 1 e 2) relativos a 1.000 requisições, 1.000 hospitais, 30 insumos e `seed=42`. Um registro indicou `jaEstavaEmCache=false` e o outro `true`. Isso comprova a consulta ao histórico persistido; não comprova a durabilidade dos snapshots completos, que continuam em memória por decisão arquitetural.

**Para finalizar a E1:** adicionar uma captura atualizada de `docker compose ps` mostrando `api (healthy)` e `db (healthy)`. O build ocorreu na máquina local, mas não há neste conjunto uma saída isolada de `mvn clean verify` para avaliar testes individualmente. Capturas da esteira vermelha/verde e URL pública pertencem às etapas E2/E3 da atividade.

## Uso de IA

**Ferramenta:** ChatGPT (OpenAI). **Declaração:** a IA foi utilizada como apoio para
analisar os critérios da E1, rascunhar o Dockerfile multi-stage, o `compose.yaml`,
a integração mínima de metadados com PostgreSQL, o README e a apresentação.

**Validação humana:** o grupo executou o Compose no Windows, consultou a API e registrou evidências de persistência. A revisão dos arquivos e a publicação no GitHub continuam sob responsabilidade dos integrantes; nenhuma execução de CI/CD ou publicação pública foi alegada.

## Continuidade do PI original

O código de cruzamento, os testes de Threads II, o script `scripts/benchmark.py`, os
gráficos e o relatório original foram preservados. Para os benchmarks de grande porte,
verifique os limites de CPU/RAM do Docker Desktop e evite requisições simultâneas grandes.
