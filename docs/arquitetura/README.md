# Rota Vital · Planejamento de arquitetura

Entrega de Infraestrutura de Software, Unidade II · Parte 2, CESAR School, 2026.2.

## Artefatos

- [Mapa de contextos](diagramas/mapa-contextos.png) · [fonte Mermaid](diagramas/mapa-contextos.mmd)
- [Três camadas](diagramas/camadas.png) · [fonte Mermaid](diagramas/camadas.mmd)
- [Projeção com microsserviços](diagramas/microsservicos.png) · [fonte Mermaid](diagramas/microsservicos.mmd)
- [Cenário baixo](diagramas/cenario-baixo.png), [moderado](diagramas/cenario-moderado.png) e [alto](diagramas/cenario-alto.png) · fontes Mermaid na mesma pasta.
- [Planilha de orçamento](orcamento-rota-vital.xlsx) · premissas editáveis, fórmulas e fontes.
- [ADR 0001](adr/0001-decomposicao.md) e [ADR 0002](adr/0002-reserva-e-compensacao.md).
- [Cenário Gherkin](../../src/test/resources/features/alocacao.feature) · executado pelos testes (`ExecutarAceiteTest`).
- [Estrutura de pacotes](pacotes.txt).

## Escopo

A Parte 1 contém o motor de cruzamento sintético, testes, CI/CD e evidências históricas. A Parte 2 propõe o monólito modular e documenta uma evolução com microsserviços. Os três cenários orçam o monólito modular no Render para isolar o efeito da escala e da disponibilidade. A região adotada é Virginia (EUA).

## Fontes do orçamento · 24/11/2026

- [Preços Render](https://render.com/pricing)
- [Regiões](https://render.com/docs/regions)
- [Capacidade de compute](https://render.com/docs/compute-plans)
- [Alta disponibilidade PostgreSQL](https://render.com/docs/postgresql-high-availability)
- [Tráfego de saída](https://render.com/docs/outbound-bandwidth)
- [Disco PostgreSQL](https://render.com/docs/blueprint-spec#disksizegb)
- [Escala dos serviços](https://render.com/docs/scaling)
