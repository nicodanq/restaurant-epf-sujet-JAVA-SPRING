package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.Fournisseur;
import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.model.LigneCommandeFournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CommandeFournisseurDao {

    private static final String FIND_ALL_QUERY =
        "SELECT cf.id AS commande_id, cf.date_commande, cf.statut, "
        + "f.id AS fournisseur_id, f.nom, f.contact, f.email "
        + "FROM COMMANDE_FOURNISSEUR cf JOIN FOURNISSEUR f ON f.id = cf.fournisseur_id";
    private static final String FIND_BY_ID_QUERY =
        "SELECT cf.id AS commande_id, cf.date_commande, cf.statut, "
        + "f.id AS fournisseur_id, f.nom, f.contact, f.email "
        + "FROM COMMANDE_FOURNISSEUR cf JOIN FOURNISSEUR f ON f.id = cf.fournisseur_id"
        + " WHERE cf.id = ?";
    private static final String SAVE_QUERY =
        "INSERT INTO COMMANDE_FOURNISSEUR (fournisseur_id) VALUES (?)";
    private static final String ADD_LIGNE_QUERY =
        "INSERT INTO LIGNE_COMMANDE_FOURNISSEUR"
        + " (commande_fournisseur_id, ingredient_id, quantite_commandee, prix_unitaire)"
        + " VALUES (?, ?, ?, ?)";
    private static final String FIND_LIGNES_QUERY =
        "SELECT lcf.id, lcf.quantite_commandee, lcf.prix_unitaire,"
        + " i.id AS ingredient_id, i.nom, i.unite, i.stock_actuel, i.seuil_alerte"
        + " FROM LIGNE_COMMANDE_FOURNISSEUR lcf JOIN INGREDIENT i ON i.id = lcf.ingredient_id"
        + " WHERE lcf.commande_fournisseur_id = ?";
    private static final String UPDATE_STATUT_QUERY =
        "UPDATE COMMANDE_FOURNISSEUR SET statut = ? WHERE id = ?";
    private static final String DELETE_LIGNES_QUERY =
        "DELETE FROM LIGNE_COMMANDE_FOURNISSEUR WHERE commande_fournisseur_id = ?";
    private static final String DELETE_QUERY =
        "DELETE FROM COMMANDE_FOURNISSEUR WHERE id = ?";

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

    public CommandeFournisseur findById(Long id) {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, commandeFournisseurMapper, id);
    }

    public Long save(Long fournisseurId) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(SAVE_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, fournisseurId);
            return ps;
        }, kh);
        return ((Number) kh.getKeys().get("ID")).longValue();
    }

    public void addLigne(Long commandeId, Long ingredientId, double quantite, double prixUnitaire) {
        jdbc.update(ADD_LIGNE_QUERY, commandeId, ingredientId, quantite, prixUnitaire);
    }

    public List<LigneCommandeFournisseur> findLignesByCommandeId(Long commandeId) {
        return jdbc.query(FIND_LIGNES_QUERY,
            (rs, rowNum) -> new LigneCommandeFournisseur(
                rs.getLong("id"),
                new Ingredient(rs.getLong("ingredient_id"), rs.getString("nom"),
                    rs.getString("unite"), rs.getDouble("stock_actuel"),
                    rs.getDouble("seuil_alerte")),
                rs.getDouble("quantite_commandee"),
                rs.getDouble("prix_unitaire")
            ), commandeId);
    }

    public void updateStatut(Long id, String statut) {
        jdbc.update(UPDATE_STATUT_QUERY, statut, id);
    }

    public void delete(Long id) {
        jdbc.update(DELETE_LIGNES_QUERY, id);
        jdbc.update(DELETE_QUERY, id);
    }
}
