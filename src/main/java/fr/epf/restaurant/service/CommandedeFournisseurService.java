package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.CommandeFournisseurDao;
import fr.epf.restaurant.dao.FournisseurDao;
import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.LigneCommandeFournisseur;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandedeFournisseurService {

    private final CommandeFournisseurDao commandeFournisseurDao;
    private final FournisseurDao fournisseurDao;
    private final IntegredientDao integredientDao;

    public CommandedeFournisseurService(CommandeFournisseurDao commandeFournisseurDao,
            FournisseurDao fournisseurDao, IntegredientDao integredientDao) {
        this.commandeFournisseurDao = commandeFournisseurDao;
        this.fournisseurDao = fournisseurDao;
        this.integredientDao = integredientDao;
    }

    public CommandeFournisseur creer(CreerCommandeFournisseurRequest req) {
        try {
            fournisseurDao.findById(req.getFournisseurId());
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Fournisseur introuvable : " + req.getFournisseurId());
        }

        Long commandeId = commandeFournisseurDao.save(req.getFournisseurId());

        for (CreerCommandeFournisseurRequest.LigneRequest ligne : req.getLignes()) {
            commandeFournisseurDao.addLigne(commandeId, ligne.getIngredientId(),
                ligne.getQuantite(), ligne.getPrixUnitaire());
        }

        return commandeFournisseurDao.findById(commandeId);
    }

    public CommandeFournisseur envoyer(Long id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        if (!"EN_ATTENTE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être EN_ATTENTE pour être envoyée");
        }
        commandeFournisseurDao.updateStatut(id, "ENVOYEE");
        return commandeFournisseurDao.findById(id);
    }

    public CommandeFournisseur recevoir(Long id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        if (!"ENVOYEE".equals(commande.getStatut())) {
            throw new StatutInvalideException("La commande doit être ENVOYEE pour être reçue");
        }

        List<LigneCommandeFournisseur> lignes =
            commandeFournisseurDao.findLignesByCommandeId(id);
        for (LigneCommandeFournisseur ligne : lignes) {
            integredientDao.incrementerStock(
                ligne.getIngredient().getId(), ligne.getQuantiteCommandee());
        }

        commandeFournisseurDao.updateStatut(id, "RECUE");
        return commandeFournisseurDao.findById(id);
    }

    public void supprimer(Long id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id);
        if (commande == null) {
            throw new ResourceNotFoundException("Commande introuvable : " + id);
        }
        commandeFournisseurDao.delete(id);
    }
}
