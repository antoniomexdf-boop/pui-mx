/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import puimx.dto.ActivarReporteRequestDto;
import puimx.model.ReporteActivo;
import puimx.repository.ReporteActivoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del ReporteService con mocks de repositorio y BusquedaService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReporteService – Lógica de negocio")
class ReporteServiceTest {

    @Mock
    private ReporteActivoRepository reporteRepo;

    @Mock
    private ReporteAsyncService reporteAsyncService;

    @Mock
    private AuditoriaService auditoriaService;

    @InjectMocks
    private ReporteService reporteService;

    // ─────────────────────────────────────────────
    // activarReporte
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("activarReporte guarda entidad con datos correctos")
    void activarReporte_guardaEntidadCorrecta() {
        ActivarReporteRequestDto dto = crearDto();
        when(reporteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        reporteService.activarReporte(dto, "127.0.0.1");

        ArgumentCaptor<ReporteActivo> captor = ArgumentCaptor.forClass(ReporteActivo.class);
        verify(reporteRepo).save(captor.capture());

        ReporteActivo guardado = captor.getValue();
        assertThat(guardado.getId()).isEqualTo(dto.getId());
        assertThat(guardado.getCurp()).isEqualTo(dto.getCurp());
        assertThat(guardado.getLugarNacimiento()).isEqualTo(dto.getLugar_nacimiento());
        assertThat(guardado.isActivo()).isTrue();
    }

    @Test
    @DisplayName("activarReporte llama save exactamente una vez")
    void activarReporte_llamaSaveUnaVez() {
        when(reporteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        reporteService.activarReporte(crearDto(), "127.0.0.1");

        verify(reporteRepo, times(1)).save(any());
    }

    // ─────────────────────────────────────────────
    // desactivarReporte
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("desactivarReporte marca activo=false y registra fecha de baja")
    void desactivarReporte_marcaInactivo() {
        ReporteActivo reporte = new ReporteActivo();
        reporte.setId("ID123-uuid");
        reporte.setCurp("TEST010101HDFABC01");
        reporte.setActivo(true);
        reporte.setLugarNacimiento("CDMX");

        when(reporteRepo.findById("ID123-uuid")).thenReturn(Optional.of(reporte));
        when(reporteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        boolean resultado = reporteService.desactivarReporte("ID123-uuid", "127.0.0.1");

        assertThat(resultado).isTrue();
        assertThat(reporte.isActivo()).isFalse();
        assertThat(reporte.getFechaBaja()).isNotNull();
        verify(reporteRepo).save(reporte);
    }

    @Test
    @DisplayName("desactivarReporte con id inexistente devuelve true (no error para la PUI)")
    void desactivarReporte_idInexistente_devuelveTrue() {
        when(reporteRepo.findById("NO_EXISTE")).thenReturn(Optional.empty());

        boolean resultado = reporteService.desactivarReporte("NO_EXISTE", "127.0.0.1");

        assertThat(resultado).isTrue();
        verify(reporteRepo, never()).save(any());
    }

    @Test
    @DisplayName("desactivarReporte ya inactivo devuelve true sin modificar")
    void desactivarReporte_yaInactivo_devuelveTrueSinGuardar() {
        ReporteActivo reporte = new ReporteActivo();
        reporte.setId("ID-inactivo");
        reporte.setActivo(false);
        reporte.setLugarNacimiento("CDMX");
        reporte.setCurp("TEST010101HDFABC01");

        when(reporteRepo.findById("ID-inactivo")).thenReturn(Optional.of(reporte));

        boolean resultado = reporteService.desactivarReporte("ID-inactivo", "127.0.0.1");

        assertThat(resultado).isTrue();
        // No debe volver a guardar si ya estaba inactivo
        verify(reporteRepo, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private ActivarReporteRequestDto crearDto() {
        ActivarReporteRequestDto dto = new ActivarReporteRequestDto();
        dto.setId("A1B2C3D4E5F6A1B2-550e8400-e29b-41d4-a716-446655440000");
        dto.setCurp("TEST010101HDFABC01");
        dto.setNombre("JUAN");
        dto.setPrimer_apellido("PEREZ");
        dto.setSegundo_apellido("LOPEZ");
        dto.setFecha_nacimiento("1990-01-01");
        dto.setFecha_desaparicion("2024-12-15");
        dto.setLugar_nacimiento("CDMX");
        dto.setSexo_asignado("H");
        return dto;
    }
}
