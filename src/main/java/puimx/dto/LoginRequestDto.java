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
import lombok.AllArgsConstructor;

/**
 * DTO para la solicitud de login que realiza la PUI.
 * Sección 8.1 - Manual Técnico PUI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "'usuario' es obligatorio")
    @Size(min = 3, max = 3, message = "'usuario' debe tener exactamente 3 caracteres")
    private String usuario;

    @NotBlank(message = "'clave' es obligatoria")
    @Size(min = 16, max = 20, message = "'clave' debe tener entre 16 y 20 caracteres")
    private String clave;
}
