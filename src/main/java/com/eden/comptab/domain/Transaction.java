package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTransaction;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 50)
    private String typeTransaction; // VENTE, REMBOURSEMENT, DEPENSE, APPORT

    @Column(length = 50)
    private String moyenPaiement;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Liens optionnels
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Vente")
    private Vente vente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Retour")
    private Retour retour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    private Magasin magasin;
}

