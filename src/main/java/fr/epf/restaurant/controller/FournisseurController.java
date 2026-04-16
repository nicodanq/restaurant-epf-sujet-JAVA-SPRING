package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.FournisseurDao;
import fr.epf.restaurant.model.Fournisseur;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    private final FournisseurDao fournisseurDao;

    public FournisseurController(FournisseurDao fournisseurDao) {
        this.fournisseurDao = fournisseurDao;
    }

    @GetMapping
    public List<Fournisseur> findAll() {
        return fournisseurDao.findAll();
    }

    @PostMapping
    public void create(@RequestBody Fournisseur fournisseur) {
        fournisseurDao.create(fournisseur);
    }

    @GetMapping("/{id}/catalogue")
    public List<Map<String, Object>> getCatalogue(@PathVariable Long id) {
        return fournisseurDao.findCatalogue(id);
    }
}
