/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import puimx.client.PuiApiClient;
import puimx.client.dto.DomicilioDto;
import puimx.client.dto.NombreCompletoDto;
import puimx.client.dto.NotificarCoincidenciaRequest;
import puimx.model.RegistroInstitucional;
import puimx.model.ReporteActivo;
import puimx.repository.RegistroInstitucionalRepository;
import puimx.repository.ReporteActivoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusquedaService {

    private final PuiApiClient puiApiClient;
    private final RegistroInstitucionalRepository registroRepository;
    private final ReporteActivoRepository reporteRepo;
    private final AuditoriaService auditoriaService;

    @Value("${pui.api.institucion-id}")
    private String institucionId;

    @Transactional("platformTransactionManager")
    public void ejecutarFase1(String curp, String id) {
        log.info("[BUSQUEDA] Iniciando Fase 1 - id: {} | curp: {}", id, curp);
        Optional<RegistroInstitucional> registro = registroRepository.buscarMasRecientePorCurp(curp);

        if (registro.isEmpty()) {
            auditoriaService.registrar(
                    "FASE_1_SIN_COINCIDENCIA",
                    id,
                    curp,
                    "1",
                    204,
                    "localhost",
                    "No se encontro informacion basica para la CURP");
            marcarFase1(id, false);
            return;
        }

        boolean ok = puiApiClient.notificarCoincidencia(construirRequestFase1(id, curp, registro.get()));
        marcarFase1(id, ok);
    }

    @Transactional("platformTransactionManager")
    public void ejecutarFase2(String curp, String id) {
        ReporteActivo reporte = reporteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));

        LocalDate fechaDesaparicion = reporte.getFechaDesaparicion();
        if (fechaDesaparicion == null) {
            auditoriaService.registrar(
                    "FASE_2_OMITIDA",
                    id,
                    curp,
                    "2",
                    204,
                    "localhost",
                    "Reporte sin fecha de desaparicion; se omite historico");
            finalizarFase2(reporte);
            return;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate desde = fechaDesaparicion.isBefore(hoy.minusYears(12))
                ? hoy.minusYears(12)
                : fechaDesaparicion;

        List<RegistroInstitucional> eventos = registroRepository.buscarHistorico(curp, desde, hoy);
        for (RegistroInstitucional evento : eventos) {
            puiApiClient.notificarCoincidencia(construirRequestFaseEvento(id, curp, "2", evento));
        }

        auditoriaService.registrar(
                "FASE_2_EJECUTADA",
                id,
                curp,
                "2",
                200,
                "localhost",
                "Coincidencias historicas encontradas: " + eventos.size());

        finalizarFase2(reporte);
    }

    @Transactional("platformTransactionManager")
    public void verificarBusquedaContinua(String curp, String id) {
        ReporteActivo reporte = reporteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado: " + id));

        LocalDateTime ultimaVerificacion = reporte.getUltimaVerificacion() == null
                ? reporte.getFechaAlta()
                : reporte.getUltimaVerificacion();

        List<RegistroInstitucional> nuevos = registroRepository.buscarNuevosOModificados(curp, ultimaVerificacion);
        for (RegistroInstitucional evento : nuevos) {
            puiApiClient.notificarCoincidencia(construirRequestFaseEvento(id, curp, "3", evento));
        }

        reporte.setUltimaVerificacion(LocalDateTime.now());
        reporteRepo.save(reporte);
        auditoriaService.registrar(
                "FASE_3_VERIFICACION",
                id,
                curp,
                "3",
                200,
                "localhost",
                "Registros nuevos/modificados detectados: " + nuevos.size());
    }

    private void marcarFase1(String id, boolean completada) {
        reporteRepo.findById(id).ifPresent(reporte -> {
            reporte.setFase1Completada(completada);
            reporteRepo.save(reporte);
        });
    }

    private void finalizarFase2(ReporteActivo reporte) {
        boolean finalizado = puiApiClient.notificarBusquedaFinalizada(reporte.getId(), institucionId);
        reporte.setFase2Completada(finalizado);
        reporte.setUltimaVerificacion(LocalDateTime.now());
        reporteRepo.save(reporte);
    }

    private NotificarCoincidenciaRequest construirRequestFase1(
            String id, String curp, RegistroInstitucional registro) {
        NombreCompletoDto nombreCompleto = dividirNombre(registro.getNombreCompleto());
        return NotificarCoincidenciaRequest.builder()
                .curp(curp)
                .id(id)
                .institucion_id(institucionId)
                .lugar_nacimiento(valorSeguro(registro.getLugarNacimiento(), "DESCONOCIDO"))
                .fase_busqueda("1")
                .nombre_completo(nombreCompleto)
                .telefono(registro.getTelefono())
                .correo(registro.getCorreo())
                .sexo_asignado(registro.getSexoAsignado())
                .domicilio(DomicilioDto.builder()
                        .direccion(registro.getDireccionCompleta())
                        .entidad_federativa(valorSeguro(registro.getLugarNacimiento(), "DESCONOCIDO"))
                        .build())
                .build();
    }

    private NotificarCoincidenciaRequest construirRequestFaseEvento(
            String id, String curp, String fase, RegistroInstitucional registro) {
        return NotificarCoincidenciaRequest.builder()
                .curp(curp)
                .id(id)
                .institucion_id(institucionId)
                .lugar_nacimiento(valorSeguro(registro.getLugarNacimiento(), "DESCONOCIDO"))
                .fase_busqueda(fase)
                .nombre_completo(dividirNombre(registro.getNombreCompleto()))
                .telefono(registro.getTelefono())
                .correo(registro.getCorreo())
                .sexo_asignado(registro.getSexoAsignado())
                .tipo_evento(registro.getTipoEvento())
                .fecha_evento(registro.getFechaEvento() != null ? registro.getFechaEvento().toString() : null)
                .descripcion_lugar_evento(registro.getDescripcionLugar())
                .direccion_evento(DomicilioDto.builder()
                        .direccion(registro.getDireccionCompleta())
                        .entidad_federativa(valorSeguro(registro.getLugarNacimiento(), "DESCONOCIDO"))
                        .build())
                .build();
    }

    private NombreCompletoDto dividirNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return NombreCompletoDto.builder().build();
        }

        String[] partes = nombreCompleto.trim().split("\\s+");
        String nombre = partes.length > 0 ? partes[0] : null;
        String primerApellido = partes.length > 1 ? partes[1] : null;
        String segundoApellido = partes.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(partes, 2, partes.length)) : null;

        return NombreCompletoDto.builder()
                .nombre(nombre)
                .primer_apellido(primerApellido)
                .segundo_apellido(segundoApellido)
                .build();
    }

    private String valorSeguro(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor;
    }
}
