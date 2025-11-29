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
            UtilisateurRepository utilisateurRepository
    ) {
        return args -> {
            System.out.println("--- DÉBUT INITIALISATION DONNÉES DE TEST ---");

            // 1. Création du Tenant
            Tenant tenant = Tenant.builder()
                    .nomSociete("Ma Super Entreprise")
                    .dateInscription(LocalDateTime.now())
                    .planAbonnement("Premium")
                    .build();
            tenant = tenantRepository.save(tenant);
            System.out.println("✅ Tenant créé : " + tenant.getNomSociete());

            // 2. Création du Magasin
            Magasin magasin = Magasin.builder()
                    .nomMagasin("Boutique Paris")
                    .adresse("12 Rue de la Paix")
                    .tenant(tenant)
                    .build();
            magasin = magasinRepository.save(magasin);
            System.out.println("✅ Magasin créé : " + magasin.getNomMagasin());

            // 3. Création d'un Utilisateur Admin
            Utilisateur admin = Utilisateur.builder()
                    .nomComplet("Jean Dupont")
                    .email("jean@admin.com")
                    .motDePasse("secret123") // (A hasher plus tard en prod)
                    .role("Admin")
                    .tenant(tenant)
                    .magasin(magasin)
                    .build();
            utilisateurRepository.save(admin);
            System.out.println("✅ Admin créé : " + admin.getNomComplet());

            // 4. Création Catalogue
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
            System.out.println("✅ Produit créé : " + coca.getNomProduit());

            // 5. Initialisation Stock
            Stock stockParis = Stock.builder()
                    .produit(coca)
                    .magasin(magasin)
                    .quantite(100)
                    .seuilAlerte(10)
                    .build();
            stockRepository.save(stockParis);
            System.out.println("✅ Stock initialisé à Paris : " + stockParis.getQuantite() + " unités");

            System.out.println("--- FIN INITIALISATION DONNÉES DE TEST ---");
        };
    }
}

