# 🚀 Projet EdenComptab : Construction d'un PGI (ERP) Multi-Tenant en Spring Boot

**Auteur :** Junior Serges Kenfack Guessop 
**Date :** Novembre 2025  
**Technologies :** Java 17, Spring Boot 3, JPA/Hibernate, H2 Database, REST API.

---

## 1. 🎯 L'Objectif
L'idée était de concevoir le backend d'un **Progiciel de Gestion Intégré (PGI)** capable de gérer plusieurs magasins (Multi-Tenant) avec une contrainte forte : **Simplicité & Robustesse**.

Le système doit permettre à un commerçant de :
*   Gérer son catalogue et ses stocks en temps réel.
*   Encaisser des ventes (Comptant ou Crédit).
*   Gérer les Retours et Remboursements.
*   Suivre ses performances via un Dashboard instantané.

---

## 2. 🏗️ Architecture & Modélisation (La Fondation)

Avant d'écrire la première ligne de code, nous avons passé du temps à affiner le modèle de données (`EdenDB.sql`).

### Choix Structurants :
1.  **Multi-Tenancy Hiérarchique** :
    *   `Tenant` (L'entreprise) ➔ `Magasin` (Le point de vente).
    *   Toutes les données sont cloisonnées par `Magasin`.
2.  **Séparation Catalogue / Stock** :
    *   Le `Produit` est défini au niveau global (Tenant).
    *   Le `Stock` est géré localement par Magasin.
3.  **Simplification Radicale** :
    *   Suppression de l'héritage complexe (`Personne`) au profit d'entités plates (`Client`, `Utilisateur`).
    *   Centralisation des flux financiers dans une table unique `Transaction`.

### Le Schéma Final (Simplifié) :
*   `Vente` 1--* `LigneVente` *--1 `Produit`.
*   `Stock` décrémenté automatiquement à chaque vente.
*   `MouvementStock` pour la traçabilité (Audit Log).

---

## 3. 💻 Développement Backend (Spring Boot)

Nous avons initialisé une application **Spring Boot** standard avec les dépendances `spring-boot-starter-data-jpa` et `spring-boot-starter-web`.

### Étape 1 : La Couche "Domain" (Entités)
Traduction du schéma SQL en classes Java annotées avec JPA (`@Entity`).
*   *Challenge rencontré* : Les boucles infinies JSON (StackOverflow) lors des relations bidirectionnelles (Vente <-> LigneVente).
*   *Solution* : Utilisation de DTOs (Data Transfer Objects) pour découpler la base de données de l'API.

### Étape 2 : La Logique Métier (`VenteService` & `RetourService`)
C'est le "cerveau" de l'application. Des méthodes `@Transactional` gèrent les flux critiques :
1.  **Vérification** : Est-ce que le produit existe ? Y a-t-il assez de stock ?
2.  **Mise à jour Stock** : Décrémentation (Vente) ou Ré-incrémentation (Retour) + Historique `MouvementStock`.
3.  **Calcul Financier** : Gestion du paiement partiel (Crédit) via `CompteClient` ou Remboursement.
4.  **Encaissement** : Enregistrement dans `Transaction`.

```java
// Extrait de la logique de contrôle de stock
if (stock.getQuantite() < item.getQuantite()) {
    throw new RuntimeException("Stock insuffisant !");
}
stock.setQuantite(stock.getQuantite() - item.getQuantite());
```

### Étape 3 : L'API REST & DTOs
Nous avons exposé cette logique via des Contrôleurs REST (`VenteController`, `RetourController`, `DashboardController`).
*   Utilisation stricte des verbes HTTP (`POST` pour créer, `GET` pour lire).
*   Création de DTOs (`VenteRequest`, `RetourResponse`, etc.) pour une API propre et sécurisée.

---

## 4. 📊 Le Dashboard (Business Intelligence)

Pour apporter de la valeur immédiate, nous avons créé un endpoint d'agrégation `/api/dashboard/{magasinId}`.

Il calcule en temps réel :
*   ✅ **CA Journalier** (Somme des transactions du jour).
*   ✅ **Top Produit** (Le best-seller du jour).
*   ✅ **Dettes Clients** (L'argent qui est dehors).

Exemple de réponse JSON :
```json
{
    "caJournalier": 150.00,
    "nombreVentesJour": 12,
    "topProduitJour": "Coca-Cola",
    "totalDettesClients": 45.50
}
```

---

## 5. ✅ Tests & Validation

Nous avons utilisé **Postman** pour valider les scénarios bout-en-bout :
1.  Initialisation des données via `DataInitializer` (Magasin "Paris", Produit "Coca").
2.  Envoi d'une requête `POST /api/ventes` (Achat de 2 Coca).
3.  Vérification que le Stock est passé de 100 à 98.
4.  **Nouveau :** Test de `POST /api/retours` pour valider le remboursement et la remise en stock.
5.  Vérification que le Dashboard affiche bien le CA correspondant.

---

## 🚀 Conclusion & Perspectives (To-Do List)
Ce projet démontre une architecture **Backend solide**, prête à scaler.

**✅ Déjà réalisé :**
- [x] Modélisation SQL optimisée (Multi-Tenant)
- [x] API Ventes & Paiements
- [x] Gestion des Stocks Temps Réel
- [x] API Retours & Remboursements
- [x] Dashboard Analytique

**⏳ Prochaines étapes (Roadmap) :**
- [x] **CRUD Complets** : Créer les endpoints pour gérer les Clients, Produits et Stocks (actuellement via SQL/Init).
- [ ] **Sécurité** : Implémenter Spring Security & JWT pour authentifier employer d'un Tenant.
- [ ] **Frontend** : Développer l'interface utilisateur (React/Angular).
