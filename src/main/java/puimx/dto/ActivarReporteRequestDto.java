/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que recibe el endpoint /activar-reporte y /activar-reporte-prueba.
 * La PUI envía los datos de la persona desaparecida o no localizada.
 *
 * Sección 8.2 y 8.3 - Manual Técnico PUI.
 * NOTA: Solo 'id', 'curp' y 'lugar_nacimiento' son obligatorios.
 *       El resto puede omitirse si no está disponible en el padrón (nota 6 del manual).
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivarReporteRequestDto {

    /**
     * Identificador único: FUB concatenado con UUID4 separados por guión.
     * Formato: &lt;FUB&gt;-&lt;UUID4&gt;
     * Ejemplo: e7b5a4c2-9f4e-4a99-91a2-6d4a8a1eaf3d-550e8400-e29b-41d4-a716-446655440000
     */
    @NotBlank(message = "'id' es obligatorio")
    @Size(min = 36, max = 75, message = "'id' debe tener entre 36 y 75 caracteres")
    private String id;

    /** CURP: exactamente 18 caracteres alfanuméricos en mayúsculas. */
    @NotBlank(message = "'curp' es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]{18}$",
             message = "'curp' debe tener 18 caracteres alfanuméricos en mayúsculas")
    private String curp;

    @Size(max = 50, message = "'nombre' no debe superar 50 caracteres")
    private String nombre;

    @Size(max = 50, message = "'primer_apellido' no debe superar 50 caracteres")
    private String primer_apellido;

    @Size(max = 50, message = "'segundo_apellido' no debe superar 50 caracteres")
    private String segundo_apellido;

    /** Formato ISO 8601: YYYY-MM-DD */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
             message = "'fecha_nacimiento' debe estar en formato YYYY-MM-DD")
    private String fecha_nacimiento;

    /** Formato ISO 8601: YYYY-MM-DD */
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
             message = "'fecha_desaparicion' debe estar en formato YYYY-MM-DD")
    private String fecha_desaparicion;

    /**
     * Estado derivado de las posiciones 12-13 de la CURP (Anexo 5).
     * Ejemplos: CDMX, JALISCO, FORANEO, DESCONOCIDO
     */
    @NotNull(message = "'lugar_nacimiento' es obligatorio")
    @Size(max = 20, message = "'lugar_nacimiento' no debe superar 20 caracteres")
    private String lugar_nacimiento;

    /** H = Hombre | M = Mujer | X = Otro */
    @Pattern(regexp = "^[MHX]$",
             message = "'sexo_asignado' debe ser H, M o X")
    private String sexo_asignado;

    @Size(max = 15, message = "'telefono' no debe superar 15 caracteres")
    private String telefono;

    @Size(max = 50, message = "'correo' no debe superar 50 caracteres")
    private String correo;

    // --- Campos de domicilio ---

    @Size(max = 500)
    private String direccion;

    @Size(max = 50)
    private String calle;

    @Size(max = 20)
    private String numero;

    @Size(max = 50)
    private String colonia;

    @Size(max = 5)
    private String codigo_postal;

    @Size(max = 100)
    private String municipio_o_alcaldia;

    @Size(max = 40)
    private String entidad_federativa;
}
