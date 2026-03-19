/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta de la PUI al hacer login. */
@Data
@NoArgsConstructor
public class PuiLoginResponse {
    private String token;
}
