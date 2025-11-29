package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "UTILISATEUR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nomComplet;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false, length = 50)
    private String role; // 'Admin', 'Manager', 'Vendeur'

    // Peut être NULL si Admin Tenant (accès global)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin")
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Tenant", nullable = false)
    private Tenant tenant;
}

