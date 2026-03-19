-- @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
-- @license   https://opensource.org/licenses/MIT MIT License.
-- @repository https://github.com/antoniomexdf-boop/pui-mx

CREATE SCHEMA IF NOT EXISTS datos;

CREATE TABLE IF NOT EXISTS datos.registros_institucionales (
  id_registro BIGINT AUTO_INCREMENT PRIMARY KEY,
  curp CHAR(18) NOT NULL,
  nombre_completo VARCHAR(200),
  tipo_evento VARCHAR(500) NOT NULL,
  fecha_evento DATE NOT NULL,
  descripcion_lugar VARCHAR(500),
  direccion_completa VARCHAR(500),
  telefono VARCHAR(15),
  correo VARCHAR(100),
  fecha_creacion TIMESTAMP NOT NULL,
  fecha_modificacion TIMESTAMP,
  lugar_nacimiento VARCHAR(20),
  sexo_asignado CHAR(1)
);

CREATE INDEX IF NOT EXISTS idx_registros_curp ON datos.registros_institucionales(curp);
CREATE INDEX IF NOT EXISTS idx_registros_fecha ON datos.registros_institucionales(curp, fecha_evento);
CREATE INDEX IF NOT EXISTS idx_registros_creacion ON datos.registros_institucionales(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_registros_modificacion ON datos.registros_institucionales(fecha_modificacion);
