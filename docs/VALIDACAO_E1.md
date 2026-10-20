# Verificações locais — E1

Verificações estáticas realizadas durante a preparação do pacote (20/10/2026):

- `compose.yaml` analisado com parser YAML: serviços `api` e `db`, ambos com healthcheck, dependência `service_healthy`, rede e volume confirmados.
- `pom.xml` lido como XML válido e dependências de Actuator, JDBC e driver PostgreSQL presentes.
- `Dockerfile` verificado: dois estágios, testes via `mvn clean verify`, cópia do JAR para JRE e execução com usuário não root.
- `.env.example`, `.gitignore` e `.dockerignore` verificados; nenhum `.env` real incluído.
- Presentação PPTX convertida e inspecionada como PDF de seis slides.

**Limitação importante:** não foi possível executar `docker compose up -d --build` porque Docker não está disponível neste ambiente. Também não foi possível rodar `./mvnw test`: o wrapper precisa baixar o Maven 3.9.9, e o ambiente de elaboração não tem acesso ao download. Portanto, o build Docker, os testes Java, os healthchecks reais e a persistência após reinício **continuam pendentes de validação na máquina do grupo**. Não existem imagens de pipeline nem prints de contêineres fabricados.

Para validação real, siga as instruções do README e capture os prints listados em `docs/evidencias/README.md`.
