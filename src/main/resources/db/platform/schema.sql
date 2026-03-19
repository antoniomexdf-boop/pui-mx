-- @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
-- @license   https://opensource.org/licenses/MIT MIT License.
-- @repository https://github.com/antoniomexdf-boop/pui-mx

CREATE SCHEMA IF NOT EXISTS pui;
CREATE SCHEMA IF NOT EXISTS audit;

CREATE TABLE IF NOT EXISTS pui.reportes_activos (
  id VARCHAR(75) PRIMARY KEY,
  curp CHAR(18) NOT NULL,
  nombre VARCHAR(50),
  primer_apellido VARCHAR(50),
  segundo_apellido VARCHAR(50),
  fecha_nacimiento DATE,
  fecha_desaparicion DATE,
  lugar_nacimiento VARCHAR(20) NOT NULL,
  sexo_asignado CHAR(1),
  telefono VARCHAR(15),
  correo VARCHAR(50),
  direccion VARCHAR(500),
  calle VARCHAR(50),
  numero VARCHAR(20),
  colonia VARCHAR(50),
  codigo_postal VARCHAR(5),
  municipio_o_alcaldia VARCHAR(100),
  entidad_federativa VARCHAR(40),
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  fase1_completada BOOLEAN NOT NULL DEFAULT FALSE,
  fase2_completada BOOLEAN NOT NULL DEFAULT FALSE,
  ultima_verificacion TIMESTAMP,
  fecha_alta TIMESTAMP NOT NULL,
  fecha_baja TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reportes_curp ON pui.reportes_activos(curp);
CREATE INDEX IF NOT EXISTS idx_reportes_activo ON pui.reportes_activos(activo);

CREATE TABLE IF NOT EXISTS audit.interacciones_pui (
  id_log BIGINT AUTO_INCREMENT PRIMARY KEY,
  timestamp_utc TIMESTAMP NOT NULL,
  tipo_operacion VARCHAR(50) NOT NULL,
  reporte_id VARCHAR(75),
  curp CHAR(18),
  fase_busqueda CHAR(1),
  resultado_http INT,
  ip_origen VARCHAR(45),
  detalle VARCHAR(4000)
);

CREATE INDEX IF NOT EXISTS idx_audit_reporte ON audit.interacciones_pui(reporte_id);
CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit.interacciones_pui(timestamp_utc);
