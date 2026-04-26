package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Plat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PlatDao {

    private static final String FIND_ALL_QUERY =
        "SELECT id, nom, description, prix FROM PLAT";
    private static final String FIND_BY_ID_QUERY =
        "SELECT id, nom, description, prix FROM PLAT WHERE id = ?";

    private final JdbcTemplate jdbc;

    public PlatDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Plat> platMapper = (rs, rowNum) -> new Plat(
        rs.getLong("id"),
        rs.getString("nom"),
        rs.getString("description"),
        rs.getDouble("prix")
    );

    public List<Plat> findAll() {
        return jdbc.query(FIND_ALL_QUERY, platMapper);
    }

    public Plat findById(Long id) {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, platMapper, id);
    }
}
