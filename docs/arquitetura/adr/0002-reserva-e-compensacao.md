# ADR 0002 — Reserva e compensação

Status: proposto  
Data: 24/11/2026

## Contexto

A Parte 1 calcula o fornecedor recomendado lendo um snapshot sintético. Na proposta, uma oferta pode ficar desatualizada entre ranking e reserva, e o preparo de expedição pode falhar depois de separar o estoque.

## Decisão

Fazer do Estoque a autoridade pela reserva atômica e usar saga orquestrada pela Alocação na projeção distribuída. Correlacionar os passos por requisicaoId e tentativaId e gravar alterações de estado com outbox; deduplicar consumo em inbox.

## Consequências

A alocação confirma apenas após reserva confirmada e preparo bem-sucedido. Em falha antes da autorização de despacho, cancelar o preparo e liberar a reserva; aguardar ambos os estados terminais antes de selecionar outra oferta. Estoque e Expedição registram marcadores terminais para que um comando atrasado não reabra a tentativa. Timeouts exigem reconsulta ou reenvio idempotente, com retries limitados, DLQ e reconciliação. Uma transação distribuída 2PC foi descartada pelo acoplamento entre serviços e pela necessidade de coordenar ações assíncronas.
