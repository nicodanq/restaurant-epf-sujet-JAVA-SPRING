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
}
