/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import puimx.dto.ActivarReporteRequestDto;
import puimx.model.ReporteActivo;
import puimx.repository.ReporteActivoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteActivoRepository reporteRepo;
    private final ReporteAsyncService reporteAsyncService;
    private final AuditoriaService auditoriaService;

    @Transactional("platformTransactionManager")
    public void activarReporte(ActivarReporteRequestDto dto, String ipOrigen) {
        log.info("[AUDIT] Activando reporte - id: {} | curp: {}", dto.getId(), dto.getCurp());

        ReporteActivo reporte = reporteRepo.findById(dto.getId()).orElseGet(ReporteActivo::new);
        mapearDtoAEntidad(dto, reporte);
        reporte.setActivo(true);
        reporte.setFechaBaja(null);
        reporteRepo.save(reporte);

        auditoriaService.registrar(
                "RECEPCION_ACTIVAR",
                dto.getId(),
                dto.getCurp(),
                null,
                200,
                ipOrigen,
                "Reporte recibido y persistido en plataforma");

        reporteAsyncService.ejecutarFasesIniciales(dto.getCurp(), dto.getId());
    }

    @Transactional("platformTransactionManager")
    public void registrarPrueba(ActivarReporteRequestDto dto, String ipOrigen) {
        auditoriaService.registrar(
                "RECEPCION_ACTIVAR_PRUEBA",
                dto.getId(),
                dto.getCurp(),
                null,
                200,
                ipOrigen,
                "Validacion de conectividad y contrato sin persistencia");
    }

    @Transactional("platformTransactionManager")
    public boolean desactivarReporte(String id, String ipOrigen) {
        return reporteRepo.findById(id).map(reporte -> {
            if (!reporte.isActivo()) {
                auditoriaService.registrar(
                        "DESACTIVAR",
                        id,
                        reporte.getCurp(),
                        null,
                        200,
                        ipOrigen,
                        "Reporte ya estaba inactivo");
                return true;
            }

            reporte.setActivo(false);
            reporte.setFechaBaja(LocalDateTime.now());
            reporteRepo.save(reporte);
            auditoriaService.registrar(
                    "DESACTIVAR",
                    id,
                    reporte.getCurp(),
                    null,
                    200,
                    ipOrigen,
                    "Busqueda continua detenida");
            return true;
        }).orElseGet(() -> {
            auditoriaService.registrar(
                    "DESACTIVAR",
                    id,
                    null,
                    null,
                    200,
                    ipOrigen,
                    "Solicitud recibida para reporte inexistente");
            return true;
        });
    }

    @Transactional(readOnly = true)
    public List<ReporteActivo> obtenerReportesActivos() {
        return reporteRepo.findAll();
    }

    private void mapearDtoAEntidad(ActivarReporteRequestDto dto, ReporteActivo r) {
        r.setId(dto.getId());
        r.setCurp(dto.getCurp());
        r.setNombre(dto.getNombre());
        r.setPrimerApellido(dto.getPrimer_apellido());
        r.setSegundoApellido(dto.getSegundo_apellido());
        r.setFechaNacimiento(parseFecha(dto.getFecha_nacimiento()));
        r.setFechaDesaparicion(parseFecha(dto.getFecha_desaparicion()));
        r.setLugarNacimiento(dto.getLugar_nacimiento());
        r.setSexoAsignado(dto.getSexo_asignado());
        r.setTelefono(dto.getTelefono());
        r.setCorreo(dto.getCorreo());
        r.setDireccion(dto.getDireccion());
        r.setCalle(dto.getCalle());
        r.setNumero(dto.getNumero());
        r.setColonia(dto.getColonia());
        r.setCodigoPostal(dto.getCodigo_postal());
        r.setMunicipioOAlcaldia(dto.getMunicipio_o_alcaldia());
        r.setEntidadFederativa(dto.getEntidad_federativa());
        if (r.getUltimaVerificacion() == null) {
            r.setUltimaVerificacion(LocalDateTime.now().minusMinutes(1));
        }
    }

    private LocalDate parseFecha(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
