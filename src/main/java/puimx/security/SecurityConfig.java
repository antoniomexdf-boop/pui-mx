/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuración de seguridad de Spring Security.
 *
 * Implementa los requisitos de la Sección 10 del Manual Técnico:
 * - Autenticación JWT sin sesiones (stateless)
 * - Solo los métodos HTTP correctos habilitados por endpoint
 * - Cabeceras de seguridad: HSTS, CSP, X-Frame-Options, X-Content-Type-Options
 * - No se expone información interna en errores
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Sin estado: no se crean sesiones (JWT es suficiente)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Deshabilitar CSRF (no aplica para APIs REST sin cookies de sesión)
            .csrf(AbstractHttpConfigurer::disable)

            // --- Control de acceso por endpoint y método HTTP ---
            // Sección 10: "Únicamente deben estar habilitados los métodos
            // para los que funciona el endpoint"
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/dashboard.html", "/assets/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/demo/**").permitAll()

                // /login solo acepta POST (sin autenticación previa)
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                // Cualquier otro método a /login → rechazado
                .requestMatchers("/login").denyAll()

                // Los demás endpoints requieren autenticación JWT
                .requestMatchers(HttpMethod.POST,
                        "/activar-reporte",
                        "/activar-reporte-prueba",
                        "/desactivar-reporte").authenticated()

                // Rechazar cualquier método diferente a POST en esos endpoints
                .requestMatchers(
                        "/activar-reporte",
                        "/activar-reporte-prueba",
                        "/desactivar-reporte").denyAll()

                // Cualquier otra ruta no definida → denegada
                .anyRequest().denyAll()
            )

            // Cabeceras de seguridad (Sección 10 - Seguridad de Infraestructura)
            .headers(headers -> headers
                // Strict-Transport-Security: forzar HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000))

                // X-Content-Type-Options: nosniff
                .contentTypeOptions(ct -> {})

                // X-Frame-Options: DENY (anti-clickjacking)
                .frameOptions(frame -> frame.deny())

                // Content-Security-Policy: sin unsafe-inline ni unsafe-eval
                .contentSecurityPolicy(csp ->
                    csp.policyDirectives(
                        "default-src 'self'; " +
                        "connect-src 'self'; " +
                        "img-src 'self' data:; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "frame-ancestors 'none'"))

                // Referrer-Policy
                .referrerPolicy(referrer ->
                    referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
            )

            // Insertar filtro JWT antes del filtro de autenticación estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // Manejo limpio de errores 401/403 sin exponer detalles internos
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"Token inválido o expirado\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"Acceso no autorizado\"}");
                })
            );

        return http.build();
    }
}
