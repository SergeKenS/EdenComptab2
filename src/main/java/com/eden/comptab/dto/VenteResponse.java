package com.eden.comptab.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VenteResponse {
    private Long id;
    private LocalDateTime dateVente;
    private BigDecimal montantTotal;
    private String statutPaiement;
    private String nomVendeur;
    private String nomMagasin;
    private String nomClient;
    
    private List<LigneVenteResponse> articles;

    @Data
    @Builder
    public static class LigneVenteResponse {
        private String nomProduit;
        private Integer quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal sousTotal;
    }
}

