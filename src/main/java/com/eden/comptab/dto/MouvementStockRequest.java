package com.eden.comptab.dto;

import lombok.Data;

@Data
public class MouvementStockRequest {
    private Long produitId;
    private Long magasinId;
    private Long utilisateurId; // Celui qui fait l'action
    private Integer quantite; // Peut être positif (ajout) ou négatif (retrait)
    private String typeMvt; // LIVRAISON, INVENTAIRE, PERTE, CASSE, RETOUR_CLIENT
    private String commentaire;
}

