package com.eden.comptab.web;

import com.eden.comptab.domain.Categorie;
import com.eden.comptab.domain.Tenant;
import com.eden.comptab.dto.CategorieRequest;
import com.eden.comptab.repository.CategorieRepository;
import com.eden.comptab.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieRepository categorieRepository;
    private final TenantRepository tenantRepository;

    @GetMapping
    public ResponseEntity<List<Categorie>> listerCategories(@RequestParam Long tenantId) {
        return ResponseEntity.ok(categorieRepository.findAll()); 
    }

    @PostMapping
    public ResponseEntity<Categorie> creerCategorie(@RequestBody CategorieRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant introuvable"));

        Categorie categorie = Categorie.builder()
                .nomCategorie(request.getNomCategorie())
                .tenant(tenant)
                .build();

        return ResponseEntity.ok(categorieRepository.save(categorie));
    }
}

