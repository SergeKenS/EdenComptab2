package com.eden.comptab.dto;

import lombok.Data;

@Data
public class ClientRequest {
    private String nomComplet;
    private String tel;
    private String email;
    private Long magasinId;
}

