package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IntegredientDao integredientDao;
    private final StockService stockService;

    public IngredientController(IntegredientDao integredientDao, StockService stockService) {
        this.integredientDao = integredientDao;
        this.stockService = stockService;
    }

    @GetMapping
    public List<Ingredient> findAll() {
        return integredientDao.findAll();
    }

    @GetMapping("/alertes")
    public List<AlerteStockDto> getAlertes() {
        return stockService.getAlertes();
    }
}
