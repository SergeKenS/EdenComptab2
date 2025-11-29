package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "LIGNE_RETOUR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneRetour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Retour", nullable = false)
    private Retour retour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Ligne_Vente", nullable = false)
    private LigneVente ligneVente;

    @Column(nullable = false)
    private Integer quantiteRetournee;
}

