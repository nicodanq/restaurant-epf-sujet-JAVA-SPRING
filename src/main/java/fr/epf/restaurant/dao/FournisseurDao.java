package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Fournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FournisseurDao {

    private static final String FIND_ALL_QUERY =
        "SELECT id, nom, contact, email FROM FOURNISSEUR";

    private final JdbcTemplate jdbc;

    public FournisseurDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Fournisseur> fournisseurMapper = (rs, rowNum) -> new Fournisseur(
        rs.getLong("id"),
        rs.getString("nom"),
        rs.getString("contact"),
        rs.getString("email")
    );

    public List<Fournisseur> findAll() {
        return jdbc.query(FIND_ALL_QUERY, fournisseurMapper);
    }
}
