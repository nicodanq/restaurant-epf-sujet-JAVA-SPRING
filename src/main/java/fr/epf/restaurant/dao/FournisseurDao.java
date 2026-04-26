package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Fournisseur;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class FournisseurDao {

    private static final String FIND_ALL_QUERY =
        "SELECT id, nom, contact, email FROM FOURNISSEUR";
    private static final String FIND_BY_ID_QUERY =
        "SELECT id, nom, contact, email FROM FOURNISSEUR WHERE id = ?";
    private static final String CREATE_QUERY =
        "INSERT INTO FOURNISSEUR (nom, contact, email) VALUES (?, ?, ?)";
    private static final String FIND_CATALOGUE_QUERY =
        "SELECT i.id AS ingredientId, i.nom AS ingredientNom, i.unite AS ingredientUnite,"
        + " fi.prix_unitaire AS prixUnitaire"
        + " FROM FOURNISSEUR_INGREDIENT fi JOIN INGREDIENT i ON i.id = fi.ingredient_id"
        + " WHERE fi.fournisseur_id = ?";

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

    public Fournisseur findById(Long id) {
        return jdbc.queryForObject(FIND_BY_ID_QUERY, fournisseurMapper, id);
    }

    public void create(Fournisseur fournisseur) {
        jdbc.update(CREATE_QUERY, fournisseur.getNom(),
            fournisseur.getContact(), fournisseur.getEmail());
    }

    public List<Map<String, Object>> findCatalogue(Long id) {
        return jdbc.query(FIND_CATALOGUE_QUERY,
            (rs, rowNum) -> Map.of(
                "ingredientId", rs.getLong("ingredientId"),
                "ingredientNom", rs.getString("ingredientNom"),
                "ingredientUnite", rs.getString("ingredientUnite"),
                "prixUnitaire", rs.getDouble("prixUnitaire")
            ), id);
    }
}
