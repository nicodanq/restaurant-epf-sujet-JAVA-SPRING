package fr.epf.restaurant.model;

import java.util.List;

public class Plat {
    private long id;
    private String nom;
    private String description;
    private double prix;
    private List<PlatIngredient> ingredients;

    public Plat() {}

    public Plat(long id, String nom, String description, double prix) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public double getPrix() {
        return prix;
    }

    public List<PlatIngredient> getIngredients() {
        return ingredients;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setIngredients(List<PlatIngredient> ingredients) {
        this.ingredients = ingredients;
    }
}
