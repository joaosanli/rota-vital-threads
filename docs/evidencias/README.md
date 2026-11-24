# Evidências reais da E1 — 20/10/2026

- `01-compose-ps.png`: serviços criados e inicializados; a API aparece como `health: starting` na captura, não como `healthy`.
- `02-api-health.png`: endpoint `/actuator/health` retorna `{"status":"UP"}`.
- `03-persistencia.png`: execução `docker compose down` e `docker compose up -d`, com consulta mostrando registros mantidos após recriação.

A captura 04 registra o healthcheck remoto histórico. Veja as execuções e as regras da main no README e em ENTREGAS_E2_E3.md.
