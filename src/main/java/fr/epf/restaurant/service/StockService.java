package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.RecommandationDto;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.model.Ingredient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final IntegredientDao integredientDao;

    public StockService(IntegredientDao integredientDao) {
        this.integredientDao = integredientDao;
    }

    public RecommandationDto getRecommandation(Long ingredientId) {
        Ingredient ingredient = integredientDao.findById(ingredientId);
        if (ingredient == null) {
            throw new ResourceNotFoundException("Ingrédient introuvable : " + ingredientId);
        }

        Map<String, Object> row = integredientDao.findFournisseurLeMoinsCher(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException("Aucun fournisseur pour cet ingrédient"));

        double stock = ingredient.getStockActuel();
        double seuil = ingredient.getSeuilAlerte();
        double qte = (seuil > stock) ? 2 * (seuil - stock) : seuil;

        return new RecommandationDto(
            (Long) row.get("fournisseurId"),
            (String) row.get("fournisseurNom"),
            (Double) row.get("prixUnitaire"),
            qte
        );
    }

    public List<AlerteStockDto> getAlertes() {
        return integredientDao.findSousAlerte().stream()
            .map(ingredient -> new AlerteStockDto(
                ingredient.getId(),
                ingredient.getNom(),
                ingredient.getStockActuel(),
                ingredient.getSeuilAlerte(),
                2 * (ingredient.getSeuilAlerte() - ingredient.getStockActuel())
            ))
            .collect(Collectors.toList());
    }
}
