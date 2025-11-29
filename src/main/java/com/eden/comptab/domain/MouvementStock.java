package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MOUVEMENT_STOCK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateMvt;

    @Column(nullable = false, length = 50)
    private String typeMvt; // VENTE, LIVRAISON, PERTE, RETOUR, INVENTAIRE

    @Column(nullable = false)
    private Integer quantite; // Positif (Entrée) ou Négatif (Sortie)

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Produit", nullable = false)
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    private Magasin magasin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Utilisateur")
    private Utilisateur utilisateur;
}

