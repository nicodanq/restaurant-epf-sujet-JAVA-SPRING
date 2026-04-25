package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.dto.PreparationResultDto;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.service.CommandeClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/client")
public class CommandeClientController {

    private final CommandeClientDao commandeClientDao;
    private final CommandeClientService commandeClientService;

    public CommandeClientController(CommandeClientDao commandeClientDao,
            CommandeClientService commandeClientService) {
        this.commandeClientDao = commandeClientDao;
        this.commandeClientService = commandeClientService;
    }

    @GetMapping
    public List<CommandeClient> findAll(
            @RequestParam(required = false) String statut) {
        if (statut != null) {
            return commandeClientDao.findByStatut(statut);
        }
        return commandeClientDao.findAll();
    }

    @GetMapping("/{id}")
    public CommandeClient findById(@PathVariable Long id) {
        return commandeClientDao.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeClient creer(@RequestBody CreerCommandeClientRequest req) {
        return commandeClientService.creer(req);
    }

    @PutMapping("/{id}/preparer")
    public PreparationResultDto preparer(@PathVariable Long id) {
        return commandeClientService.preparer(id);
    }

    @PutMapping("/{id}/servir")
    public CommandeClient servir(@PathVariable Long id) {
        return commandeClientService.servir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        commandeClientService.supprimer(id);
    }
}
