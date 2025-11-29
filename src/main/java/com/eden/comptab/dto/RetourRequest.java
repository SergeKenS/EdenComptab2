package com.eden.comptab.dto;

import lombok.Data;

import java.util.List;

@Data
public class RetourRequest {
    private Long magasinId;
    private Long venteId; // La vente d'origine
    private String raison;
    
    private List<LigneRetourRequest> articlesRetournes;

    @Data
    public static class LigneRetourRequest {
        private Long ligneVenteId; // ID de la ligne dans la vente d'origine
        private Integer quantite;
    }
}

