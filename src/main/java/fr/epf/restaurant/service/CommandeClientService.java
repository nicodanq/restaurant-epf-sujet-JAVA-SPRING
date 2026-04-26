package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.ClientDao;
import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.exception.ResourceNotFoundException;
import fr.epf.restaurant.model.CommandeClient;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CommandeClientService {

    private final CommandeClientDao commandeClientDao;
    private final ClientDao clientDao;

    public CommandeClientService(CommandeClientDao commandeClientDao, ClientDao clientDao) {
        this.commandeClientDao = commandeClientDao;
        this.clientDao = clientDao;
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
}
