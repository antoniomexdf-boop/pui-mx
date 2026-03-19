/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio para generación y validación de JWT.
 *
 * La institución emite tokens firmados que la PUI deberá incluir
 * en el header "Authorization: Bearer <token>" en cada solicitud.
 *
 * Duración del token: configurable en application.yml (por defecto 1 hora).
 * Sección 8.1 del Manual Técnico PUI.
 */
@Slf4j
@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        // La clave debe tener al menos 256 bits para HMAC-SHA256
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Genera un JWT firmado para el usuario autenticado.
     *
     * @param usuario nombre del usuario (siempre "PUI" para este flujo)
     * @return token JWT como String
     */
    public String generarToken(String usuario) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(usuario)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrae el subject (usuario) de un token válido.
     *
     * @param token JWT a parsear
     * @return nombre de usuario contenido en el token
     * @throws JwtException si el token es inválido o expirado
     */
    public String extraerUsuario(String token) {
        return parsearClaims(token).getSubject();
    }

    /**
     * Valida que el token sea auténtico y no haya expirado.
     *
     * @param token JWT a validar
     * @return true si es válido, false en caso contrario
     */
    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido o expirado: {}", e.getMessage());
            return false;
        }
    }

    // --- Privados ---

    private Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
