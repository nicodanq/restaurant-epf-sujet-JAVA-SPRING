package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final IntegredientDao integredientDao;

    public StockService(IntegredientDao integredientDao) {
        this.integredientDao = integredientDao;
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
