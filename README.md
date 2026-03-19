# PUI MX

Demo funcional en Spring Boot 3.2 / Java 17 para integrar una institucion con la Plataforma Unica de Identidad.

## Que hace ahora

- Expone los 4 endpoints requeridos por PUI: `/login`, `/activar-reporte`, `/activar-reporte-prueba`, `/desactivar-reporte`.
- Usa dos bases separadas:
  - `platform-db`: esquemas `pui` y `audit` para reportes activos y bitacora.
  - `datos-db`: esquema `datos` para los registros institucionales consultables.
- Ejecuta las fases 1, 2 y 3 con consultas reales sobre la base `datos-db`.
- Registra auditoria persistente de recepcion, coincidencias y cierre de busqueda.
- Incluye `demo mode` para simular el envio a la PUI sin depender de conectividad externa.
- Incluye endpoints de consulta del demo para que terceros revisen el comportamiento.
- Incluye una consola web en `/dashboard.html` para visualizar historiales, reportes y consultas remotas.

## Estructura de codigo simplificada

- El paquete Java principal ahora es `puimx`.
- Codigo productivo: `src/main/java/puimx`
- Tests: `src/test/java/puimx`
- Esto reemplaza la ruta anterior larga `mx/gob/institucion/pui`.

## Versionamiento

- El historial de cambios del proyecto se mantiene en `CHANGELOG.md`.
- El manual operativo se mantiene en `MANUAL_INSTALACION_Y_PRUEBAS.md`.
- La documentacion para despliegue y carga productiva se encuentra en `docs/operacion-produccion/`.
- El stack base de despliegue real incluye `docs/operacion-produccion/docker-compose.yml`, `.env.example`, `run-prod.sh` y `apache-pui-mx.conf`.

## Endpoints principales

| Metodo | Path | Auth | Uso |
|---|---|---|---|
| POST | `/login` | No | Obtener JWT |
| POST | `/activar-reporte` | Bearer JWT | Persistir reporte e iniciar fases |
| POST | `/activar-reporte-prueba` | Bearer JWT | Validar conectividad/contrato |
| POST | `/desactivar-reporte` | Bearer JWT | Detener busqueda continua |
| GET | `/demo/reportes` | No | Consultar reportes registrados |
| GET | `/demo/auditoria` | No | Consultar bitacora |
| GET | `/demo/registros` | No | Ver registros institucionales demo |
| GET | `/demo/registros/{curp}` | No | Filtrar registros por CURP |
| GET | `/demo/resumen` | No | Resumen numerico para la consola |
| GET | `/dashboard.html` | No | Consola de control web |

## Configuracion demo

Por defecto `application.yml` deja listo el demo con H2 en archivo y carga datos iniciales automaticamente.

```yaml
app:
  datasource:
    platform:
      jdbc-url: jdbc:h2:file:./data/platform-db;MODE=MySQL;AUTO_SERVER=TRUE;DATABASE_TO_LOWER=TRUE
    datos:
      jdbc-url: jdbc:h2:file:./data/datos-db;MODE=MySQL;AUTO_SERVER=TRUE;DATABASE_TO_LOWER=TRUE

pui:
  api:
    demo-mode: true
```

## Configuracion MariaDB

El perfil `prod` ya viene preparado para MariaDB con dos bases:

- `platform_db`
- `datos_db`

Variables esperadas:

- `PLATFORM_DB_URL`
- `PLATFORM_DB_USERNAME`
- `PLATFORM_DB_PASSWORD`
- `DATOS_DB_URL`
- `DATOS_DB_USERNAME`
- `DATOS_DB_PASSWORD`
- `JWT_SECRET`
- `PUI_CLAVE_RECIBIDA`
- `PUI_RFC_HOMOCLAVE`
- `PUI_API_CLAVE`
- `PUI_BIOMETRICOS_CLAVE`

## Flujo demo rapido

1. Levanta la app.
2. Haz `POST /login`.
3. Haz `POST /activar-reporte` con la CURP `TEST010101HDFABC01`.
4. Consulta `GET /demo/reportes` y `GET /demo/auditoria`.
5. Espera el scheduler o inserta nuevos registros en `datos-db` para ver la fase 3.
6. Abre `http://localhost:8080/dashboard.html` para ver la consola.

Payload sugerido:

```json
{
  "id": "A1B2C3D4E5F6A1B2-550e8400-e29b-41d4-a716-446655440000",
  "curp": "TEST010101HDFABC01",
  "nombre": "JUAN",
  "primer_apellido": "PEREZ",
  "segundo_apellido": "LOPEZ",
  "fecha_nacimiento": "1990-01-01",
  "fecha_desaparicion": "2024-12-15",
  "lugar_nacimiento": "CDMX",
  "sexo_asignado": "H"
}
```

## Notas

- No pude ejecutar `mvn test` en este entorno porque Maven no esta instalado localmente.
- Los scripts SQL de demo viven en:
  - `src/main/resources/db/platform`
  - `src/main/resources/db/datos`
