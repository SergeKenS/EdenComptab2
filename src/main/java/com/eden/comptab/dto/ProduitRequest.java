package com.eden.comptab.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProduitRequest {
    private String nomProduit;
    private String codeBarre;
    private BigDecimal prixVente;
    private BigDecimal coutAchat;
    private Long categorieId; // Optionnel
    private Long tenantId;
}

