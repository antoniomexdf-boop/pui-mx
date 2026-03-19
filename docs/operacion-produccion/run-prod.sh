#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
OPS_DIR="$ROOT_DIR/docs/operacion-produccion"
ENV_FILE="$OPS_DIR/.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Falta $ENV_FILE"
  echo "Copia .env.example a .env y ajusta credenciales/paths antes de continuar."
  exit 1
fi

cd "$ROOT_DIR"

echo "Compilando artefacto..."
mvn clean package -DskipTests

JAR_NAME="${APP_JAR_NAME:-pui-mx-0.9.5.jar}"

if [[ ! -f "$ROOT_DIR/target/$JAR_NAME" ]]; then
  echo "No se encontro target/$JAR_NAME"
  exit 1
fi

echo "Levantando stack productivo con Docker Compose..."
docker compose --env-file "$ENV_FILE" -f "$OPS_DIR/docker-compose.yml" up -d

echo "Despliegue solicitado."
echo "Revisa estado con:"
echo "  docker compose --env-file $ENV_FILE -f $OPS_DIR/docker-compose.yml ps"
