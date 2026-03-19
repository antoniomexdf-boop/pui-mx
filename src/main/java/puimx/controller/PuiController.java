/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import puimx.dto.*;
import puimx.security.JwtService;
import puimx.service.ReporteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador que expone los endpoints que la PUI consumirá.
 *
 * URL base de ejemplo: https://api.institucion.gob.mx/pui
 * Todos los paths se concatenan directamente (Sección 8 del Manual Técnico):
 *   - POST <URL_BASE>/login
 *   - POST <URL_BASE>/activar-reporte
 *   - POST <URL_BASE>/activar-reporte-prueba
 *   - POST <URL_BASE>/desactivar-reporte
 *
 * Todos los endpoints usan UTF-8 y application/json (Raw JSON).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class PuiController {

    private final JwtService jwtService;
    private final ReporteService reporteService;

    /** Usuario fijo que usa la PUI para autenticarse (siempre "PUI") */
    @Value("${pui.usuario}")
    private String puiUsuario;

    /** Clave configurada para la PUI en esta institución */
    @Value("${pui.clave}")
    private String puiClave;

    // ================================================================
    // ENDPOINT 1: /login
    // Sección 8.1 - La PUI obtiene un JWT para usar en los demás endpoints
    // ================================================================

    /**
     * La PUI envía usuario="PUI" y la clave acordada.
     * Si son correctas, devuelve un JWT con expiración de 1 hora.
     *
     * HTTP 200 → token generado
     * HTTP 401 → credenciales inválidas
     * HTTP 400 → campos con formato incorrecto
     */
    @PostMapping(value = "/login",
                 consumes = "application/json;charset=UTF-8",
                 produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {

        log.info("[AUDIT] Intento de login - usuario: {}", request.getUsuario());

        // Validar que el usuario sea exactamente "PUI"
        if (!puiUsuario.equals(request.getUsuario())) {
            log.warn("[AUDIT] Login fallido - usuario incorrecto: {}", request.getUsuario());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto("Credenciales inválidas"));
        }

        // Validar la clave
        if (!puiClave.equals(request.getClave())) {
            log.warn("[AUDIT] Login fallido - clave incorrecta para usuario PUI");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDto("Credenciales inválidas"));
        }

        String token = jwtService.generarToken(request.getUsuario());
        log.info("[AUDIT] Login exitoso - token generado para usuario PUI");

        return ResponseEntity.ok(new TokenResponseDto(token));
    }

    // ================================================================
    // ENDPOINT 2: /activar-reporte
    // Sección 8.2 - La PUI notifica un nuevo reporte de búsqueda
    // ================================================================

    /**
     * La PUI envía los datos de una persona desaparecida o no localizada.
     * La institución debe persistir el reporte e iniciar el proceso de
     * búsqueda en 3 fases (Sección 6 del Manual Técnico).
     *
     * HTTP 200 → reporte recibido correctamente
     * HTTP 400 → datos inválidos o formato incorrecto
     * HTTP 401 → token inválido o expirado
     * HTTP 403 → sin permisos
     * HTTP 500 → error interno
     * HTTP 504 → timeout (configurado en servidor)
     */
    @PostMapping(value = "/activar-reporte",
                 consumes = "application/json;charset=UTF-8",
                 produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> activarReporte(
            @Valid @RequestBody ActivarReporteRequestDto request,
            HttpServletRequest httpRequest) {

        log.info("[AUDIT] Activar reporte - id: {} | curp: {}",
                request.getId(), request.getCurp());

        try {
            reporteService.activarReporte(request, httpRequest.getRemoteAddr());
            return ResponseEntity.ok(
                new MensajeResponseDto(
                    "La solicitud de activación del reporte de búsqueda se recibió correctamente."));

        } catch (Exception e) {
            log.error("[AUDIT] Error al activar reporte - id: {} | error: {}",
                    request.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Error interno al procesar la solicitud"));
        }
    }

    // ================================================================
    // ENDPOINT 3: /activar-reporte-prueba
    // Sección 8.3 - Valida la integración antes de producción
    // ================================================================

    /**
     * Endpoint de prueba para verificar:
     * 1. Conectividad con el webhook registrado
     * 2. Estructura del payload de la PUI
     * 3. Esquema de autenticación Bearer Token
     * 4. Validación de campos obligatorios
     *
     * Tiene el mismo contrato que /activar-reporte pero NO persiste datos
     * ni inicia búsquedas reales. Solo confirma que la integración funciona.
     */
    @PostMapping(value = "/activar-reporte-prueba",
                 consumes = "application/json;charset=UTF-8",
                 produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> activarReportePrueba(
            @Valid @RequestBody ActivarReporteRequestDto request,
            HttpServletRequest httpRequest) {

        log.info("[AUDIT][PRUEBA] Activar reporte prueba - id: {} | curp: {}",
                request.getId(), request.getCurp());

        reporteService.registrarPrueba(request, httpRequest.getRemoteAddr());

        return ResponseEntity.ok(
            new MensajeResponseDto(
                "La solicitud de activación del reporte de búsqueda se recibió correctamente."));
    }

    // ================================================================
    // ENDPOINT 4: /desactivar-reporte
    // Sección 8.4 - La PUI notifica que una persona fue localizada
    // ================================================================

    /**
     * La CNB localizó a la persona: la PUI envía únicamente el 'id'
     * para que la institución detenga la búsqueda continua y limpie
     * su estado interno.
     *
     * HTTP 200 → baja registrada correctamente
     * HTTP 400 → id inválido
     * HTTP 401 → token inválido o expirado
     * HTTP 500 → error interno
     */
    @PostMapping(value = "/desactivar-reporte",
                 consumes = "application/json;charset=UTF-8",
                 produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> desactivarReporte(
            @Valid @RequestBody DesactivarReporteRequestDto request,
            HttpServletRequest httpRequest) {

        log.info("[AUDIT] Desactivar reporte - id: {}", request.getId());

        try {
            reporteService.desactivarReporte(request.getId(), httpRequest.getRemoteAddr());
            return ResponseEntity.ok(
                new MensajeResponseDto(
                    "Registro de finalización de búsqueda histórica guardado correctamente"));

        } catch (Exception e) {
            log.error("[AUDIT] Error al desactivar reporte - id: {} | error: {}",
                    request.getId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("Error interno al procesar la solicitud"));
        }
    }
}
