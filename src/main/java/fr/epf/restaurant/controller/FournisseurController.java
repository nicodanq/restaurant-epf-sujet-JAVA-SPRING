package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.FournisseurDao;
import fr.epf.restaurant.model.Fournisseur;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
