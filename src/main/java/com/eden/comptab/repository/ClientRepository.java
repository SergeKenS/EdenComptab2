package com.eden.comptab.repository;

import com.eden.comptab.domain.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByMagasinId(Long magasinId);
    List<Client> findByMagasinIdAndNomCompletContainingIgnoreCase(Long magasinId, String nom);
}

