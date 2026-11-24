# ADR 0001 — Decomposição do Rota Vital

Status: proposto  
Data: 24/11/2026

## Contexto

A API Java existente reúne preparação sintética, cruzamento e histórico. Um squad mantém um deploy e precisa evoluir a regra de alocação com testes. Os cenários projetam picos de 2, 30 e 100 requisições HTTP/s, ainda sem medições na arquitetura alvo.

## Decisão

Adotar primeiro um monólito modular em três camadas, com fronteiras de Requisições, Estoque, Alocação, Expedição e Identidade e Acesso. A projeção extrai Alocação primeiro se testes e métricas mostrarem saturação de CPU sob carga após ajuste e replicação; os outros contextos de domínio têm plano de extração e Identidade e Acesso delega autenticação a OIDC.

## Consequências

Um pipeline e um PostgreSQL simplificam a implantação inicial. Cada módulo tem esquema e responsabilidade de escrita próprios; referências cruzadas usam IDs e contratos. Replicar o monólito aumenta a capacidade em conjunto. Extrair Alocação depois exige rede, migração, contratos versionados, tracing e orçamento próprio. A alternativa de distribuir todos os contextos imediatamente foi descartada pelo custo operacional e pela falta de evidência de necessidade.
