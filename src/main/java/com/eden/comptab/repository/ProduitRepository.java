package com.eden.comptab.repository;

import com.eden.comptab.domain.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByTenantId(Long tenantId);
    Optional<Produit> findByCodeBarreAndTenantId(String codeBarre, Long tenantId);
}

