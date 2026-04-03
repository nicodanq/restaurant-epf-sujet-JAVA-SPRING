package fr.epf.restaurant.dto;

import java.util.List;

public class CreerCommandeClientRequest {
    private Long clientId;
    private List<LigneRequest> lignes;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<LigneRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneRequest> lignes) {
        this.lignes = lignes;
    }

    public static class LigneRequest {
        private Long platId;
        private int quantite;

        public Long getPlatId() {
            return platId;
        }

        public void setPlatId(Long platId) {
            this.platId = platId;
        }

        public int getQuantite() {
            return quantite;
        }

        public void setQuantite(int quantite) {
            this.quantite = quantite;
        }
    }
}
