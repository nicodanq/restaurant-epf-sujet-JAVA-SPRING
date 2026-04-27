package fr.epf.restaurant.service;

import fr.epf.restaurant.TestConfig;
import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dao.IntegredientDao;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.exception.StockInsuffisantException;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(TestConfig.class)
public class CommandeClientServiceTest {

    @Autowired
    private CommandeClientService commandeClientService;

    @Autowired
    private CommandeClientDao commandeClientDao;

    @Autowired
    private IntegredientDao integredientDao;

    // methode utilitaire pour creer une requete de commande rapidement
    private CreerCommandeClientRequest creerRequete(Long clientId, Long platId, int quantite) {
        CreerCommandeClientRequest req = new CreerCommandeClientRequest();
        req.setClientId(clientId);
        CreerCommandeClientRequest.LigneRequest ligne = new CreerCommandeClientRequest.LigneRequest();
        ligne.setPlatId(platId);
        ligne.setQuantite(quantite);
        req.setLignes(Arrays.asList(ligne));
        return req;
    }

    @Test
    public void testCreerUneCommande() {
        // client 1 et plat 1 existent dans les donnees de test (data.sql)
        CreerCommandeClientRequest req = creerRequete(1L, 1L, 2);
        CommandeClient commande = commandeClientService.creer(req);

        assertNotNull(commande);
        assertEquals("EN_ATTENTE", commande.getStatut());
    }

    @Test
    public void testCreerCommandeClientInexistant() {
        // le client 999 n'existe pas, on attend une exception
        CreerCommandeClientRequest req = creerRequete(999L, 1L, 1);

        assertThrows(ResourceNotFoundException.class, () ->
            commandeClientService.creer(req)
        );
    }

    @Test
    public void testPreparerAvecStockInsuffisant() {
        // quantite 9999 va depasser le stock disponible
        CreerCommandeClientRequest req = creerRequete(1L, 1L, 9999);
        CommandeClient commande = commandeClientService.creer(req);

        assertThrows(StockInsuffisantException.class, () ->
            commandeClientService.preparer(commande.getId())
        );
    }

    @Test
    public void testPreparerDeuxFoisMemeCommande() {
        // on ne peut pas preparer une commande deja en preparation
        CreerCommandeClientRequest req = creerRequete(1L, 1L, 1);
        CommandeClient commande = commandeClientService.creer(req);
        commandeClientService.preparer(commande.getId());

        assertThrows(StatutInvalideException.class, () ->
            commandeClientService.preparer(commande.getId())
        );
    }

    @Test
    public void testServirUneCommande() {
        CreerCommandeClientRequest req = creerRequete(1L, 1L, 1);
        CommandeClient commande = commandeClientService.creer(req);
        commandeClientService.preparer(commande.getId());

        CommandeClient result = commandeClientService.servir(commande.getId());

        assertEquals("SERVIE", result.getStatut());
    }

    @Test
    public void testIngredientsSousAlerte() {
        // on verifie que la methode retourne bien une liste (meme vide ca ne doit pas planter)
        List<Ingredient> alertes = integredientDao.findSousAlerte();

        assertNotNull(alertes);
    }
}
