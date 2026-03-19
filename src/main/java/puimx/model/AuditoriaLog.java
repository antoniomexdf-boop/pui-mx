/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "interacciones_pui", schema = "audit")
@Data
@NoArgsConstructor
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @Column(name = "timestamp_utc", nullable = false)
    private LocalDateTime timestampUtc;

    @Column(name = "tipo_operacion", nullable = false, length = 50)
    private String tipoOperacion;

    @Column(name = "reporte_id", length = 75)
    private String reporteId;

    @Column(name = "curp", length = 18)
    private String curp;

    @Column(name = "fase_busqueda", length = 1)
    private String faseBusqueda;

    @Column(name = "resultado_http")
    private Integer resultadoHttp;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "detalle", length = 4000)
    private String detalle;

    @PrePersist
    protected void onCreate() {
        this.timestampUtc = LocalDateTime.now();
    }
}
