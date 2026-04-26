package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class IntegredientDao {

    private static final String FIND_ALL_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT";
    private static final String FIND_BY_ID_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT WHERE id = ?";
    private static final String FIND_SOUS_ALERTE_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT"
        + " WHERE stock_actuel < seuil_alerte";
    private static final String FIND_FOURNISSEUR_MOINS_CHER_QUERY =
        "SELECT fi.fournisseur_id AS fournisseurId, f.nom AS fournisseurNom,"
        + " fi.prix_unitaire AS prixUnitaire"
        + " FROM FOURNISSEUR_INGREDIENT fi"
        + " JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id"
        + " WHERE fi.ingredient_id = ? ORDER BY fi.prix_unitaire ASC LIMIT 1";
    private static final String DECREMENTER_STOCK_QUERY =
        "UPDATE INGREDIENT SET stock_actuel = stock_actuel - ? WHERE id = ?";
    private static final String FIND_PRIX_PAR_FOURNISSEUR_QUERY =
        "SELECT fi.fournisseur_id AS fournisseurId, f.nom AS fournisseurNom,"
        + " fi.prix_unitaire AS prixUnitaire"
        + " FROM FOURNISSEUR_INGREDIENT fi"
        + " JOIN FOURNISSEUR f ON f.id = fi.fournisseur_id"
        + " WHERE fi.ingredient_id = ?";

    private final JdbcTemplate jdbc;

    public IntegredientDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Ingredient> ingredientMapper = (rs, rowNum) -> new Ingredient(
        rs.getLong("id"),
        rs.getString("nom"),
        rs.getString("unite"),
        rs.getDouble("stock_actuel"),
        rs.getDouble("seuil_alerte")
    );

    public List<Ingredient> findAll() {
        return jdbc.query(FIND_ALL_QUERY, ingredientMapper);
    }

    public Ingredient findById(Long id) {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, ingredientMapper, id);
    }

    public List<Ingredient> findSousAlerte() {
        return jdbc.query(FIND_SOUS_ALERTE_QUERY, ingredientMapper);
    }

    public Optional<Map<String, Object>> findFournisseurLeMoinsCher(Long id) {
        List<Map<String, Object>> results = jdbc.query(FIND_FOURNISSEUR_MOINS_CHER_QUERY,
            (rs, rowNum) -> Map.of(
                "fournisseurId", rs.getLong("fournisseurId"),
                "fournisseurNom", rs.getString("fournisseurNom"),
                "prixUnitaire", rs.getDouble("prixUnitaire")
            ), id);
        return results.stream().findFirst();
    }

    public void decrementerStock(Long id, double qte) {
        jdbc.update(DECREMENTER_STOCK_QUERY, qte, id);
    }

    public List<Map<String, Object>> findPrixParFournisseur(Long id) {
        return jdbc.query(FIND_PRIX_PAR_FOURNISSEUR_QUERY,
            (rs, rowNum) -> Map.of(
                "fournisseurId", rs.getLong("fournisseurId"),
                "fournisseurNom", rs.getString("fournisseurNom"),
                "prixUnitaire", rs.getDouble("prixUnitaire")
            ), id);
    }
}