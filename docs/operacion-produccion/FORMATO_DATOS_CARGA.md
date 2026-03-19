# Formato de Datos para Carga Institucional

La informacion institucional debe cargarse en la tabla:

`datos.registros_institucionales`

## Campos requeridos

| Campo | Tipo | Obligatorio | Regla |
|---|---|---:|---|
| `curp` | `CHAR(18)` | Si | 18 caracteres, mayusculas |
| `tipo_evento` | `VARCHAR(500)` | Si | No vacio |
| `fecha_evento` | `DATE` | Si | Formato `YYYY-MM-DD` |
| `fecha_creacion` | `TIMESTAMP` | Si | Formato `YYYY-MM-DD HH:MM:SS` |

## Campos recomendados

| Campo | Tipo | Uso |
|---|---|---|
| `nombre_completo` | `VARCHAR(200)` | Mejora fase 1 |
| `descripcion_lugar` | `VARCHAR(500)` | Contexto del evento |
| `direccion_completa` | `VARCHAR(500)` | Ubicacion del evento |
| `telefono` | `VARCHAR(15)` | Contacto |
| `correo` | `VARCHAR(100)` | Contacto |
| `fecha_modificacion` | `TIMESTAMP` | Fase 3 |
| `lugar_nacimiento` | `VARCHAR(20)` | Datos basicos |
| `sexo_asignado` | `CHAR(1)` | `H`, `M` o `X` |

## Formato recomendado de archivo

- Tipo: CSV
- Codificacion: UTF-8
- Separador: coma
- Encapsulado de texto: comillas dobles si hay comas

## Ejemplo

```csv
curp,nombre_completo,tipo_evento,fecha_evento,descripcion_lugar,direccion_completa,telefono,correo,fecha_creacion,fecha_modificacion,lugar_nacimiento,sexo_asignado
TEST010101HDFABC01,JUAN PEREZ LOPEZ,ALTA_PADRON,2024-11-10,Modulo central CDMX,"Av. Reforma 123, Cuauhtemoc, CDMX",5512345678,juan.perez@example.com,2024-11-10 09:30:00,2025-01-15 10:00:00,CDMX,H
MOPR800101MDFXYZ09,MARIA ORTIZ PRADO,REGISTRO_BENEFICIO,2025-02-03,Oficina Oriente,"Calle Sur 200, Iztapalapa, CDMX",5587654321,maria.ortiz@example.com,2025-02-03 11:15:00,2025-02-10 16:20:00,CDMX,M
```

## Validaciones previas recomendadas

- Eliminar CURP nulas o mal formadas.
- Convertir CURP a mayusculas.
- Quitar espacios extra en nombres.
- Normalizar fechas invalidas.
- Evitar `tipo_evento` vacio.
