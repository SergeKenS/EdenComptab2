package com.eden.comptab.web;

import com.eden.comptab.domain.*;
import com.eden.comptab.dto.StockAjustementRequest;
import com.eden.comptab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockRepository stockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;
    private final MagasinRepository magasinRepository;
    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    public ResponseEntity<List<Stock>> listerStocks(@RequestParam Long magasinId) {
        return ResponseEntity.ok(stockRepository.findByMagasinId(magasinId));
    }

    @PostMapping("/ajustement")
    @Transactional
    public ResponseEntity<Stock> ajusterStock(@RequestBody StockAjustementRequest request) {
        Magasin magasin = magasinRepository.findById(request.getMagasinId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable"));
        
        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 1. Mise à jour ou Création du Stock
        Stock stock = stockRepository.findByProduitIdAndMagasinId(produit.getId(), magasin.getId())
                .orElseGet(() -> Stock.builder()
                        .produit(produit)
                        .magasin(magasin)
                        .quantite(0)
                        .seuilAlerte(5)
                        .build());

        stock.setQuantite(stock.getQuantite() + request.getQuantiteAjoutee());
        Stock stockSauvegarde = stockRepository.save(stock);

        // 2. Historique Mouvement
        MouvementStock mvt = MouvementStock.builder()
                .dateMvt(LocalDateTime.now())
                .typeMvt("AJUSTEMENT_MANUEL") // Ou utiliser le motif de la request
                .quantite(request.getQuantiteAjoutee())
                .produit(produit)
                .magasin(magasin)
                .utilisateur(utilisateur)
                .commentaire(request.getMotif())
                .build();
        mouvementStockRepository.save(mvt);

        return ResponseEntity.ok(stockSauvegarde);
    }
}

