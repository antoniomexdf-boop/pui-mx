# Importacion de Datos a MariaDB

## 1. Crear las bases

Ejecuta:

```bash
mariadb -u root -p < mariadb_crear_bases_y_usuarios.sql
```

## 2. Crear la tabla destino

La aplicacion puede inicializar tablas demo, pero para una carga institucional controlada debes crear la tabla equivalente en `datos_db` con el esquema esperado por el proyecto.

## 3. Importar CSV

Ejemplo:

```sql
LOAD DATA LOCAL INFILE '/ruta/plantilla_registros_institucionales.csv'
INTO TABLE datos.registros_institucionales
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(curp, nombre_completo, tipo_evento, fecha_evento, descripcion_lugar, direccion_completa,
 telefono, correo, fecha_creacion, fecha_modificacion, lugar_nacimiento, sexo_asignado);
```

## 4. Validacion posterior

```sql
SELECT COUNT(*) FROM datos.registros_institucionales;
SELECT curp, tipo_evento, fecha_evento
FROM datos.registros_institucionales
ORDER BY fecha_evento DESC
LIMIT 20;
```

## 5. Recomendaciones

- Cargar primero un lote pequeño.
- Validar una CURP real en `/demo/registros/{curp}`.
- Ejecutar despues `POST /activar-reporte` con una CURP cargada.
