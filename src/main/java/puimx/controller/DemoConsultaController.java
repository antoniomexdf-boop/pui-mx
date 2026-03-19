/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.controller;

import lombok.RequiredArgsConstructor;
import puimx.model.RegistroInstitucional;
import puimx.service.AuditoriaService;
import puimx.service.ReporteService;
import puimx.repository.RegistroInstitucionalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoConsultaController {

    private final ReporteService reporteService;
    private final AuditoriaService auditoriaService;
    private final RegistroInstitucionalRepository registroRepository;

    @GetMapping("/reportes")
    public ResponseEntity<?> reportes() {
        return ResponseEntity.ok(reporteService.obtenerReportesActivos());
    }

    @GetMapping("/auditoria")
    public ResponseEntity<?> auditoria() {
        return ResponseEntity.ok(auditoriaService.obtenerUltimosLogs());
    }

    @GetMapping("/registros")
    public ResponseEntity<?> registros() {
        return ResponseEntity.ok(registroRepository.buscarTodos());
    }

    @GetMapping("/registros/{curp}")
    public ResponseEntity<?> registrosPorCurp(@PathVariable String curp) {
        return ResponseEntity.ok(registroRepository.buscarPorCurp(curp));
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> resumen() {
        var reportes = reporteService.obtenerReportesActivos();
        var logs = auditoriaService.obtenerUltimosLogs();
        var registros = registroRepository.buscarTodos();

        long activos = reportes.stream().filter(r -> r.isActivo()).count();
        long fase1Completada = reportes.stream().filter(r -> r.isFase1Completada()).count();
        long fase2Completada = reportes.stream().filter(r -> r.isFase2Completada()).count();

        return ResponseEntity.ok(Map.of(
                "totalReportes", reportes.size(),
                "reportesActivos", activos,
                "fase1Completada", fase1Completada,
                "fase2Completada", fase2Completada,
                "totalRegistrosInstitucionales", registros.size(),
                "ultimosEventos", logs.size()
        ));
    }
}
