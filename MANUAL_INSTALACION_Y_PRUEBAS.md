# Manual de Instalacion y Pruebas

## Proyecto

- Nombre: `PUI MX`
- Repositorio: `https://github.com/antoniomexdf-boop/pui-mx`
- Licencia: `Licencia MIT`

## Objetivo

Este manual explica como instalar, ejecutar y probar `PUI MX` en modo demo local y como prepararlo para un despliegue con MariaDB.

## Requisitos

- Java JDK 17
- Maven 3.9 o superior
- Navegador moderno
- MariaDB 11.x solo si se usara el perfil `prod`

## Estructura relevante

```text
/pui-mx
|-- pom.xml
|-- README.md
|-- CHANGELOG.md
|-- LICENSE.md
|-- NOTICE.md
|-- MANUAL_INSTALACION_Y_PRUEBAS.md
|-- src/main/java/puimx
|-- src/test/java/puimx
|-- src/main/resources/application.yml
|-- src/main/resources/application-prod.yml
|-- src/main/resources/static/dashboard.html
|-- src/main/resources/db/platform
\-- src/main/resources/db/datos
```

## Modo demo local

### 1. Clonar repositorio

```bash
git clone https://github.com/antoniomexdf-boop/pui-mx.git
cd pui-mx
```

### 2. Verificar configuracion demo

El archivo `src/main/resources/application.yml` ya viene preparado para:

- usar H2 en archivo
- crear dos bases separadas
- cargar datos demo
- activar `demo-mode`

### 3. Compilar

```bash
mvn clean package
```

### 4. Ejecutar

```bash
mvn spring-boot:run
```

O bien:

```bash
java -jar target/pui-mx-0.9.7.jar
```

### 5. Resultado esperado

- Aplicacion disponible en `http://localhost:8080`
- Bases demo creadas en archivos locales
- Datos iniciales cargados automaticamente

## Pruebas funcionales

### 1. Obtener JWT

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"usuario":"PUI","clave":"Pui@Institucion2025!"}'
```

### 2. Activar reporte

```bash
curl -X POST http://localhost:8080/activar-reporte \
  -H "Authorization: Bearer TU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id":"A1B2C3D4E5F6A1B2-550e8400-e29b-41d4-a716-446655440000",
    "curp":"TEST010101HDFABC01",
    "nombre":"JUAN",
    "primer_apellido":"PEREZ",
    "segundo_apellido":"LOPEZ",
    "fecha_nacimiento":"1990-01-01",
    "fecha_desaparicion":"2024-12-15",
    "lugar_nacimiento":"CDMX",
    "sexo_asignado":"H"
  }'
```

### 3. Consultar endpoints demo

- `http://localhost:8080/demo/resumen`
- `http://localhost:8080/demo/reportes`
- `http://localhost:8080/demo/auditoria`
- `http://localhost:8080/demo/registros`
- `http://localhost:8080/demo/registros/TEST010101HDFABC01`

### 4. Abrir consola web

```text
http://localhost:8080/dashboard.html
```

La consola muestra:

- resumen numerico
- auditoria reciente
- reportes activos
- registros institucionales
- busqueda por CURP

### 5. Desactivar reporte

```bash
curl -X POST http://localhost:8080/desactivar-reporte \
  -H "Authorization: Bearer TU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"id":"A1B2C3D4E5F6A1B2-550e8400-e29b-41d4-a716-446655440000"}'
```

## Perfil productivo con MariaDB

### 1. Crear bases

```sql
CREATE DATABASE platform_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE datos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar variables

```bash
export PLATFORM_DB_URL='jdbc:mariadb://localhost:3306/platform_db?useSsl=false'
export PLATFORM_DB_USERNAME='root'
export PLATFORM_DB_PASSWORD='tu_password'
export DATOS_DB_URL='jdbc:mariadb://localhost:3306/datos_db?useSsl=false'
export DATOS_DB_USERNAME='root'
export DATOS_DB_PASSWORD='tu_password'
export JWT_SECRET='ClaveJwtSeguraDe32CaracteresMinimo!!!'
export PUI_CLAVE_RECIBIDA='Pui@Institucion2025!'
export PUI_RFC_HOMOCLAVE='RFC_DEMO010101ABC'
export PUI_API_CLAVE='CLAVE_PROPORCIONADA_POR_PUI'
export PUI_BIOMETRICOS_CLAVE='ClaveAES256ProporcionadaPorPUI'
```

### 3. Ejecutar perfil prod

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## Solucion de problemas

### La app no arranca

- Verificar `java -version`
- Verificar `mvn -version`

### Error 401

- Obtener un token nuevo por `/login`

### La consola no muestra datos

- Ejecutar primero `/activar-reporte`
- Revisar `/demo/resumen`

### Falla con MariaDB

- Revisar URL, usuario y password de ambas bases
- Confirmar que el perfil `prod` esta activo

## Notas

- El paquete Java raiz del proyecto es `puimx`
- El historial de cambios se mantiene en `CHANGELOG.md`
