/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes_activos", schema = "pui")
@Data
@NoArgsConstructor
public class ReporteActivo {

    @Id
    @Column(name = "id", length = 75, nullable = false)
    private String id;

    @Column(name = "curp", length = 18, nullable = false)
    private String curp;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "primer_apellido", length = 50)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_desaparicion")
    private LocalDate fechaDesaparicion;

    @Column(name = "lugar_nacimiento", length = 20, nullable = false)
    private String lugarNacimiento;

    @Column(name = "sexo_asignado", length = 1)
    private String sexoAsignado;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "correo", length = 50)
    private String correo;

    @Column(name = "direccion", length = 500)
    private String direccion;

    @Column(name = "calle", length = 50)
    private String calle;

    @Column(name = "numero", length = 20)
    private String numero;

    @Column(name = "colonia", length = 50)
    private String colonia;

    @Column(name = "codigo_postal", length = 5)
    private String codigoPostal;

    @Column(name = "municipio_o_alcaldia", length = 100)
    private String municipioOAlcaldia;

    @Column(name = "entidad_federativa", length = 40)
    private String entidadFederativa;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fase1_completada", nullable = false)
    private boolean fase1Completada;

    @Column(name = "fase2_completada", nullable = false)
    private boolean fase2Completada;

    @Column(name = "ultima_verificacion")
    private LocalDateTime ultimaVerificacion;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;

    @PrePersist
    protected void onCreate() {
        this.fechaAlta = LocalDateTime.now();
        if (this.ultimaVerificacion == null) {
            this.ultimaVerificacion = this.fechaAlta.minusMinutes(1);
        }
    }
}
