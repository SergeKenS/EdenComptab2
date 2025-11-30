package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomCategorie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Tenant", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Tenant tenant;
}

