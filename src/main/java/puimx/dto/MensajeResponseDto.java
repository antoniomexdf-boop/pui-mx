/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta estándar de éxito con mensaje descriptivo. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeResponseDto {
    private String message;
}
