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
        Ingredient ing = integredientDao.findById(ingredientId);
        if (ing == null) {
            throw new ResourceNotFoundException("Ingrédient introuvable : " + ingredientId);
        }

        Map<String, Object> row = integredientDao.findFournisseurLeMoinsCher(ingredientId)
            .orElseThrow(() -> new ResourceNotFoundException("Aucun fournisseur pour cet ingrédient"));

        double stock = ing.getStockActuel();
        double seuil = ing.getSeuilAlerte();
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
            .map(ing -> new AlerteStockDto(
                ing.getId(),
                ing.getNom(),
                ing.getStockActuel(),
                ing.getSeuilAlerte(),
                2 * (ing.getSeuilAlerte() - ing.getStockActuel())
            ))
            .collect(Collectors.toList());
    }
}
