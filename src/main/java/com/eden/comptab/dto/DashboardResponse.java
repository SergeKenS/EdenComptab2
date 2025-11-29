package com.eden.comptab.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private BigDecimal caJournalier;
    private Integer nombreVentesJour;
    private BigDecimal caMensuel;
    private BigDecimal totalDettesClients;
    
    private String topProduitJour;
    private Integer quantiteTopProduit;
}

