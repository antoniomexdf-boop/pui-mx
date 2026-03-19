# Operacion Produccion

Este apartado concentra los documentos y artefactos necesarios para preparar `PUI MX` para despliegue e integracion institucional.

## Contenido

- `CHECKLIST_PRODUCCION.md`
  Lista de pendientes para pasar de demo a produccion.

- `FORMATO_DATOS_CARGA.md`
  Especificacion del formato de datos que debe tener la informacion institucional.

- `plantilla_registros_institucionales.csv`
  Plantilla base para carga masiva en la tabla `datos.registros_institucionales`.

- `mariadb_crear_bases_y_usuarios.sql`
  Script para crear `platform_db`, `datos_db` y usuarios sugeridos en MariaDB.

- `mariadb_importacion_datos.md`
  Guia para importar CSV a MariaDB.

- `.env.example`
  Plantilla de variables de entorno para despliegue.

- `docker-compose.yml`
  Stack con dos MariaDB y la aplicacion `pui-mx`.

- `run-prod.sh`
  Script base para compilar y levantar el stack productivo.

- `apache-pui-mx.conf`
  Plantilla de VirtualHost para Apache como reverse proxy TLS.

## Flujo recomendado

1. Copiar `.env.example` a `.env`
2. Ajustar passwords, URLs y certificado
3. Colocar keystore en `docs/operacion-produccion/certs/`
4. Configurar Apache usando `apache-pui-mx.conf`
5. Ejecutar `run-prod.sh`

## Nota importante

Esto deja el proyecto preparado para un despliegue real, pero aun debes:

- tener Docker y Docker Compose instalados
- contar con un keystore TLS valido
- validar integracion real con PUI
- revisar endurecimiento de red y secretos
