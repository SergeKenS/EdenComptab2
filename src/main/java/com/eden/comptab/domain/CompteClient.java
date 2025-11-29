package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "COMPTE_CLIENT", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ID_Client", "ID_Magasin"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompteClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Client", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    private Magasin magasin;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal soldeActuel = BigDecimal.ZERO; // Positif = Dû par le client; Négatif = Avance
}

