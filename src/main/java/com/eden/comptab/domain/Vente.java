package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateVente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Column(nullable = false, length = 50)
    private String statutPaiement; // 'Complet', 'Partiel/Crédit', 'Annulé'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Client")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Utilisateur", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Magasin magasin;

    @OneToMany(mappedBy = "vente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LigneVente> lignes = new ArrayList<>();
}

