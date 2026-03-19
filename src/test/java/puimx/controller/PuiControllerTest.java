/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import puimx.dto.ActivarReporteRequestDto;
import puimx.dto.DesactivarReporteRequestDto;
import puimx.dto.LoginRequestDto;
import puimx.service.ReporteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para PuiController.
 * Verifica los 4 endpoints con casos de éxito y error.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "pui.usuario=PUI",
    "pui.clave=Pui@Test2025Clave!!",
    "pui.api.base-url=http://localhost:9999",
    "pui.api.institucion-id=TEST123ABC001",
    "pui.api.clave=ClaveTestPUI123!!",
    "pui.biometricos.clave=ClaveTestBiometricos123!!",
    "jwt.secret=ClaveSecretaDeTestParaJWT32chars!!",
    "jwt.expiration-ms=3600000"
})
@DisplayName("PuiController – Tests de integración")
class PuiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReporteService reporteService;

    private String tokenValido;

    @BeforeEach
    void obtenerToken() throws Exception {
        // Autenticarse para obtener token reutilizable en los demás tests
        LoginRequestDto login = new LoginRequestDto("PUI", "Pui@Test2025Clave!!");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        // Extraer el token del JSON {"token":"..."}
        tokenValido = objectMapper.readTree(body).get("token").asText();
    }

    // ─────────────────────────────────────────────
    // /login
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /login con credenciales válidas → 200 + token")
    void login_credencialesValidas_devuelveToken() throws Exception {
        LoginRequestDto req = new LoginRequestDto("PUI", "Pui@Test2025Clave!!");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /login con clave incorrecta → 401")
    void login_claveIncorrecta_devuelve401() throws Exception {
        LoginRequestDto req = new LoginRequestDto("PUI", "ClaveEquivocada123!!");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("GET /login → 401 cuando no hay token y el acceso es rechazado por seguridad")
    void login_metodoGet_devuelve401() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /login sin body → 400")
    void login_sinBody_devuelve400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").isArray());
    }

    // ─────────────────────────────────────────────
    // /activar-reporte
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /activar-reporte con datos válidos → 200")
    void activarReporte_datosValidos_devuelve200() throws Exception {
        doNothing().when(reporteService).activarReporte(any(), anyString());

        mockMvc.perform(post("/activar-reporte")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /activar-reporte sin token → 401")
    void activarReporte_sinToken_devuelve401() throws Exception {
        mockMvc.perform(post("/activar-reporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /activar-reporte con CURP inválida → 400")
    void activarReporte_curpInvalida_devuelve400() throws Exception {
        ActivarReporteRequestDto req = crearRequestValido();
        req.setCurp("CURP_INVALIDA");

        mockMvc.perform(post("/activar-reporte")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    @DisplayName("POST /activar-reporte con id corto → 400")
    void activarReporte_idCorto_devuelve400() throws Exception {
        ActivarReporteRequestDto req = crearRequestValido();
        req.setId("ID_MUY_CORTO");

        mockMvc.perform(post("/activar-reporte")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // /activar-reporte-prueba
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /activar-reporte-prueba con datos válidos → 200 (no persiste)")
    void activarReportePrueba_datosValidos_devuelve200() throws Exception {
        mockMvc.perform(post("/activar-reporte-prueba")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearRequestValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    // ─────────────────────────────────────────────
    // /desactivar-reporte
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /desactivar-reporte con id válido → 200")
    void desactivarReporte_idValido_devuelve200() throws Exception {
        when(reporteService.desactivarReporte(any(), anyString())).thenReturn(true);

        DesactivarReporteRequestDto req = new DesactivarReporteRequestDto(
                "e7b5a4c2-9f4e-4a99-91a2-6d4a8a1eaf3d-550e8400-e29b-41d4-a716-446655440000");

        mockMvc.perform(post("/desactivar-reporte")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("POST /desactivar-reporte sin id → 400")
    void desactivarReporte_sinId_devuelve400() throws Exception {
        mockMvc.perform(post("/desactivar-reporte")
                        .header("Authorization", "Bearer " + tokenValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────

    private ActivarReporteRequestDto crearRequestValido() {
        ActivarReporteRequestDto r = new ActivarReporteRequestDto();
        r.setId("A1B2C3D4E5F6A1B2-550e8400-e29b-41d4-a716-446655440000");
        r.setCurp("TEST010101HDFABC01");
        r.setNombre("JUAN");
        r.setPrimer_apellido("PEREZ");
        r.setSegundo_apellido("LOPEZ");
        r.setFecha_nacimiento("1990-01-01");
        r.setFecha_desaparicion("2024-12-15");
        r.setLugar_nacimiento("CDMX");
        r.setSexo_asignado("H");
        return r;
    }
}
