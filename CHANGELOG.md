# Changelog

Todos los cambios relevantes de este proyecto se documentan en este archivo.

El formato sigue una version simplificada inspirada en Keep a Changelog.

## [0.9.6] - 2026-03-19

### Added
- Workflow de GitHub Actions para ejecutar `mvn clean test` en `push` y `pull_request` sobre `main`.

### Changed
- `README.md` se reescribio para presentar `PUI MX` como integracion institucional y no como demo.
- Se actualizo la version del proyecto y de artefactos asociados a `0.9.6`.

## [0.9.5] - 2026-03-19

### Changed
- La licencia del proyecto se actualizo de GPL a MIT.
- Se actualizaron cabeceras fuente, `LICENSE.md`, `NOTICE.md` y documentacion para reflejar MIT.

## [0.9.4] - 2026-03-18

### Changed
- El paquete de despliegue real se ajusto a Apache + MariaDB.
- Se reemplazo el conector JDBC de MySQL por MariaDB.
- Se actualizo `application-prod.yml` para `MariaDBDialect`, driver MariaDB y URLs `jdbc:mariadb`.
- Se agrego `apache-pui-mx.conf` como plantilla de reverse proxy TLS.
- Se actualizo la documentacion y los artefactos de despliegue a la version `0.9.4`.

## [0.9.3] - 2026-03-18

### Added
- Carpeta `docs/operacion-produccion/` con artefactos base para despliegue real.
- `.env.example` para variables de entorno.
- `docker-compose.yml` con stack de bases y la aplicacion.
- `run-prod.sh` para compilar y levantar el stack.
- Documentacion de carga e importacion de datos.

## [0.9.2] - 2026-03-18

### Fixed
- Alineacion de `PuiControllerTest` con el comportamiento real de seguridad para `GET /login`, que actualmente responde `401`.
- Ajuste de version de artefacto y manual a `0.9.2`.

## [0.9.1] - 2026-03-18

### Fixed
- Correccion de configuracion de datasources personalizados para Hikari usando `jdbc-url` en lugar de `url`.
- Ajuste de la documentacion del manual para reflejar el nombre del artefacto `0.9.1`.

## [0.9.0] - 2026-03-18

### Added
- `MANUAL_INSTALACION_Y_PRUEBAS.md` como fuente principal de documentacion operativa.

### Changed
- Se reemplazo la documentacion de versionamiento en HTML/DOCX por `CHANGELOG.md`.
- Se eliminaron los documentos HTML y DOCX de versionamiento e instalacion para evitar duplicidad.
- La version del proyecto en `pom.xml` se actualizo a `0.9.0`.

## [0.8.0] - 2026-03-18

### Added
- Manual de instalacion y pruebas en Word y HTML.
- Consola web en `/dashboard.html` para monitoreo de auditoria, reportes y registros.
- Endpoint `/demo/resumen` para alimentar la consola.

### Changed
- Renombre del proyecto a `PUI MX`.
- Actualizacion de branding y referencias visibles en configuracion, recursos estaticos y README.
- Simplificacion de la estructura Java a `src/main/java/puimx` y `src/test/java/puimx`.

## [0.7.0] - 2026-03-18

### Added
- Cabeceras de licencia en archivos fuente relevantes.
- Archivos `LICENSE.md` y `NOTICE.md`.

### Changed
- Normalizacion legal del proyecto con referencia al repositorio:
  `https://github.com/antoniomexdf-boop/pui-mx`

## [0.6.0] - 2026-03-18

### Added
- Copia independiente del proyecto para evolucionarlo sin tocar la version base.
- Panel web estatico con HTML, CSS y JavaScript.

### Changed
- El proyecto intermedio `pui-upn` fue consolidado posteriormente como `pui-mx`.

## [0.5.0] - 2026-03-18

### Added
- Endpoints demo para consulta de reportes, auditoria y registros institucionales.
- Datos demo iniciales y resumen de actividad.

### Changed
- Seguridad ajustada para permitir acceso a consola y endpoints demo de solo lectura.

## [0.4.0] - 2026-03-18

### Added
- Dos bases separadas:
  - `platform-db` para reportes y auditoria
  - `datos-db` para registros institucionales
- Scripts SQL de inicializacion para ambos esquemas.
- Preparacion de perfil productivo con MySQL.

### Changed
- Reemplazo de persistencia minima por una arquitectura de demo funcional con separacion de responsabilidades.

## [0.3.0] - 2026-03-18

### Added
- Auditoria persistente en base de datos.
- Scheduler real para fase 3.
- Servicio async separado para ejecucion de fases iniciales.

### Changed
- Integracion real de fases 1, 2 y 3 contra datos institucionales.
- Eliminacion de simulaciones principales en busquedas.

## [0.2.0] - 2026-03-18

### Added
- Configuracion dual de datasource.
- Repositorios y modelos para reportes, auditoria y registros institucionales.
- Cliente PUI con `demo-mode`, timeouts y registro de salidas.

### Changed
- El sistema paso de esqueleto tecnico a demo funcional operable localmente.

## [0.1.0] - 2026-03-18

### Added
- Revision inicial del proyecto frente a la documentacion funcional entregada.

### Identified
- Faltantes de base de datos real, auditoria persistente, integracion de fases y preparacion para uso institucional.
