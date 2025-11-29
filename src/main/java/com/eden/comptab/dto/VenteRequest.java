package com.eden.comptab.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VenteRequest {
    private Long magasinId;
    private Long utilisateurId; // Le vendeur
    private Long clientId; // Optionnel
    
    private List<LigneVenteRequest> articles;
    
    private BigDecimal montantPaye; // Ce que le client donne (pour gérer le rendu monnaie ou partiel)
    private String moyenPaiement; // ESPECES, CB, MOBILE_MONEY
    private boolean aCredit; // Si true, le reste est mis sur le compte client

    @Data
    public static class LigneVenteRequest {
        private Long produitId;
        private Integer quantite;
    }
}

