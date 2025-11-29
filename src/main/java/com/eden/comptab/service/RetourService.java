package com.eden.comptab.service;

import com.eden.comptab.domain.*;
import com.eden.comptab.dto.RetourRequest;
import com.eden.comptab.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetourService {

    private final VenteRepository venteRepository;
    private final RetourRepository retourRepository;
    private final StockRepository stockRepository;
    private final TransactionRepository transactionRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final MagasinRepository magasinRepository;
    private final LigneVenteRepository ligneVenteRepository; // Nécessaire pour trouver les lignes

    @Transactional
    public Retour traiterRetour(RetourRequest request) {
        // 1. Validations
        Magasin magasin = magasinRepository.findById(request.getMagasinId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable"));

        Vente venteInitiale = venteRepository.findById(request.getVenteId())
                .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        if (!venteInitiale.getMagasin().getId().equals(magasin.getId())) {
            throw new RuntimeException("Cette vente n'appartient pas à ce magasin");
        }

        Retour retour = Retour.builder()
                .dateRetour(LocalDateTime.now())
                .raisonRetour(request.getRaison())
                .venteInitiale(venteInitiale)
                .magasin(magasin)
                .build();

        BigDecimal montantTotalRembourse = BigDecimal.ZERO;
        List<LigneRetour> lignesRetour = new ArrayList<>();

        // 2. Traitement des articles retournés
        for (RetourRequest.LigneRetourRequest item : request.getArticlesRetournes()) {
            LigneVente ligneVente = ligneVenteRepository.findById(item.getLigneVenteId())
                    .orElseThrow(() -> new RuntimeException("Ligne de vente introuvable"));

            if (!ligneVente.getVente().getId().equals(venteInitiale.getId())) {
                throw new RuntimeException("La ligne de vente ne correspond pas à la vente indiquée");
            }

            if (item.getQuantite() > ligneVente.getQuantite()) { // Simplification (idéalement vérifier quantité déjà retournée)
                throw new RuntimeException("Impossible de retourner plus que la quantité achetée");
            }

            // Calcul remboursement (au prix d'achat initial)
            BigDecimal montantLigne = ligneVente.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite()));
            montantTotalRembourse = montantTotalRembourse.add(montantLigne);

            // Création Ligne Retour
            LigneRetour ligneRetour = LigneRetour.builder()
                    .retour(retour)
                    .ligneVente(ligneVente)
                    .quantiteRetournee(item.getQuantite())
                    .build();
            lignesRetour.add(ligneRetour);

            // 3. Remise en Stock (Le produit est considéré comme revendable/neuf dans notre modèle simplifié)
            Produit produit = ligneVente.getProduit();
            Stock stock = stockRepository.findByProduitIdAndMagasinId(produit.getId(), magasin.getId())
                    .orElseThrow(() -> new RuntimeException("Stock introuvable")); // Ne devrait pas arriver

            stock.setQuantite(stock.getQuantite() + item.getQuantite());
            stockRepository.save(stock);

            // Historique Stock
            MouvementStock mvt = MouvementStock.builder()
                    .dateMvt(LocalDateTime.now())
                    .typeMvt("RETOUR_CLIENT")
                    .quantite(item.getQuantite()) // Positif = Entrée
                    .produit(produit)
                    .magasin(magasin)
                    .commentaire("Retour Vente #" + venteInitiale.getId())
                    .build();
            mouvementStockRepository.save(mvt);
        }

        retour.setLignesRetour(lignesRetour); // Note: Il faudra ajouter ce champ dans l'entité Retour si pas présent (Bidirectionnel) ou le gérer autrement
        retour.setMontantRembourse(montantTotalRembourse);
        
        Retour retourSauvegarde = retourRepository.save(retour);

        // 4. Transaction de Remboursement (Sortie de Caisse)
        Transaction transaction = Transaction.builder()
                .dateTransaction(LocalDateTime.now())
                .montant(montantTotalRembourse.negate()) // Négatif car sortie d'argent
                .typeTransaction("REMBOURSEMENT")
                .magasin(magasin)
                .retour(retourSauvegarde)
                .description("Remboursement Retour #" + retourSauvegarde.getId())
                .build();
        transactionRepository.save(transaction);

        return retourSauvegarde;
    }
}

