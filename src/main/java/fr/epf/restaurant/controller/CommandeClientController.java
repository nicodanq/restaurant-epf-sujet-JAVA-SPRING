package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.model.CommandeClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/client")
public class CommandeClientController {

    private final CommandeClientDao commandeClientDao;

    public CommandeClientController(CommandeClientDao commandeClientDao) {
        this.commandeClientDao = commandeClientDao;
    }

    @GetMapping
    public List<CommandeClient> findAll() {
        return commandeClientDao.findAll();
    }
}
