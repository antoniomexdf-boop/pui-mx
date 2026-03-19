/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import lombok.RequiredArgsConstructor;
import puimx.model.AuditoriaLog;
import puimx.repository.AuditoriaLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    @Transactional
    public void registrar(String tipoOperacion, String reporteId, String curp, String faseBusqueda,
                          Integer resultadoHttp, String ipOrigen, String detalle) {
        AuditoriaLog log = new AuditoriaLog();
        log.setTipoOperacion(tipoOperacion);
        log.setReporteId(reporteId);
        log.setCurp(curp);
        log.setFaseBusqueda(faseBusqueda);
        log.setResultadoHttp(resultadoHttp);
        log.setIpOrigen(ipOrigen);
        log.setDetalle(detalle);
        auditoriaLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaLog> obtenerUltimosLogs() {
        return auditoriaLogRepository.findTop100ByOrderByTimestampUtcDesc();
    }
}
