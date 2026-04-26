package fr.epf.restaurant.dao;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import fr.epf.restaurant.model.Client;
import org.springframework.jdbc.core.RowMapper;

@Repository
public class ClientDao {
    private static final String FIND_ALL_QUERY = "SELECT * FROM client";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM client WHERE id = ?";
    private static final String CREATE_QUERY =
        "INSERT INTO client (nom, prenom, email, telephone) VALUES (?, ?, ?, ?)";

    private final JdbcTemplate jdbc;

    public ClientDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Client> responseMapper = (rs, rowNum) -> new Client(
        rs.getLong("id"),
        rs.getString("nom"),
        rs.getString("prenom"),
        rs.getString("email"),
        rs.getString("telephone")
    );

    public List<Client> findAll() {
        return jdbc.query(FIND_ALL_QUERY, responseMapper);
    }
}
