#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/docker/local/docker-compose.yml"
SCHEMA_FILE="${SCRIPT_DIR}/../my-wiki/sql/schema.sql"

if [[ ! -f "${SCHEMA_FILE}" ]]; then
  echo "schema.sql not found: ${SCHEMA_FILE}" >&2
  exit 1
fi

docker compose -f "${COMPOSE_FILE}" down -v
docker compose -f "${COMPOSE_FILE}" up -d
