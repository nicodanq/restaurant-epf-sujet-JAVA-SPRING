package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeFournisseur {
    private long id;
    private Fournisseur fournisseur;
    private LocalDateTime dateCommande;
    private String statut;
    private List<LigneCommandeFournisseur> lignes;

    public CommandeFournisseur(long id, Fournisseur fournisseur, LocalDateTime dateCommande, String statut) {
        this.id = id;
        this.fournisseur = fournisseur;
        this.dateCommande = dateCommande;
        this.statut = statut;
    }

    public long getId() {
        return id;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public String getStatut() {
        return statut;
    }

    public List<LigneCommandeFournisseur> getLignes() {
        return lignes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public void setDateCommande(LocalDateTime dateCommande) {
        this.dateCommande = dateCommande;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setLignes(List<LigneCommandeFournisseur> lignes) {
        this.lignes = lignes;
    }
}
