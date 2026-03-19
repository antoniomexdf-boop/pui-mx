/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Credenciales que envía la institución a la PUI para obtener JWT. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PuiLoginRequest {
    private String institucion_id;
    private String clave;
}
