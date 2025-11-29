package com.eden.comptab;

import com.eden.comptab.domain.Magasin;
import com.eden.comptab.domain.Produit;
import com.eden.comptab.domain.Stock;
import com.eden.comptab.domain.Tenant;
import com.eden.comptab.repository.MagasinRepository;
import com.eden.comptab.repository.ProduitRepository;
import com.eden.comptab.repository.StockRepository;
import com.eden.comptab.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EdenComptabApplicationTests {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private MagasinRepository magasinRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    void contextLoadsAndDataIsInitialized() {
        // Vérification du Tenant
        List<Tenant> tenants = tenantRepository.findAll();
        assertThat(tenants).hasSize(1);
        assertThat(tenants.get(0).getNomSociete()).isEqualTo("Ma Super Entreprise");

        // Vérification du Magasin
        List<Magasin> magasins = magasinRepository.findAll();
        assertThat(magasins).hasSize(1);
        assertThat(magasins.get(0).getNomMagasin()).isEqualTo("Boutique Paris");

        // Vérification du Produit
        List<Produit> produits = produitRepository.findAll();
        assertThat(produits).hasSize(1);
        assertThat(produits.get(0).getNomProduit()).isEqualTo("Coca-Cola 33cl");

        // Vérification du Stock
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(1);
        assertThat(stocks.get(0).getQuantite()).isEqualTo(100);
        
        System.out.println(">>> TEST RÉUSSI : Toutes les données de base sont présentes en base !");
    }
}

