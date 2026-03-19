/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para BiometricosCifradoService.
 * Verifica que el ciclo cifrar → descifrar sea correcto y
 * que datos distintos produzcan ciphertext distinto (IV aleatorio).
 */
@DisplayName("BiometricosCifradoService – AES-256-GCM")
class BiometricosCifradoServiceTest {

    private BiometricosCifradoService service;

    @BeforeEach
    void setUp() {
        // Usar clave de prueba (en producción viene de application.yml)
        service = new BiometricosCifradoService("ClaveDeTestParaPUI_32chars!!");
    }

    @Test
    @DisplayName("Cifrar y descifrar recupera los bytes originales")
    void cifrarDescifrar_recuperaBytesOriginales() {
        byte[] original = "huella_dactilar_simulada_wsq".getBytes();

        String cifrado = service.cifrarBiometrico(original);
        byte[] recuperado = service.descifrarBiometrico(cifrado);

        assertThat(recuperado).isEqualTo(original);
    }

    @Test
    @DisplayName("El resultado cifrado es base64 válido y no vacío")
    void cifrar_devuelveBase64Valido() {
        byte[] foto = new byte[]{1, 2, 3, 4, 5};

        String cifrado = service.cifrarBiometrico(foto);

        assertThat(cifrado).isNotBlank();
        // Verificar que es base64 válido decodificándolo sin excepción
        assertThatCode(() -> java.util.Base64.getDecoder().decode(cifrado))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Dos cifrados del mismo dato producen resultados distintos (IV aleatorio)")
    void cifrar_mismosDatos_producenResultadosDistintos() {
        byte[] datos = "mismo_contenido".getBytes();

        String cifrado1 = service.cifrarBiometrico(datos);
        String cifrado2 = service.cifrarBiometrico(datos);

        // IV aleatorio garantiza que nunca sean iguales
        assertThat(cifrado1).isNotEqualTo(cifrado2);

        // Pero ambos descifran al mismo contenido original
        assertThat(service.descifrarBiometrico(cifrado1)).isEqualTo(datos);
        assertThat(service.descifrarBiometrico(cifrado2)).isEqualTo(datos);
    }

    @Test
    @DisplayName("Descifrar datos alterados lanza excepción (autenticación GCM)")
    void descifrar_datosAlterados_lanzaExcepcion() {
        byte[] datos = "datos_biometricos".getBytes();
        String cifrado = service.cifrarBiometrico(datos);

        // Alterar el último carácter del base64 (simula manipulación)
        String cifradoAlterado = cifrado.substring(0, cifrado.length() - 2) + "XX";

        assertThatThrownBy(() -> service.descifrarBiometrico(cifradoAlterado))
                .isInstanceOf(BiometricosCifradoService.BiometricoCifradoException.class);
    }
}
