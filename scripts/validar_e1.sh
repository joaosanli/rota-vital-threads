#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
if ! command -v docker >/dev/null 2>&1; then
  echo 'Docker nao encontrado. Instale o Docker Desktop ou Docker Engine.' >&2
  exit 1
fi
if ! command -v curl >/dev/null 2>&1; then
  echo 'curl nao encontrado.' >&2
  exit 1
fi
: "${APP_PORT:=8080}"
if [[ -f .env ]]; then
  # Extrai apenas APP_PORT da configuracao local; nao imprime nem carrega segredos.
  configured_port="$(sed -n 's/^APP_PORT=//p' .env | tail -n 1)"
  APP_PORT="${configured_port:-$APP_PORT}"
fi
base="http://localhost:${APP_PORT}"
docker compose up -d --wait --wait-timeout 180
echo '== Containers =='
docker compose ps
echo -e '\n== Health =='
curl --fail --show-error --silent "${base}/actuator/health"
echo -e '\n\n== Geracao registrada no PostgreSQL =='
curl --fail --show-error --silent -X POST "${base}/api/dados/gerar?requisicoes=1000"
echo -e '\n\n== Historico persistente =='
curl --fail --show-error --silent "${base}/api/historico/geracoes"
echo -e '\n\nE1 respondeu. Teste a persistencia executando docker compose down, docker compose up -d e consultando /api/historico/geracoes novamente.'
