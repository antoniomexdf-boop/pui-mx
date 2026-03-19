/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para JwtService.
 * Verifica generación, validación y expiración de tokens.
 */
@DisplayName("JwtService – Generación y validación de JWT")
class JwtServiceTest {

    private JwtService jwtService;

    // Clave de prueba >= 32 caracteres (256 bits para HMAC-SHA256)
    private static final String SECRET = "ClaveSecretaDePruebaParaTestsJWT!!";
    // 1 hora en milisegundos
    private static final long EXPIRACION = 3_600_000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRACION);
    }

    @Test
    @DisplayName("Token generado es válido de inmediato")
    void generarToken_esValido() {
        String token = jwtService.generarToken("PUI");
        assertThat(jwtService.esValido(token)).isTrue();
    }

    @Test
    @DisplayName("Se extrae correctamente el usuario del token")
    void extraerUsuario_devuelveUsuarioCorrecto() {
        String token = jwtService.generarToken("PUI");
        assertThat(jwtService.extraerUsuario(token)).isEqualTo("PUI");
    }

    @Test
    @DisplayName("Token expirado no es válido")
    void tokenExpirado_noEsValido() {
        // Token con expiración de 1 ms
        JwtService serviceExpiracionInmediata = new JwtService(SECRET, 1L);
        String token = serviceExpiracionInmediata.generarToken("PUI");

        // Esperar que expire
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        assertThat(serviceExpiracionInmediata.esValido(token)).isFalse();
    }

    @Test
    @DisplayName("Token manipulado no es válido")
    void tokenManipulado_noEsValido() {
        String token = jwtService.generarToken("PUI");
        // Alterar la firma (última sección del JWT)
        String[] partes = token.split("\\.");
        String tokenAlterado = partes[0] + "." + partes[1] + ".firmaInvalida";

        assertThat(jwtService.esValido(tokenAlterado)).isFalse();
    }

    @Test
    @DisplayName("String vacío no es válido")
    void stringVacio_noEsValido() {
        assertThat(jwtService.esValido("")).isFalse();
        assertThat(jwtService.esValido("   ")).isFalse();
    }

    @Test
    @DisplayName("Token firmado con otra clave no es válido")
    void tokenOtraClave_noEsValido() {
        JwtService otraInstancia = new JwtService("OtraClaveCompletamenteDistintaXX!!", EXPIRACION);
        String tokenAjeno = otraInstancia.generarToken("PUI");

        assertThat(jwtService.esValido(tokenAjeno)).isFalse();
    }
}
