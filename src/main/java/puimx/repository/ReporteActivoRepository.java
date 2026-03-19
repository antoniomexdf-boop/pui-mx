/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.repository;

import puimx.model.ReporteActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la persistencia local de reportes activos.
 * Requerido por el manual (Sección 6): "Persistencia de las búsquedas
 * y coincidencias en bases de datos locales."
 */
@Repository
public interface ReporteActivoRepository extends JpaRepository<ReporteActivo, String> {

    /** Devuelve todos los reportes que siguen con búsqueda continua activa. */
    List<ReporteActivo> findByActivoTrue();

    /** Verifica si existe un reporte activo con ese id. */
    boolean existsByIdAndActivoTrue(String id);

    Optional<ReporteActivo> findTopByCurpOrderByFechaAltaDesc(String curp);
}
