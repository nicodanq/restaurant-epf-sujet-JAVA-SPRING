package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.model.Ingredient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IntegredientDao integredientDao;

    public IngredientController(IntegredientDao integredientDao) {
        this.integredientDao = integredientDao;
    }

    @GetMapping
    public List<Ingredient> findAll() {
        return integredientDao.findAll();
    }
}
