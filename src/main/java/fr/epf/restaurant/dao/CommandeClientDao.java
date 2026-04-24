package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Client;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.LigneCommandeClient;
import fr.epf.restaurant.model.Plat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CommandeClientDao {

    private static final String SAVE_QUERY =
        "INSERT INTO COMMANDE_CLIENT (client_id) VALUES (?)";
    private static final String ADD_LIGNE_QUERY =
        "INSERT INTO LIGNE_COMMANDE_CLIENT (commande_client_id, plat_id, quantite)"
        + " VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_QUERY =
        "SELECT cc.id AS commande_id, cc.date_commande, cc.statut, "
        + "c.id AS client_id, c.nom, c.prenom, c.email, c.telephone "
        + "FROM COMMANDE_CLIENT cc JOIN CLIENT c ON c.id = cc.client_id"
        + " WHERE cc.id = ?";
    private static final String UPDATE_STATUT_QUERY =
        "UPDATE COMMANDE_CLIENT SET statut = ? WHERE id = ?";
    private static final String DELETE_LIGNES_QUERY =
        "DELETE FROM LIGNE_COMMANDE_CLIENT WHERE commande_client_id = ?";
    private static final String DELETE_QUERY =
        "DELETE FROM COMMANDE_CLIENT WHERE id = ?";
    private static final String FIND_LIGNES_QUERY =
        "SELECT lcc.id, lcc.quantite, p.id AS plat_id, p.nom, p.description, p.prix"
        + " FROM LIGNE_COMMANDE_CLIENT lcc JOIN PLAT p ON p.id = lcc.plat_id"
        + " WHERE lcc.commande_client_id = ?";
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

    public CommandeClient findById(Long id) {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, commandeClientMapper, id);
    }

    public Long save(Long clientId) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, clientId);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("ID")).longValue();
    }

    public void addLigne(Long commandeId, Long platId, int quantite) {
        jdbc.update(ADD_LIGNE_QUERY, commandeId, platId, quantite);
    }

    public void updateStatut(Long id, String statut) {
        jdbc.update(UPDATE_STATUT_QUERY, statut, id);
    }

    public void delete(Long id) {
        jdbc.update(DELETE_LIGNES_QUERY, id);
        jdbc.update(DELETE_QUERY, id);
    }

    public List<LigneCommandeClient> findLignesByCommandeId(Long commandeId) {
        return jdbc.query(FIND_LIGNES_QUERY,
            (rs, rowNum) -> new LigneCommandeClient(
                rs.getLong("id"),
                new Plat(rs.getLong("plat_id"), rs.getString("nom"),
                    rs.getString("description"), rs.getDouble("prix")),
                rs.getInt("quantite")
            ), commandeId);
    }

}