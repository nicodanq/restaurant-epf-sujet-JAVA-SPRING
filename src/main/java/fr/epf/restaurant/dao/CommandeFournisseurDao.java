package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.Fournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommandeFournisseurDao {

    private static final String FIND_ALL_QUERY =
        "SELECT cf.id AS commande_id, cf.date_commande, cf.statut, "
        + "f.id AS fournisseur_id, f.nom, f.contact, f.email "
        + "FROM COMMANDE_FOURNISSEUR cf JOIN FOURNISSEUR f ON f.id = cf.fournisseur_id";

    private final JdbcTemplate jdbc;

    public CommandeFournisseurDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<CommandeFournisseur> commandeFournisseurMapper = (rs, rowNum) -> {
        Fournisseur fournisseur = new Fournisseur(
            rs.getLong("fournisseur_id"),
            rs.getString("nom"),
            rs.getString("contact"),
            rs.getString("email")
        );
        return new CommandeFournisseur(
            rs.getLong("commande_id"),
            fournisseur,
            rs.getTimestamp("date_commande").toLocalDateTime(),
            rs.getString("statut")
        );
    };

    public List<CommandeFournisseur> findAll() {
        return jdbc.query(FIND_ALL_QUERY, commandeFournisseurMapper);
    }
}
