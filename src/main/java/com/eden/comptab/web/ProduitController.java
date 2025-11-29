package com.eden.comptab.web;

import com.eden.comptab.domain.Categorie;
import com.eden.comptab.domain.Produit;
import com.eden.comptab.domain.Tenant;
import com.eden.comptab.dto.ProduitRequest;
import com.eden.comptab.repository.CategorieRepository;
import com.eden.comptab.repository.ProduitRepository;
import com.eden.comptab.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitRepository produitRepository;
    private final TenantRepository tenantRepository;
    private final CategorieRepository categorieRepository;

    @PostMapping
    public ResponseEntity<Produit> ajouterProduit(@RequestBody ProduitRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        Categorie categorie = null;
        if (request.getCategorieId() != null) {
            categorie = categorieRepository.findById(request.getCategorieId()).orElse(null);
        }

        Produit produit = Produit.builder()
                .nomProduit(request.getNomProduit())
                .codeBarre(request.getCodeBarre())
                .prixVenteStandard(request.getPrixVente())
                .coutAchatStandard(request.getCoutAchat())
                .tenant(tenant)
                .categorie(categorie)
                .build();

        return ResponseEntity.ok(produitRepository.save(produit));
    }

    @GetMapping
    public ResponseEntity<List<Produit>> listerProduits(@RequestParam Long tenantId) {
        return ResponseEntity.ok(produitRepository.findByTenantId(tenantId));
    }
}

