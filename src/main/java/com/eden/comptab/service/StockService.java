package com.eden.comptab.service;

import com.eden.comptab.domain.Magasin;
import com.eden.comptab.domain.MouvementStock;
import com.eden.comptab.domain.Produit;
import com.eden.comptab.domain.Stock;
import com.eden.comptab.domain.Utilisateur;
import com.eden.comptab.dto.MouvementStockRequest;
import com.eden.comptab.repository.MagasinRepository;
import com.eden.comptab.repository.MouvementStockRepository;
import com.eden.comptab.repository.ProduitRepository;
import com.eden.comptab.repository.StockRepository;
import com.eden.comptab.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;
    private final MagasinRepository magasinRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    public Stock ajusterStock(MouvementStockRequest request) {
        // 1. Récupération des entités
        Produit produit = produitRepository.findById(request.getProduitId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        
        Magasin magasin = magasinRepository.findById(request.getMagasinId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable"));

        Utilisateur utilisateur = null;
        if (request.getUtilisateurId() != null) {
            utilisateur = utilisateurRepository.findById(request.getUtilisateurId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        }

        // 2. Récupérer ou Créer l'entrée de Stock
        Stock stock = stockRepository.findByProduitIdAndMagasinId(produit.getId(), magasin.getId())
                .orElseGet(() -> {
                    Stock s = Stock.builder()
                            .produit(produit)
                            .magasin(magasin)
                            .quantite(0)
                            .seuilAlerte(5) // Valeur par défaut
                            .build();
                    return stockRepository.save(s);
                });

        // 3. Appliquer l'ajustement
        // Si quantite est positive => Ajout
        // Si quantite est négative => Retrait
        int nouvelleQuantite = stock.getQuantite() + request.getQuantite();
        if (nouvelleQuantite < 0) {
            throw new RuntimeException("Stock insuffisant pour cette opération (Stock actuel: " + stock.getQuantite() + ")");
        }
        stock.setQuantite(nouvelleQuantite);
        stockRepository.save(stock);

        // 4. Historiser le mouvement
        MouvementStock mouvement = MouvementStock.builder()
                .dateMvt(LocalDateTime.now())
                .typeMvt(request.getTypeMvt())
                .quantite(request.getQuantite())
                .produit(produit)
                .magasin(magasin)
                .utilisateur(utilisateur)
                .commentaire(request.getCommentaire())
                .build();
        
        mouvementStockRepository.save(mouvement);

        return stock;
    }
}

