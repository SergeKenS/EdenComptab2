package com.eden.comptab.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String token;
    private Long id;
    private String username;
    private String email;
    private String role;
    private Long tenantId;
    private Long magasinId;
}

