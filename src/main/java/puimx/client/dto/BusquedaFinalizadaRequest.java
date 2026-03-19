/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request para POST /busqueda-finalizada de la PUI. Sección 7.3. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusquedaFinalizadaRequest {
    private String id;
    private String institucion_id;
}
