/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.client;

/** Excepción lanzada cuando el cliente no puede comunicarse con la PUI. */
public class PuiClientException extends RuntimeException {

    public PuiClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public PuiClientException(String message) {
        super(message);
    }
}
