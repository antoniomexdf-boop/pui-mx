/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class RegistroInstitucional {
    private Long idRegistro;
    private String curp;
    private String nombreCompleto;
    private String tipoEvento;
    private LocalDate fechaEvento;
    private String descripcionLugar;
    private String direccionCompleta;
    private String telefono;
    private String correo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private String lugarNacimiento;
    private String sexoAsignado;
}
