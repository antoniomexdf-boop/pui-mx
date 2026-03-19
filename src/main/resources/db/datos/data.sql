-- @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
-- @license   https://opensource.org/licenses/MIT MIT License.
-- @repository https://github.com/antoniomexdf-boop/pui-mx

INSERT INTO datos.registros_institucionales
  (curp, nombre_completo, tipo_evento, fecha_evento, descripcion_lugar, direccion_completa,
   telefono, correo, fecha_creacion, fecha_modificacion, lugar_nacimiento, sexo_asignado)
SELECT 'TEST010101HDFABC01', 'JUAN PEREZ LOPEZ', 'ALTA_PADRON', DATE '2024-11-10', 'Modulo central CDMX',
       'Av. Reforma 123, Cuauhtemoc, CDMX', '5512345678', 'juan.perez@example.com',
       DATEADD('DAY', -10, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'CDMX', 'H'
WHERE NOT EXISTS (
  SELECT 1 FROM datos.registros_institucionales WHERE curp = 'TEST010101HDFABC01' AND tipo_evento = 'ALTA_PADRON'
);

INSERT INTO datos.registros_institucionales
  (curp, nombre_completo, tipo_evento, fecha_evento, descripcion_lugar, direccion_completa,
   telefono, correo, fecha_creacion, fecha_modificacion, lugar_nacimiento, sexo_asignado)
SELECT 'TEST010101HDFABC01', 'JUAN PEREZ LOPEZ', 'ACTUALIZACION_DOMICILIO', DATE '2025-01-12', 'Subdelegacion Norte',
       'Calle Norte 45, Gustavo A. Madero, CDMX', '5512345678', 'juan.perez@example.com',
       DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'CDMX', 'H'
WHERE NOT EXISTS (
  SELECT 1 FROM datos.registros_institucionales WHERE curp = 'TEST010101HDFABC01' AND tipo_evento = 'ACTUALIZACION_DOMICILIO'
);

INSERT INTO datos.registros_institucionales
  (curp, nombre_completo, tipo_evento, fecha_evento, descripcion_lugar, direccion_completa,
   telefono, correo, fecha_creacion, fecha_modificacion, lugar_nacimiento, sexo_asignado)
SELECT 'MOPR800101MDFXYZ09', 'MARIA ORTIZ PRADO', 'REGISTRO_BENEFICIO', DATE '2025-02-03', 'Oficina Oriente',
       'Calle Sur 200, Iztapalapa, CDMX', '5587654321', 'maria.ortiz@example.com',
       DATEADD('DAY', -8, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP), 'CDMX', 'M'
WHERE NOT EXISTS (
  SELECT 1 FROM datos.registros_institucionales WHERE curp = 'MOPR800101MDFXYZ09'
);
