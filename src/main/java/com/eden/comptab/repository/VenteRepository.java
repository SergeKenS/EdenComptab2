package com.eden.comptab.repository;

import com.eden.comptab.domain.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenteRepository extends JpaRepository<Vente, Long> {
    List<Vente> findByMagasinId(Long magasinId);
    List<Vente> findByClientId(Long clientId);
}

