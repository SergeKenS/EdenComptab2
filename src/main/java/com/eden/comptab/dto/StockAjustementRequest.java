package com.eden.comptab.dto;

import lombok.Data;

@Data
public class StockAjustementRequest {
    private Long magasinId;
    private Long produitId;
    private Integer quantiteAjoutee; // Peut être négatif pour une correction à la baisse
    private String motif; // "Livraison", "Inventaire", "Vol"
    private Long utilisateurId;
}

