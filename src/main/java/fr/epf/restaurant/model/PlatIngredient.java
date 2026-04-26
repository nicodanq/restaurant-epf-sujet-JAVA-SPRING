package fr.epf.restaurant.model;

public class PlatIngredient {
    private Ingredient ingredient;
    private double quantiteRequise;

    public PlatIngredient(Ingredient ingredient, double quantiteRequise) {
        this.ingredient = ingredient;
        this.quantiteRequise = quantiteRequise;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantiteRequise() {
        return quantiteRequise;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setQuantiteRequise(double quantiteRequise) {
        this.quantiteRequise = quantiteRequise;
    }
}
