package com.eden.comptab.web;

import com.eden.comptab.domain.Vente;
import com.eden.comptab.dto.VenteRequest;
import com.eden.comptab.dto.VenteResponse;
import com.eden.comptab.service.VenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    @PostMapping
    public ResponseEntity<VenteResponse> creerVente(@RequestBody VenteRequest request) {
        Vente vente = venteService.traiterVente(request);
        
        // Conversion Entité -> DTO
        VenteResponse response = VenteResponse.builder()
                .id(vente.getId())
                .dateVente(vente.getDateVente())
                .montantTotal(vente.getMontantTotal())
                .statutPaiement(vente.getStatutPaiement())
                .nomMagasin(vente.getMagasin().getNomMagasin())
                .nomVendeur(vente.getUtilisateur().getNomComplet())
                .nomClient(vente.getClient() != null ? vente.getClient().getNomComplet() : "Client de passage")
                .articles(vente.getLignes().stream()
                        .map(l -> VenteResponse.LigneVenteResponse.builder()
                                .nomProduit(l.getProduit().getNomProduit())
                                .quantite(l.getQuantite())
                                .prixUnitaire(l.getPrixUnitaire())
                                .sousTotal(l.getSousTotal())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        return ResponseEntity.ok(response);
    }
}
