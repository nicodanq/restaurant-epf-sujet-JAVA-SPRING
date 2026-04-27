package fr.epf.restaurant.service;

import fr.epf.restaurant.TestConfig;
import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.model.CommandeFournisseur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(TestConfig.class)
public class CommandedeFournisseurServiceTest {

    @Autowired
    private CommandedeFournisseurService commandedeFournisseurService;

    @Autowired
    private IntegredientDao integredientDao;

    // methode utilitaire pour ne pas repeter la creation de requete partout
    private CreerCommandeFournisseurRequest creerRequete(Long fournisseurId, Long ingredientId,
            double quantite, double prix) {
        CreerCommandeFournisseurRequest req = new CreerCommandeFournisseurRequest();
        req.setFournisseurId(fournisseurId);
        CreerCommandeFournisseurRequest.LigneRequest ligne = new CreerCommandeFournisseurRequest.LigneRequest();
        ligne.setIngredientId(ingredientId);
        ligne.setQuantite(quantite);
        ligne.setPrixUnitaire(prix);
        req.setLignes(Arrays.asList(ligne));
        return req;
    }

    @Test
    public void testCreerCommandeFournisseur() {
        // fournisseur 1 existe dans data.sql
        CreerCommandeFournisseurRequest req = creerRequete(1L, 1L, 10, 3.5);
        CommandeFournisseur commande = commandedeFournisseurService.creer(req);

        assertNotNull(commande);
        assertEquals("EN_ATTENTE", commande.getStatut());
    }

    @Test
    public void testCreerAvecFournisseurInexistant() {
        CreerCommandeFournisseurRequest req = creerRequete(999L, 1L, 10, 3.5);

        assertThrows(ResourceNotFoundException.class, () ->
            commandedeFournisseurService.creer(req)
        );
    }

    @Test
    public void testEnvoyerUneCommande() {
        CreerCommandeFournisseurRequest req = creerRequete(1L, 1L, 10, 3.5);
        CommandeFournisseur commande = commandedeFournisseurService.creer(req);

        CommandeFournisseur result = commandedeFournisseurService.envoyer(commande.getId());

        assertEquals("ENVOYEE", result.getStatut());
    }

    @Test
    public void testRecevoirAugmenteLeStock() {
        // on note le stock avant la commande
        double stockAvant = integredientDao.findById(1L).getStockActuel();

        CreerCommandeFournisseurRequest req = creerRequete(1L, 1L, 10, 3.5);
        CommandeFournisseur commande = commandedeFournisseurService.creer(req);
        commandedeFournisseurService.envoyer(commande.getId());
        commandedeFournisseurService.recevoir(commande.getId());

        double stockApres = integredientDao.findById(1L).getStockActuel();

        // le stock doit avoir augmente de 10
        assertEquals(stockAvant + 10, stockApres, 0.01);
    }

    @Test
    public void testRecevoirSansEnvoyer() {
        // on ne peut pas recevoir une commande qui n'a pas ete envoyee
        CreerCommandeFournisseurRequest req = creerRequete(1L, 1L, 10, 3.5);
        CommandeFournisseur commande = commandedeFournisseurService.creer(req);

        assertThrows(StatutInvalideException.class, () ->
            commandedeFournisseurService.recevoir(commande.getId())
        );
    }
}
