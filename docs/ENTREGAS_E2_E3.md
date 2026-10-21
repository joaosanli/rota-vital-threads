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

## Ativação externa pendente

1. Obter permissão de escrita/admin no repositório ou criar um fork na conta do grupo.
2. Publicar os commits locais em uma branch e abrir PR.
3. No GitHub, proteger main: exigir PR, revisão e os checks Testes e Imagem; impedir bypass e force-push.
4. Criar conta Render e autorizar o repositório correto. Criar Blueprint usando render.yaml. A criação inicial é bootstrap; os merges seguintes usam o workflow.
5. Conferir custos e limites antes de confirmar. O banco fornece os segredos por referências do Blueprint; nenhuma senha real é versionada.
6. No painel Render, copiar Deploy Hook. No ambiente production do GitHub, criar o secret RENDER_DEPLOY_HOOK_URL. Não colocar o hook em README, prints ou mensagens.
7. Manter Auto Deploy desligado no Render: o Actions é quem libera o deploy após os gates.
8. Fazer merge de um PR verde. Confirmar deploy concluído e /actuator/health retornando UP na URL pública; registrar a URL no README.
9. Para evidência vermelha real: em branch separada, alterar temporariamente uma expectativa de teste, fazer push e abrir PR. Registrar falha e merge bloqueado; corrigir o teste e registrar nova execução verde. Não mesclar a falha na main.
10. Guardar links dos runs, PR, deploy concluído e URL pública. Logs locais não substituem evidências da esteira.

## Rollback

Escolher um deploy saudável anterior no painel Render e usar Rollback, quando disponível; confirmar a saúde na URL pública. Alternativa: reverter o commit problemático por um PR com git revert; o merge aprovado executa a esteira novamente e publica a reversão. Rollback da aplicação não restaura dados: mudanças incompatíveis no banco exigem plano próprio e backup. O schema atual só cria tabela se ausente.

## Situação

Arquivos de automação preparados localmente. Sem acesso ao GitHub/provedor, não foi possível configurar proteção, publicar PRs, executar Actions ou disponibilizar URL pública. E2 e E3 não devem ser declaradas integralmente concluídas antes dessas evidências.

Referências oficiais: https://render.com/docs/blueprint-spec e https://render.com/docs/deploy-hooks.
