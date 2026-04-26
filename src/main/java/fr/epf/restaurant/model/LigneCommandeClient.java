package fr.epf.restaurant.model;

public class LigneCommandeClient {
    private long id;
    private Plat plat;
    private int quantite;

    public LigneCommandeClient(long id, Plat plat, int quantite) {
        this.id = id;
        this.plat = plat;
        this.quantite = quantite;
    }

    public long getId() {
        return id;
    }

    public Plat getPlat() {
        return plat;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
