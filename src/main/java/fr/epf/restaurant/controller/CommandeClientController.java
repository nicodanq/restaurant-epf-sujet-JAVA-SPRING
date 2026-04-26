package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.service.CommandeClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<CommandeClient> findAll() {
        return commandeClientDao.findAll();
    }

    @PostMapping
    public CommandeClient creer(@RequestBody CreerCommandeClientRequest req) {
        return commandeClientService.creer(req);
    }
}
