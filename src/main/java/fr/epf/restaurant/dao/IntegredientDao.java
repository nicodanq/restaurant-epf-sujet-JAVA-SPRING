package fr.epf.restaurant.dao;

import fr.epf.restaurant.model.Ingredient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IntegredientDao {

    private static final String FIND_ALL_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT";
    private static final String FIND_BY_ID_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT WHERE id = ?";
    private static final String FIND_SOUS_ALERTE_QUERY =
        "SELECT id, nom, unite, stock_actuel, seuil_alerte FROM INGREDIENT"
        + " WHERE stock_actuel < seuil_alerte";

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
}