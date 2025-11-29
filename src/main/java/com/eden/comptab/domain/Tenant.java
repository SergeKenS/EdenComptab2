package com.eden.comptab.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TENANT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nomSociete;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @Column(length = 50)
    private String planAbonnement;

    // Un Tenant peut avoir plusieurs magasins
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Magasin> magasins = new ArrayList<>();
}

