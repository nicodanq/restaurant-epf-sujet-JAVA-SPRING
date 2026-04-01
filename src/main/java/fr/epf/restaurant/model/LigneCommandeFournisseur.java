package fr.epf.restaurant.model;

public class LigneCommandeFournisseur {
    private long id;
    private Ingredient ingredient;
    private double quantiteCommandee;
    private double prixUnitaire;

    public LigneCommandeFournisseur(long id, Ingredient ingredient, double quantiteCommandee, double prixUnitaire) {
        this.id = id;
        this.ingredient = ingredient;
        this.quantiteCommandee = quantiteCommandee;
        this.prixUnitaire = prixUnitaire;
    }

    public long getId() {
        return id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setQuantiteCommandee(double quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
    }

    public void setPrixUnitaire(double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}
