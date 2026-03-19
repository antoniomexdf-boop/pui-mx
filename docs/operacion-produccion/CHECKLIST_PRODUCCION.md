# Checklist de Produccion

## 1. Infraestructura

- Tener un servidor o contenedor para la aplicacion.
- Tener una instancia MariaDB 11.x para `platform_db`.
- Tener una instancia MariaDB 11.x para `datos_db` o al menos una base separada en el mismo servidor.
- Configurar respaldos automaticos.
- Configurar monitoreo basico del servicio y de MariaDB.

## 2. Seguridad

- Publicar el servicio solo por HTTPS.
- Configurar TLS 1.2 o superior.
- Configurar Apache 2.4 como reverse proxy si ese sera el frontal.
- Mover secretos a variables de entorno o secret manager.
- Confirmar que `JWT_SECRET` no este en repositorio.
- Restringir acceso de red a MariaDB.
- Definir IPs o ACLs si la PUI o terceros deben entrar por lista blanca.

## 3. Aplicacion

- Desactivar `demo-mode`.
- Configurar `application-prod.yml` con valores reales.
- Validar `base-url` de la PUI real o sandbox.
- Verificar que el scheduler de fase 3 este habilitado y con cron adecuado.
- Revisar logs y rotacion.

## 4. Datos

- Validar que `curp` tenga 18 caracteres en mayusculas.
- Validar `fecha_evento`.
- Validar `fecha_creacion`.
- Asegurar que `tipo_evento` nunca venga vacio.
- Cargar datos reales en `datos.registros_institucionales`.
- Probar consultas por CURP con volumen real en MariaDB.

## 5. Integracion PUI

- Probar `POST /login` de tu API.
- Probar `POST /activar-reporte-prueba`.
- Probar `POST /activar-reporte`.
- Validar envio de coincidencias.
- Validar envio de `busqueda-finalizada`.
- Validar desactivacion de reportes.

## 6. Validacion final

- Ejecutar `mvn test`.
- Ejecutar analisis de seguridad SAST.
- Ejecutar analisis DAST.
- Ejecutar analisis SCA.
- Confirmar evidencia operativa y tecnica antes de liberar.
