package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RETOUR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Retour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateRetour;

    @Column(precision = 10, scale = 2)
    private BigDecimal montantRembourse;

    @Column(columnDefinition = "TEXT")
    private String raisonRetour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Vente_Initiale", nullable = false)
    private Vente venteInitiale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    private Magasin magasin;

    @OneToMany(mappedBy = "retour", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<LigneRetour> lignesRetour = new java.util.ArrayList<>();
}

