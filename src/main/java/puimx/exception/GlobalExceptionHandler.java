/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.exception;

import lombok.extern.slf4j.Slf4j;
import puimx.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones.
 *
 * Garantiza el cumplimiento de la Sección 10 del Manual Técnico:
 * "No deben exponerse stacktraces, mensajes internos ni información
 * del framework o rutas del servidor."
 *
 * También implementa:
 * - HTTP 405 para métodos no permitidos
 * - HTTP 400 para campos inválidos (con detalle de cada campo)
 * - Sin generar errores 500 por entradas malformadas
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Error de validación de Bean Validation (@Valid).
     * Devuelve HTTP 400 con la lista de campos inválidos.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> "'" + fe.getField() + "': " + fe.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("[AUDIT] Error de validación: {}", errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errores", errores));
    }

    /**
     * JSON malformado o body no legible.
     * Devuelve HTTP 400 sin exponer detalles del parser.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleMalformedJson(
            HttpMessageNotReadableException ex) {

        log.warn("[AUDIT] JSON malformado recibido");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto("La solicitud contiene datos con formato incorrecto"));
    }

    /**
     * Método HTTP no permitido en el endpoint.
     * Devuelve HTTP 405 como requiere la Sección 10:
     * "Métodos no usados deben responder con 405 Method Not Allowed."
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {

        log.warn("[AUDIT] Método HTTP no permitido: {}", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ErrorResponseDto("Método HTTP no permitido"));
    }

    /**
     * Cualquier excepción no controlada.
     * Devuelve HTTP 500 SIN exponer información interna.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        // Solo loguear internamente, nunca exponer al cliente
        log.error("[AUDIT] Error interno no controlado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto("Error interno al procesar la solicitud"));
    }
}
