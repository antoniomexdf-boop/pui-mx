/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Request para POST /notificar-coincidencia de la PUI.
 * Sección 7.2 del Manual Técnico.
 *
 * Para Fase 1: omitir tipo_evento, fecha_evento,
 *              descripcion_lugar_evento y direccion_evento.
 * Para Fase 2 y 3: incluir todos los campos disponibles.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificarCoincidenciaRequest {

    /** CURP exactamente 18 chars alfanuméricos mayúsculas. Obligatorio. */
    private String curp;

    /** FUB + UUID4. Obligatorio. */
    private String id;

    /** RFC con homoclave de la institución. Obligatorio. */
    private String institucion_id;

    /**
     * Lugar de nacimiento según Anexo 5.
     * Valores: nombre del estado, FORANEO, DESCONOCIDO. Obligatorio.
     */
    private String lugar_nacimiento;

    /** "1", "2" o "3" según la fase de búsqueda. Obligatorio. */
    private String fase_busqueda;

    // --- Nombre ---
    private NombreCompletoDto nombre_completo;
    private String fecha_nacimiento;
    private String sexo_asignado;
    private String telefono;
    private String correo;

    // --- Domicilio de la persona ---
    private DomicilioDto domicilio;

    // --- Biométricos (cifrados AES-256-GCM, luego base64) ---
    private List<String> fotos;
    private String formato_fotos;
    private Object huellas;           // Objeto JSON con etiquetas del Anexo 4
    private String formato_huellas;

    // --- Datos del evento (solo fase 2 y 3) ---
    private String tipo_evento;
    private String fecha_evento;
    private String descripcion_lugar_evento;
    private DomicilioDto direccion_evento;
}
