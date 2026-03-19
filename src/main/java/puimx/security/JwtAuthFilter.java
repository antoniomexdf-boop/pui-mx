/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filtro que se ejecuta una vez por request para validar el Bearer token JWT.
 *
 * Si el token es válido, carga la autenticación en el SecurityContext
 * permitiendo que Spring Security autorice el acceso al endpoint.
 *
 * Sección 8.1 y Sección 10 (Verificación de Autenticación) del Manual Técnico.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay header o no es Bearer, continuar sin autenticar.
        // Spring Security rechazará los endpoints protegidos con 401.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); // quitar "Bearer "

        if (jwtService.esValido(token)) {
            String usuario = jwtService.extraerUsuario(token);

            // Registrar autenticación en el contexto de seguridad
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            usuario,
                            null,
                            Collections.emptyList()  // sin roles adicionales
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("[AUDIT] Token válido para usuario '{}' - IP: {} - Path: {}",
                    usuario,
                    request.getRemoteAddr(),
                    request.getRequestURI());
        } else {
            log.warn("[AUDIT] Token inválido o expirado - IP: {} - Path: {}",
                    request.getRemoteAddr(),
                    request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }
}
