package fr.epf.restaurant.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import fr.epf.restaurant.dao.ClientDao;
import fr.epf.restaurant.model.Client;

@RestController  //c'est la combinaison de @Controller etde @ResponseBody
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientDao clientDao;


    public ClientController(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @GetMapping
    public List<Client> findAll() {
        return clientDao.findAll();
    }

}
