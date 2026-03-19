/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteAsyncService {

    private final BusquedaService busquedaService;

    @Async
    public void ejecutarFasesIniciales(String curp, String id) {
        log.info("[BUSQUEDA] Iniciando fases 1 y 2 - id: {}", id);
        try {
            busquedaService.ejecutarFase1(curp, id);
            busquedaService.ejecutarFase2(curp, id);
            log.info("[BUSQUEDA] Fases 1 y 2 completadas - id: {}", id);
        } catch (Exception e) {
            log.error("[BUSQUEDA] Error en fases iniciales - id: {} | {}", id, e.getMessage());
        }
    }
}
