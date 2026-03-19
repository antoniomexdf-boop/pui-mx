/*
 * @copyright 2026 Jesus Antonio Jimenez Avina <antoniomexdf@gmail.com>
 * @license   https://opensource.org/licenses/MIT MIT License.
 * @repository https://github.com/antoniomexdf-boop/pui-mx
 */

package puimx.repository;

import puimx.model.RegistroInstitucional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class RegistroInstitucionalRepository {

    private final JdbcTemplate jdbcTemplate;

    public RegistroInstitucionalRepository(@Qualifier("datosJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<RegistroInstitucional> buscarMasRecientePorCurp(String curp) {
        String sql = """
                SELECT *
                FROM datos.registros_institucionales
                WHERE curp = ?
                ORDER BY COALESCE(fecha_modificacion, fecha_creacion) DESC
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, rowMapper(), curp).stream().findFirst();
    }

    public List<RegistroInstitucional> buscarHistorico(String curp, LocalDate desde, LocalDate hasta) {
        String sql = """
                SELECT *
                FROM datos.registros_institucionales
                WHERE curp = ?
                  AND fecha_evento BETWEEN ? AND ?
                ORDER BY fecha_evento ASC, id_registro ASC
                """;
        return jdbcTemplate.query(sql, rowMapper(), curp, Date.valueOf(desde), Date.valueOf(hasta));
    }

    public List<RegistroInstitucional> buscarNuevosOModificados(String curp, LocalDateTime desde) {
        String sql = """
                SELECT *
                FROM datos.registros_institucionales
                WHERE curp = ?
                  AND COALESCE(fecha_modificacion, fecha_creacion) > ?
                ORDER BY COALESCE(fecha_modificacion, fecha_creacion) ASC, id_registro ASC
                """;
        return jdbcTemplate.query(sql, rowMapper(), curp, Timestamp.valueOf(desde));
    }

    public List<RegistroInstitucional> buscarTodos() {
        return jdbcTemplate.query("""
                SELECT *
                FROM datos.registros_institucionales
                ORDER BY fecha_evento DESC, id_registro DESC
                """, rowMapper());
    }

    public List<RegistroInstitucional> buscarPorCurp(String curp) {
        return jdbcTemplate.query("""
                SELECT *
                FROM datos.registros_institucionales
                WHERE curp = ?
                ORDER BY fecha_evento DESC, id_registro DESC
                """, rowMapper(), curp);
    }

    private RowMapper<RegistroInstitucional> rowMapper() {
        return (rs, rowNum) -> RegistroInstitucional.builder()
                .idRegistro(rs.getLong("id_registro"))
                .curp(rs.getString("curp"))
                .nombreCompleto(rs.getString("nombre_completo"))
                .tipoEvento(rs.getString("tipo_evento"))
                .fechaEvento(rs.getObject("fecha_evento", LocalDate.class))
                .descripcionLugar(rs.getString("descripcion_lugar"))
                .direccionCompleta(rs.getString("direccion_completa"))
                .telefono(rs.getString("telefono"))
                .correo(rs.getString("correo"))
                .fechaCreacion(rs.getObject("fecha_creacion", LocalDateTime.class))
                .fechaModificacion(rs.getObject("fecha_modificacion", LocalDateTime.class))
                .lugarNacimiento(rs.getString("lugar_nacimiento"))
                .sexoAsignado(rs.getString("sexo_asignado"))
                .build();
    }
}
