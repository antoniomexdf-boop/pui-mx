/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import puimx.model.ReporteActivo;
import puimx.repository.ReporteActivoRepository;
import puimx.service.BusquedaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler que implementa la FASE 3 del flujo PUI: Búsqueda Continua.
 *
 * Según el Manual Técnico (Sección 6, paso 4c):
 * - Revisa periódicamente si hay registros nuevos o modificados.
 * - Reporta cada coincidencia con fase_busqueda="3".
 * - Se detiene solo cuando se recibe /desactivar-reporte.
 * - Frecuencia recomendada: cada hora, cada 4 horas o una vez al día.
 *
 * La frecuencia se configura en application.yml con:
 *   pui.scheduler.cron=0 0 * * * *   (cada hora)
 *
 * Para habilitar el scheduler, asegurarse de tener
 * @EnableScheduling en la clase principal o en esta misma clase.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.scheduling.annotation.EnableScheduling
public class BusquedaContinuaScheduler {

    private final ReporteActivoRepository reporteRepo;
    private final BusquedaService busquedaService;

    /**
     * Ejecuta la búsqueda continua para todos los reportes activos.
     *
     * Configuración del cron en application.yml:
     *   pui.scheduler.cron: "0 0 * * * *"   ← cada hora (recomendado)
     *   pui.scheduler.cron: "0 0 cada-4-horas * * *"  ejemplo conceptual para 4 horas
     *   pui.scheduler.cron: "0 0 1 * * *"    ← una vez al día a la 1am
     *
     * Por defecto ejecuta cada hora.
     */
    @Scheduled(cron = "${pui.scheduler.cron:0 0 * * * *}")
    public void ejecutarBusquedaContinua() {
        List<ReporteActivo> reportesActivos = reporteRepo.findByActivoTrue();

        if (reportesActivos.isEmpty()) {
            log.debug("[SCHEDULER] No hay reportes activos para búsqueda continua.");
            return;
        }

        log.info("[SCHEDULER] Iniciando búsqueda continua (Fase 3) - {} reportes activos",
                reportesActivos.size());

        int procesados = 0;
        int errores = 0;

        for (ReporteActivo reporte : reportesActivos) {
            try {
                busquedaService.verificarBusquedaContinua(
                        reporte.getCurp(),
                        reporte.getId());
                procesados++;
            } catch (Exception e) {
                errores++;
                log.error("[SCHEDULER] Error en búsqueda continua - id: {} | curp: {} | error: {}",
                        reporte.getId(), reporte.getCurp(), e.getMessage());
                // Continuar con el siguiente reporte sin detener el batch
            }
        }

        log.info("[SCHEDULER] Búsqueda continua completada - procesados: {} | errores: {}",
                procesados, errores);
    }
}
