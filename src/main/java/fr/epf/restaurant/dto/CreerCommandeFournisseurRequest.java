package fr.epf.restaurant.dto;

import java.util.List;

public class CreerCommandeFournisseurRequest {
    private Long fournisseurId;
    private List<LigneRequest> lignes;

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public List<LigneRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneRequest> lignes) {
        this.lignes = lignes;
    }

    public static class LigneRequest {
        private Long ingredientId;
        private double quantite;
        private double prixUnitaire;

        public Long getIngredientId() {
            return ingredientId;
        }

        public void setIngredientId(Long ingredientId) {
            this.ingredientId = ingredientId;
        }

        public double getQuantite() {
            return quantite;
        }

        public void setQuantite(double quantite) {
            this.quantite = quantite;
        }

        public double getPrixUnitaire() {
            return prixUnitaire;
        }

        public void setPrixUnitaire(double prixUnitaire) {
            this.prixUnitaire = prixUnitaire;
        }
    }
}
