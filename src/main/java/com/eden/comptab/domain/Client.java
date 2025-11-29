package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nomComplet;

    @Column(length = 20)
    private String tel;

    @Column(length = 100)
    private String email;

    @Column(length = 50)
    @Builder.Default
    private String statutFidelite = "Standard";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Magasin", nullable = false)
    private Magasin magasin;
}

