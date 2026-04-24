package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.ClientDao;
import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dao.PlatDao;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.dto.PreparationResultDto;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.exception.StockInsuffisantException;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.LigneCommandeClient;
import fr.epf.restaurant.model.PlatIngredient;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeClientService {

    private final CommandeClientDao commandeClientDao;
    private final ClientDao clientDao;
    private final PlatDao platDao;
    private final IntegredientDao integredientDao;
    private final StockService stockService;

    public CommandeClientService(CommandeClientDao commandeClientDao, ClientDao clientDao,
            PlatDao platDao, IntegredientDao integredientDao, StockService stockService) {
        this.commandeClientDao = commandeClientDao;
        this.clientDao = clientDao;
        this.platDao = platDao;
        this.integredientDao = integredientDao;
        this.stockService = stockService;
    }

    public CommandeClient creer(CreerCommandeClientRequest req) {
        try {
            clientDao.findById(req.getClientId());
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Client introuvable : " + req.getClientId());
        }

        Long commandeId = commandeClientDao.save(req.getClientId());

        for (CreerCommandeClientRequest.LigneRequest ligne : req.getLignes()) {
            commandeClientDao.addLigne(commandeId, ligne.getPlatId(), ligne.getQuantite());
        }

        return commandeClientDao.findById(commandeId);
    }

    public PreparationResultDto preparer(Long id) {
        CommandeClient commande = commandeClientDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_ATTENTE pour être préparée");
        }

        List<LigneCommandeClient> lignes = commandeClientDao.findLignesByCommandeId(id);

        // Vérification du stock pour chaque ligne avant de décrémenter
        for (LigneCommandeClient ligne : lignes) {
            List<PlatIngredient> ingredients =
                platDao.findIngredientsByPlatId(ligne.getPlat().getId());
            for (PlatIngredient pi : ingredients) {
                double qteNecessaire = pi.getQuantiteRequise() * ligne.getQuantite();
                if (pi.getIngredient().getStockActuel() < qteNecessaire) {
                    throw new StockInsuffisantException(
                        "Stock insuffisant pour : " + pi.getIngredient().getNom());
                }
            }
        }

        // Décrémentation du stock
        for (LigneCommandeClient ligne : lignes) {
            List<PlatIngredient> ingredients =
                platDao.findIngredientsByPlatId(ligne.getPlat().getId());
            for (PlatIngredient pi : ingredients) {
                double qteNecessaire = pi.getQuantiteRequise() * ligne.getQuantite();
                integredientDao.decrementerStock(pi.getIngredient().getId(), qteNecessaire);
            }
        }

        commandeClientDao.updateStatut(id, "EN_PREPARATION");
        CommandeClient commandeMaj = commandeClientDao.findById(id);
        return new PreparationResultDto(commandeMaj, stockService.getAlertes());
    }

    public CommandeClient servir(Long id) {
        CommandeClient commande = commandeClientDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        if (!"EN_PREPARATION".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_PREPARATION pour être servie");
        }
        commandeClientDao.updateStatut(id, "SERVIE");
        return commandeClientDao.findById(id);
    }

    public void supprimer(Long id) {
        CommandeClient commande = commandeClientDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        commandeClientDao.delete(id);
    }
}
