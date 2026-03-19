-- @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
-- @license   https://opensource.org/licenses/MIT MIT License.
-- @repository https://github.com/antoniomexdf-boop/pui-mx

INSERT INTO audit.interacciones_pui
  (timestamp_utc, tipo_operacion, reporte_id, curp, fase_busqueda, resultado_http, ip_origen, detalle)
SELECT CURRENT_TIMESTAMP, 'DEMO_INICIALIZACION', 'DEMO-BOOTSTRAP', 'DEMO010101HDFABC01', '1', 200, 'bootstrap',
       'Inicializacion automatica del demo'
WHERE NOT EXISTS (
  SELECT 1 FROM audit.interacciones_pui WHERE tipo_operacion = 'DEMO_INICIALIZACION'
);
