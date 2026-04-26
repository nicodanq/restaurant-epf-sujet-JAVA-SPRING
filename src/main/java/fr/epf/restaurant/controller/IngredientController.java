package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.RecommandationDto;
import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.service.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/{id}/recommandation")
    public RecommandationDto getRecommandation(@PathVariable Long id) {
        return stockService.getRecommandation(id);
    }

    @GetMapping("/{id}/prix")
    public List<Map<String, Object>> getPrixParFournisseur(@PathVariable Long id) {
        return integredientDao.findPrixParFournisseur(id);
    }
}
