package com.eden.comptab.repository;

import com.eden.comptab.domain.Retour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetourRepository extends JpaRepository<Retour, Long> {
    List<Retour> findByMagasinId(Long magasinId);
}

