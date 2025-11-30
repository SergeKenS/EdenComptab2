package com.eden.comptab.service;

import com.eden.comptab.domain.*;
import com.eden.comptab.dto.VenteRequest;
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
public class VenteService {

    private final MagasinRepository magasinRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClientRepository clientRepository;
    private final ProduitRepository produitRepository;
    private final StockRepository stockRepository;
    private final VenteRepository venteRepository;
    private final TransactionRepository transactionRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final CompteClientRepository compteClientRepository;

    @Transactional
    public Vente traiterVente(VenteRequest request) {
        // 1. Validations de base
        Magasin magasin = magasinRepository.findById(request.getMagasinId())
                .orElseThrow(() -> new RuntimeException("Magasin introuvable"));
        
        Utilisateur vendeur = utilisateurRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Vendeur introuvable"));

        Client client = null;
        if (request.getClientId() != null) {
            client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client introuvable"));
        }

        // 2. Traitement des articles & Stocks
        Vente vente = Vente.builder()
                .dateVente(LocalDateTime.now())
                .magasin(magasin)
                .utilisateur(vendeur)
                .client(client)
                .statutPaiement("EN_COURS") // Sera mis à jour à la fin
                .build();

        BigDecimal montantTotalVente = BigDecimal.ZERO;
        List<LigneVente> lignesVente = new ArrayList<>();
        List<MouvementStock> mouvementsToSave = new ArrayList<>();

        for (VenteRequest.LigneVenteRequest item : request.getArticles()) {
            Produit produit = produitRepository.findById(item.getProduitId())
                    .orElseThrow(() -> new RuntimeException("Produit introuvable ID: " + item.getProduitId()));

            // Vérification Stock
            Stock stock = stockRepository.findByProduitIdAndMagasinId(produit.getId(), magasin.getId())
                    .orElseThrow(() -> new RuntimeException("Pas de stock pour ce produit dans ce magasin"));

            if (stock.getQuantite() < item.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour " + produit.getNomProduit() + 
                                         " (Demandé: " + item.getQuantite() + ", Dispo: " + stock.getQuantite() + ")");
            }

            // Décrémentation Stock
            stock.setQuantite(stock.getQuantite() - item.getQuantite());
            stockRepository.save(stock);

            // Historisation Mouvement Stock (Préparation)
            MouvementStock mvt = MouvementStock.builder()
                    .dateMvt(LocalDateTime.now())
                    .typeMvt("VENTE")
                    .quantite(-item.getQuantite()) // Négatif car sortie
                    .produit(produit)
                    .magasin(magasin)
                    .utilisateur(vendeur)
                    // Commentaire sera mis à jour plus tard
                    .build();
            mouvementsToSave.add(mvt);

            // Création Ligne Vente
            BigDecimal sousTotal = produit.getPrixVenteStandard().multiply(BigDecimal.valueOf(item.getQuantite()));
            LigneVente ligne = LigneVente.builder()
                    .vente(vente)
                    .produit(produit)
                    .quantite(item.getQuantite())
                    .prixUnitaire(produit.getPrixVenteStandard())
                    .sousTotal(sousTotal)
                    .build();
            
            lignesVente.add(ligne);
            montantTotalVente = montantTotalVente.add(sousTotal);
        }

        vente.setLignes(lignesVente);
        vente.setMontantTotal(montantTotalVente);

        // 3. Gestion du Paiement
        BigDecimal montantPaye = request.getMontantPaye() != null ? request.getMontantPaye() : BigDecimal.ZERO;
        
        // Calcul du reste à payer (ou monnaie à rendre si négatif, mais on simplifie ici)
        BigDecimal resteAPayer = montantTotalVente.subtract(montantPaye);

        if (resteAPayer.compareTo(BigDecimal.ZERO) > 0 && request.isACredit()) {
            if (client == null) throw new RuntimeException("Impossible de faire crédit sans Client identifié");
            
            vente.setStatutPaiement("PARTIEL/CREDIT");
            
            // Mise à jour du Compte Client (Dette)
            final Client clientFinal = client;
            CompteClient compte = compteClientRepository.findByClientIdAndMagasinId(client.getId(), magasin.getId())
                    .orElseGet(() -> {
                        CompteClient nouveau = CompteClient.builder()
                                .client(clientFinal)
                                .magasin(magasin)
                                .soldeActuel(BigDecimal.ZERO)
                                .build();
                        return compteClientRepository.save(nouveau);
                    });
            
            compte.setSoldeActuel(compte.getSoldeActuel().add(resteAPayer));
            compteClientRepository.save(compte);
            
        } else if (resteAPayer.compareTo(BigDecimal.ZERO) > 0 && !request.isACredit()) {
            throw new RuntimeException("Montant payé insuffisant (" + montantPaye + ") pour le total (" + montantTotalVente + ")");
        } else {
            vente.setStatutPaiement("COMPLET");
        }

        // Sauvegarde Vente Finale
        Vente venteSauvegardee = venteRepository.save(vente);

        // Sauvegarde Mouvements avec ID Vente correct
        for (MouvementStock mvt : mouvementsToSave) {
            mvt.setCommentaire("Vente #" + venteSauvegardee.getId());
            mouvementStockRepository.save(mvt);
        }

        // 4. Enregistrement Transaction (Encaissement réel uniquement)
        if (montantPaye.compareTo(BigDecimal.ZERO) > 0) {
            Transaction transaction = Transaction.builder()
                    .dateTransaction(LocalDateTime.now())
                    .montant(montantPaye) // On enregistre ce qu'on a vraiment reçu
                    .typeTransaction("VENTE")
                    .moyenPaiement(request.getMoyenPaiement())
                    .magasin(magasin)
                    .vente(venteSauvegardee)
                    .description("Encaissement Vente #" + venteSauvegardee.getId())
                    .build();
            transactionRepository.save(transaction);
        }

        return venteSauvegardee;
    }
}

