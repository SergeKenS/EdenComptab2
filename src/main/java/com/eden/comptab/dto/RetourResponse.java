package com.eden.comptab.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class RetourResponse {
    private Long id;
    private LocalDateTime dateRetour;
    private BigDecimal montantRembourse;
    private String raison;
    private Long venteOriginaleId;
}

