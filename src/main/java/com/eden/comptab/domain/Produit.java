package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUIT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nomProduit;

    @Column(length = 50)
    private String codeBarre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixVenteStandard;

    @Column(precision = 10, scale = 2)
    private BigDecimal coutAchatStandard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Categorie")
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Tenant", nullable = false)
    private Tenant tenant;
}

