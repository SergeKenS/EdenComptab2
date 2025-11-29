package com.eden.comptab.repository;

import com.eden.comptab.domain.CompteClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteClientRepository extends JpaRepository<CompteClient, Long> {
    Optional<CompteClient> findByClientIdAndMagasinId(Long clientId, Long magasinId);
}

