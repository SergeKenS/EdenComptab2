package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "STOCK", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ID_Produit", "ID_Magasin"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Produit", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Magasin magasin;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantite = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer seuilAlerte = 5;
}

