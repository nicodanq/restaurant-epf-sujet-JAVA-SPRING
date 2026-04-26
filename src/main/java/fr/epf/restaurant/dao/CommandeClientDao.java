package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Client;
import fr.epf.restaurant.model.CommandeClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommandeClientDao {

    private static final String FIND_ALL_QUERY =
        "SELECT cc.id AS commande_id, cc.date_commande, cc.statut, "
        + "c.id AS client_id, c.nom, c.prenom, c.email, c.telephone "
        + "FROM COMMANDE_CLIENT cc JOIN CLIENT c ON c.id = cc.client_id";

    private final JdbcTemplate jdbc;

    public CommandeClientDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<CommandeClient> commandeClientMapper = (rs, rowNum) -> {
        Client client = new Client(
            rs.getLong("client_id"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("email"),
            rs.getString("telephone")
        );
        return new CommandeClient(
            rs.getLong("commande_id"),
            client,
            rs.getTimestamp("date_commande").toLocalDateTime(),
            rs.getString("statut")
        );
    };

    public List<CommandeClient> findAll() {
        return jdbc.query(FIND_ALL_QUERY, commandeClientMapper);
    }
}
