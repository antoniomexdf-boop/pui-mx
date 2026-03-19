/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que recibe el endpoint /desactivar-reporte.
 * Solo requiere el 'id' de la persona para dar de baja la búsqueda.
 * Sección 8.4 - Manual Técnico PUI.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesactivarReporteRequestDto {

    @NotBlank(message = "'id' es obligatorio")
    @Size(min = 36, max = 75, message = "'id' debe tener entre 36 y 75 caracteres")
    private String id;
}
