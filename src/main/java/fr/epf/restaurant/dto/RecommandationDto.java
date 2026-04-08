package fr.epf.restaurant.dto;

public class RecommandationDto {
    private long fournisseurId;
    private String fournisseurNom;
    private double prixUnitaire;
    private double quantiteRecommandee;

    public RecommandationDto(long fournisseurId, String fournisseurNom,
            double prixUnitaire, double quantiteRecommandee) {
        this.fournisseurId = fournisseurId;
        this.fournisseurNom = fournisseurNom;
        this.prixUnitaire = prixUnitaire;
        this.quantiteRecommandee = quantiteRecommandee;
    }

    public long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(long fournisseurId) {
        this.fournisseurId = fournisseurId;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNom) {
        this.fournisseurNom = fournisseurNom;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public double getQuantiteRecommandee() {
        return quantiteRecommandee;
    }

    public void setQuantiteRecommandee(double quantiteRecommandee) {
        this.quantiteRecommandee = quantiteRecommandee;
    }
}
