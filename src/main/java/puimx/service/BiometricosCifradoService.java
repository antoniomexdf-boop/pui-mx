/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Servicio para cifrar información biométrica (fotos y huellas).
 *
 * Según la Sección 9 del Manual Técnico PUI:
 * 1. Convertir el archivo a base64.
 * 2. Cifrar el resultado con AES-256-GCM usando la clave proporcionada.
 * 3. Enviar la cadena cifrada en el campo correspondiente del JSON.
 *
 * AES-256-GCM proporciona:
 * - Confidencialidad (cifrado simétrico de 256 bits).
 * - Integridad y autenticación (authentication tag de 128 bits).
 * - IV aleatorio de 12 bytes por cada operación de cifrado.
 *
 * Formato del resultado (todo en base64 estándar, separado internamente):
 *   [12 bytes IV][16 bytes auth tag incluido en ciphertext por JCE][ciphertext]
 * Empaquetado como: Base64(IV + ciphertext_con_tag)
 */
@Slf4j
@Service
public class BiometricosCifradoService {

    private static final String ALGORITHM    = "AES/GCM/NoPadding";
    private static final int    GCM_IV_LEN   = 12;   // bytes – recomendado para GCM
    private static final int    GCM_TAG_BITS = 128;  // bits – máxima autenticación
    private static final int    KEY_BITS     = 256;  // AES-256

    private final SecretKey claveAes;

    /**
     * La clave se configura en application.yml como:
     *   pui.biometricos.clave=ClaveProporcionadaPorPUI
     *
     * Se deriva a 256 bits con PBKDF2 para garantizar la longitud correcta.
     */
    public BiometricosCifradoService(
            @Value("${pui.biometricos.clave}") String claveTexto) {
        this.claveAes = derivarClave(claveTexto);
        log.info("[BIOMETRICOS] Servicio de cifrado AES-256-GCM inicializado.");
    }

    // ================================================================
    // CIFRADO
    // ================================================================

    /**
     * Cifra un archivo biométrico (foto o huella) siguiendo el proceso del manual:
     * 1. Recibe los bytes crudos del archivo.
     * 2. Codifica en base64.
     * 3. Cifra con AES-256-GCM usando IV aleatorio.
     * 4. Devuelve base64(IV + ciphertext) listo para incluir en el JSON.
     *
     * @param bytesArchivo bytes del archivo de imagen/huella
     * @return String base64 que se envía en el campo fotos[] o huellas{}
     */
    public String cifrarBiometrico(byte[] bytesArchivo) {
        try {
            // Paso 1: base64 del archivo original
            byte[] base64Archivo = Base64.getEncoder().encode(bytesArchivo);

            // Paso 2: generar IV aleatorio (12 bytes)
            byte[] iv = new byte[GCM_IV_LEN];
            new SecureRandom().nextBytes(iv);

            // Paso 3: cifrar con AES-256-GCM
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, claveAes, params);
            byte[] ciphertext = cipher.doFinal(base64Archivo);

            // Paso 4: empaquetar IV + ciphertext y codificar en base64
            byte[] empaquetado = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getEncoder().encodeToString(empaquetado);

        } catch (Exception e) {
            log.error("[BIOMETRICOS] Error al cifrar biométrico: {}", e.getMessage());
            throw new BiometricoCifradoException("Error al cifrar el biométrico", e);
        }
    }

    // ================================================================
    // DESCIFRADO (para verificación interna / pruebas)
    // ================================================================

    /**
     * Descifra un biométrico previamente cifrado con cifrarBiometrico().
     * Uso interno o para pruebas; no se envía nada descifrado a la PUI.
     *
     * @param base64Cifrado resultado de cifrarBiometrico()
     * @return bytes del archivo original
     */
    public byte[] descifrarBiometrico(String base64Cifrado) {
        try {
            byte[] empaquetado = Base64.getDecoder().decode(base64Cifrado);

            // Separar IV (primeros 12 bytes) del ciphertext
            ByteBuffer buffer = ByteBuffer.wrap(empaquetado);
            byte[] iv = new byte[GCM_IV_LEN];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            // Descifrar
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec params = new GCMParameterSpec(GCM_TAG_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, claveAes, params);
            byte[] base64Archivo = cipher.doFinal(ciphertext);

            // Decodificar el base64 del archivo original
            return Base64.getDecoder().decode(base64Archivo);

        } catch (Exception e) {
            log.error("[BIOMETRICOS] Error al descifrar biométrico: {}", e.getMessage());
            throw new BiometricoCifradoException("Error al descifrar el biométrico", e);
        }
    }

    // ================================================================
    // HELPERS
    // ================================================================

    /**
     * Deriva una clave AES-256 a partir del texto de configuración
     * usando PBKDF2WithHmacSHA256. Garantiza exactamente 256 bits.
     */
    private SecretKey derivarClave(String claveTexto) {
        try {
            // Salt fijo derivado del nombre del sistema (no secreto, solo para derivación)
            byte[] salt = "PUI-Institucion-Salt-v1".getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(
                    claveTexto.toCharArray(), salt, 65536, KEY_BITS);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Error al inicializar clave AES-256", e);
        }
    }

    /** Excepción de cifrado que no expone detalles internos. */
    public static class BiometricoCifradoException extends RuntimeException {
        public BiometricoCifradoException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
}
