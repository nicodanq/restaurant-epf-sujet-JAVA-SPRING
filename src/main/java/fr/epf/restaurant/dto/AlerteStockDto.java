package fr.epf.restaurant.dto;

public class AlerteStockDto {
    private long ingredientId;
    private String ingredientNom;
    private double stockActuel;
    private double seuilAlerte;
    private double quantiteACommander;

    public AlerteStockDto(long ingredientId, String ingredientNom,
            double stockActuel, double seuilAlerte, double quantiteACommander) {
        this.ingredientId = ingredientId;
        this.ingredientNom = ingredientNom;
        this.stockActuel = stockActuel;
        this.seuilAlerte = seuilAlerte;
        this.quantiteACommander = quantiteACommander;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientNom() {
        return ingredientNom;
    }

    public void setIngredientNom(String ingredientNom) {
        this.ingredientNom = ingredientNom;
    }

    public double getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(double stockActuel) {
        this.stockActuel = stockActuel;
    }

    public double getSeuilAlerte() {
        return seuilAlerte;
    }

    public void setSeuilAlerte(double seuilAlerte) {
        this.seuilAlerte = seuilAlerte;
    }

    public double getQuantiteACommander() {
        return quantiteACommander;
    }

    public void setQuantiteACommander(double quantiteACommander) {
        this.quantiteACommander = quantiteACommander;
    }
}
