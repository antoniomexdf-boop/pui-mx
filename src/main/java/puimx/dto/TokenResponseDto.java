/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta con token JWT tras autenticación exitosa. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponseDto {
    private String token;
}
