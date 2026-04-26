package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.PlatDao;
import fr.epf.restaurant.model.Plat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatDao platDao;

    public PlatController(PlatDao platDao) {
        this.platDao = platDao;
    }

    @GetMapping
    public List<Plat> findAll() {
        return platDao.findAll();
    }
}
