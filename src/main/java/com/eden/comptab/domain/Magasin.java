package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "MAGASIN")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Magasin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomMagasin;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_Tenant", nullable = false)
    private Tenant tenant;
}

