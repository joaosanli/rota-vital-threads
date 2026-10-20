# Evidências reais da E1 — 20/10/2026

- `01-compose-ps.png`: serviços criados e inicializados; a API aparece como `health: starting` na captura, não como `healthy`.
- `02-api-health.png`: endpoint `/actuator/health` retorna `{"status":"UP"}`.
- `03-persistencia.png`: execução `docker compose down` e `docker compose up -d`, com consulta mostrando registros mantidos após recriação.

**Complementar antes de enviar:** print atualizado de `docker compose ps` com os dois serviços `healthy`. As evidências de pipeline vermelho/verde e URL pública são itens futuros da E2/E3.
