package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.CommandeFournisseurDao;
import fr.epf.restaurant.model.CommandeFournisseur;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/fournisseur")
public class CommandeFournisseurController {

    private final CommandeFournisseurDao commandeFournisseurDao;

    public CommandeFournisseurController(CommandeFournisseurDao commandeFournisseurDao) {
        this.commandeFournisseurDao = commandeFournisseurDao;
    }

    @GetMapping
    public List<CommandeFournisseur> findAll() {
        return commandeFournisseurDao.findAll();
    }
}
