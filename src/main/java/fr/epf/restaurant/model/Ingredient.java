package fr.epf.restaurant.model;

public class Ingredient {
    private long id;
    private String nom;
    private String unite;
    private double stockActuel;
    private double seuilAlerte;

    public Ingredient(long id, String nom, String unite, double stockActuel, double seuilAlerte) {
        this.id = id;
        this.nom = nom;
        this.unite = unite;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
    }

    public long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getUnite() {
        return unite;
    }

    public double getStockActuel() {
        return stockActuel;
    }

    public double getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public void setStockActuel(double stockActuel) {
        this.stockActuel = stockActuel;
    }

    public void setSeuilAlerte(double seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }
}
