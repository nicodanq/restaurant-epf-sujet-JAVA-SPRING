package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeClient {
    private long id;
    private Client client;
    private LocalDateTime dateCommande;
    private String statut;
    private List<LigneCommandeClient> lignes;

    public CommandeClient(long id, Client client, LocalDateTime dateCommande, String statut) {
        this.id = id;
        this.client = client;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    public long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public List<LigneCommandeClient> getLignes() {
        return lignes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setLignes(List<LigneCommandeClient> lignes) {
        this.lignes = lignes;
    }
}
