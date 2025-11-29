package com.eden.comptab.service;

import com.eden.comptab.domain.Vente;
import com.eden.comptab.domain.LigneVente;
import com.eden.comptab.dto.DashboardResponse;
import com.eden.comptab.repository.CompteClientRepository;
import com.eden.comptab.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VenteRepository venteRepository;
    private final CompteClientRepository compteClientRepository;

    public DashboardResponse getSyntheseMagasin(Long magasinId) {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1);
        LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        List<Vente> toutesVentes = venteRepository.findByMagasinId(magasinId);

        // 1. CA Journalier
        List<Vente> ventesJour = toutesVentes.stream()
                .filter(v -> v.getDateVente().isAfter(debutJour) && v.getDateVente().isBefore(finJour))
                .filter(v -> !v.getStatutPaiement().equals("ANNULE"))
                .collect(Collectors.toList());

        BigDecimal caJour = ventesJour.stream()
                .map(Vente::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. CA Mensuel
        BigDecimal caMois = toutesVentes.stream()
                .filter(v -> v.getDateVente().isAfter(debutMois))
                .filter(v -> !v.getStatutPaiement().equals("ANNULE"))
                .map(Vente::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Top Produit du Jour
        String topProduit = "Aucun";
        Integer topQte = 0;
        
        if (!ventesJour.isEmpty()) {
            Map<String, Integer> produitsVendus = ventesJour.stream()
                .flatMap(v -> v.getLignes().stream())
                .collect(Collectors.groupingBy(
                    l -> l.getProduit().getNomProduit(),
                    Collectors.summingInt(LigneVente::getQuantite)
                ));
            
            var entry = produitsVendus.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
                
            if (entry != null) {
                topProduit = entry.getKey();
                topQte = entry.getValue();
            }
        }

        // 4. Total Dettes Clients (Somme des soldes positifs)
        BigDecimal dettes = compteClientRepository.findAll().stream() // Idéalement filtrer par magasinId dans le repo directement
                .filter(c -> c.getMagasin().getId().equals(magasinId))
                .map(c -> c.getSoldeActuel())
                .filter(solde -> solde.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardResponse.builder()
                .caJournalier(caJour)
                .nombreVentesJour(ventesJour.size())
                .caMensuel(caMois)
                .topProduitJour(topProduit)
                .quantiteTopProduit(topQte)
                .totalDettesClients(dettes)
                .build();
    }
}

