package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.model.Plat;
import fr.epf.restaurant.model.PlatIngredient;
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
    private static final String CREATE_QUERY =
        "INSERT INTO PLAT (nom, description, prix) VALUES (?, ?, ?)";
    private static final String FIND_INGREDIENTS_QUERY =
        "SELECT i.id, i.nom, i.unite, i.stock_actuel, i.seuil_alerte, pi.quantite_requise"
        + " FROM PLAT_INGREDIENT pi JOIN INGREDIENT i ON i.id = pi.ingredient_id"
        + " WHERE pi.plat_id = ?";

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

    public void create(Plat plat) {
        jdbc.update(CREATE_QUERY, plat.getNom(), plat.getDescription(), plat.getPrix());
    }

    public List<PlatIngredient> findIngredientsByPlatId(Long platId) {
        return jdbc.query(FIND_INGREDIENTS_QUERY,
            (rs, rowNum) -> new PlatIngredient(
                new Ingredient(rs.getLong("id"), rs.getString("nom"), rs.getString("unite"),
                    rs.getDouble("stock_actuel"), rs.getDouble("seuil_alerte")),
                rs.getDouble("quantite_requise")
            ), platId);
    }
}