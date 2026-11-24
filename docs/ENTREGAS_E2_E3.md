# E2 e E3 — implementação e ativação

## Story map
Backbone: preparar dados → comparar processamento → consultar rastreabilidade.
Release 1: H1, H2 e H3 abaixo.

| História | Tarefas e regras | Verificação |
| --- | --- | --- |
| H1: como apresentador, gerar uma base reproduzível para simular pedidos | Gerar com seed fixa; reutilizar cache; rejeitar tamanho inválido; registrar metadados | ApiIT.gerarDadosRegistraNoBanco; ApiIT.consultarHistoricoRetornaVinteMaisRecentes |
| H2: como analista, comparar modos com resultados equivalentes | Rejeitar modo/threads inválidos antes de alocar dados; dividir base sem sobreposição; preservar resultados | CruzamentoServiceTest; MotorCruzamentoTest; ApiIT.compararModosMantemResultados |
| H3: como integrante, consultar o histórico das gerações | Gravar no PostgreSQL; retornar no máximo 20 registros em ordem decrescente; informar uso de cache | ApiIT.consultarHistoricoRetornaVinteMaisRecentes, com 22 inserções e consulta HTTP |

Os testes ApiIT são automatizados de aceitação pela API HTTP real e integração com PostgreSQL real. O banco de testes é separado e descartável. Nunca execute esses testes apontando para produção: eles limpam a tabela de histórico.

## Executar testes completos

```bash
docker compose -p rotavital-tests -f compose.test.yaml up --abort-on-container-exit --exit-code-from tests
docker compose -p rotavital-tests -f compose.test.yaml down
```

Ou, com Java 21 e PostgreSQL de testes configurado por DB_HOST, DB_NAME, DB_USER e DB_PASSWORD:

```bash
bash mvnw -Pintegration clean verify
```

O Dockerfile executa os testes unitários. Os testes com banco rodam na esteira antes da construção da imagem.

## TDD: alcance real

O projeto original já existia. Não alegamos que ele inteiro nasceu de TDD.
Os testes novos de validação no serviço foram escritos e executados antes da correção: docs/evidencias/tdd-red.log registra duas falhas reais. O commit de teste precede o commit da correção. A execução verde está em docs/evidencias/testes-green.log.
O risco priorizado foi alocar grandes bases para pedidos inválidos e aceitar parâmetros inválidos fora do controlador.
Os testes HTTP das histórias foram acrescentados depois da implementação original: ampliam a cobertura, mas não provam TDD retroativo. Não há teste de carga, falha de rede ou concorrência do indicador de cache.

## CI/CD e fluxo escolhido

GitHub Actions: o repositório já está no GitHub; evita manter um servidor Jenkins.
GitHub Flow: branches curtas → pull request → revisão e testes → merge na main; adequado a um grupo pequeno com uma versão publicada. Isso precisa ser aplicado no GitHub, não apenas descrito aqui.
Render: executa o Dockerfile e fornece PostgreSQL gerenciado; evita administrar uma VM. O plano e sua disponibilidade devem ser conferidos antes da criação; recursos gratuitos podem ter limitações e expiração.

O workflow ci.yml roda testes em push e PR, incluindo PostgreSQL; só constrói a imagem após aprovação dos testes; só solicita deploy após imagem e testes aprovados em push para main. O Render reconstrói o mesmo commit usando o Dockerfile. O job solicita o deploy, mas não acompanha sua conclusão: confirme no painel e na URL pública.

## Rollback

Escolher um deploy saudável anterior no painel Render e usar Rollback, quando disponível; confirmar a saúde na URL pública. Alternativa: reverter o commit problemático por um PR com git revert; o merge aprovado executa a esteira novamente e publica a reversão. Rollback da aplicação não restaura dados: mudanças incompatíveis no banco exigem plano próprio e backup. O schema atual só cria tabela se ausente.

## Evidências verificadas

O ciclo de TDD das validações pode ser acompanhado no [commit vermelho](https://github.com/joaosanli/rota-vital-threads/commit/c38a2cb5d3722210f07a6ba9fd8cd4429d0d3274) e no [commit verde seguinte](https://github.com/joaosanli/rota-vital-threads/commit/2d6b19ad30db49d77698c74fa66af361ac0d1948). A cobertura posterior do sistema original está descrita acima.

As [regras efetivas da main](https://api.github.com/repos/joaosanli/rota-vital-threads/rules/branches/main) exigem pull request, resolução das conversas e os checks Testes e Imagem. Permitem zero aprovações formais. A [execução após o merge](https://github.com/joaosanli/rota-vital-threads/actions/runs/39521021341) registra a solicitação de deploy.

Codex acrescentou testes de reprodutibilidade da semente e limites de tamanho da H1. A H3 verifica o limite de 20 e a ordem dos IDs em PostgreSQL real com 22 inserções. O resultado desta revisão está em evidencias/revisao-u2.log e evidencias/aceite-u2.json. O segundo arquivo registra quatro cenários e os resultados dos 23 passos.
