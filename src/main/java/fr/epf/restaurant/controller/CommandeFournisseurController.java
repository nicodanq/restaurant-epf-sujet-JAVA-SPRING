package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.CommandeFournisseurDao;
import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.service.CommandedeFournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/fournisseur")
public class CommandeFournisseurController {

    private final CommandeFournisseurDao commandeFournisseurDao;
    private final CommandedeFournisseurService commandedeFournisseurService;

    public CommandeFournisseurController(CommandeFournisseurDao commandeFournisseurDao,
            CommandedeFournisseurService commandedeFournisseurService) {
        this.commandeFournisseurDao = commandeFournisseurDao;
        this.commandedeFournisseurService = commandedeFournisseurService;
    }

    @GetMapping
    public List<CommandeFournisseur> findAll() {
        return commandeFournisseurDao.findAll();
    }

    @GetMapping("/{id}")
    public CommandeFournisseur findById(@PathVariable Long id) {
        return commandeFournisseurDao.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeFournisseur creer(@RequestBody CreerCommandeFournisseurRequest req) {
        return commandedeFournisseurService.creer(req);
    }

    @PutMapping("/{id}/envoyer")
    public CommandeFournisseur envoyer(@PathVariable Long id) {
        return commandedeFournisseurService.envoyer(id);
    }

    @PutMapping("/{id}/recevoir")
    public CommandeFournisseur recevoir(@PathVariable Long id) {
        return commandedeFournisseurService.recevoir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        commandedeFournisseurService.supprimer(id);
    }
}
