package com.eden.comptab.config;

import com.eden.comptab.domain.*;
import com.eden.comptab.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            TenantRepository tenantRepository,
            MagasinRepository magasinRepository,
            CategorieRepository categorieRepository,
            ProduitRepository produitRepository,
            StockRepository stockRepository,
            UtilisateurRepository utilisateurRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder
    ) {
        return args -> {
            System.out.println("--- DÉBUT INITIALISATION DONNÉES DE TEST ---");

            // ... (Code existant pour Tenant et Magasin) ...
            // 1. Création du Tenant
            if (tenantRepository.count() > 0) return; // Eviter doublons si redémarrage sans drop-create

            Tenant tenant = Tenant.builder()
                    .nomSociete("Ma Super Entreprise")
                    .dateInscription(LocalDateTime.now())
                    .planAbonnement("Premium")
                    .build();
            tenant = tenantRepository.save(tenant);

            // 2. Création du Magasin
            Magasin magasin = Magasin.builder()
                    .nomMagasin("Boutique Paris")
                    .adresse("12 Rue de la Paix")
                    .tenant(tenant)
                    .build();
            magasin = magasinRepository.save(magasin);

            // 3. Création d'un Utilisateur Admin
            Utilisateur admin = Utilisateur.builder()
                    .nomComplet("Jean Dupont")
                    .email("jean@admin.com")
                    .motDePasse(passwordEncoder.encode("secret123")) // Hashage du mot de passe !
                    .role("Admin")
                    .tenant(tenant)
                    .magasin(magasin)
                    .build();
            utilisateurRepository.save(admin);
            System.out.println("✅ Admin créé : " + admin.getNomComplet() + " (Pass: secret123)");

            // 4. Création Catalogue (Suite du code...)
            Categorie catBoissons = Categorie.builder()
                    .nomCategorie("Boissons")
                    .tenant(tenant)
                    .build();
            catBoissons = categorieRepository.save(catBoissons);

            Produit coca = Produit.builder()
                    .nomProduit("Coca-Cola 33cl")
                    .codeBarre("54490001")
                    .prixVenteStandard(new BigDecimal("1.50"))
                    .coutAchatStandard(new BigDecimal("0.80"))
                    .categorie(catBoissons)
                    .tenant(tenant)
                    .build();
            coca = produitRepository.save(coca);
            
            // 5. Initialisation Stock
            Stock stockParis = Stock.builder()
                    .produit(coca)
                    .magasin(magasin)
                    .quantite(100)
                    .seuilAlerte(10)
                    .build();
            stockRepository.save(stockParis);

            System.out.println("--- FIN INITIALISATION DONNÉES DE TEST ---");
        };
    }
}

