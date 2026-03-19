/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Punto de entrada del servicio de integración con la PUI.
 * @EnableAsync habilita las búsquedas en background (fases 1 y 2).
 */
@SpringBootApplication
@EnableAsync
public class PuiIntegracionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PuiIntegracionApplication.class, args);
    }
}
