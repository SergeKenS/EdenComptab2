-- ######################################
-- 0. TABLES DE TENANCY
-- ######################################

-- L'entité SaaS cliente (l'entreprise qui possède les magasins)
CREATE TABLE TENANT (
    ID_Tenant INTEGER PRIMARY KEY,
    Nom_Société VARCHAR(150) NOT NULL,
    Date_Inscription DATETIME NOT NULL,
    Plan_Abonnement VARCHAR(50)
);

-- Les magasins physiques
CREATE TABLE MAGASIN (
    ID_Magasin INTEGER PRIMARY KEY,
    Nom_Magasin VARCHAR(100) NOT NULL,
    Adresse TEXT,
    ID_Tenant INTEGER NOT NULL,
    FOREIGN KEY (ID_Tenant) REFERENCES TENANT(ID_Tenant)
);


-- ######################################
-- 1. ACTEURS (Simplifié : Plus de table PERSONNE)
-- ######################################

-- UTILISATEUR (Staff/Vendeurs)
CREATE TABLE UTILISATEUR (
    ID_Utilisateur INTEGER PRIMARY KEY,
    Nom_Complet VARCHAR(200) NOT NULL,
    Email VARCHAR(100),
    Mot_De_Passe VARCHAR(255) NOT NULL,
    Role VARCHAR(50) NOT NULL, -- 'Admin', 'Manager', 'Vendeur'
    ID_Magasin INTEGER, -- Peut être NULL si Admin Tenant
    ID_Tenant INTEGER NOT NULL,
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin),
    FOREIGN KEY (ID_Tenant) REFERENCES TENANT(ID_Tenant)
);

-- CLIENT lié au Magasin
CREATE TABLE CLIENT (
    ID_Client INTEGER PRIMARY KEY,
    Nom_Complet VARCHAR(200) NOT NULL,
    Tél VARCHAR(20),
    Email VARCHAR(100),
    Statut_Fidélité VARCHAR(50) DEFAULT 'Standard',
    ID_Magasin INTEGER NOT NULL,
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin)
);

-- Suivi du solde de crédit/avance par client
CREATE TABLE COMPTE_CLIENT (
    ID_Compte INTEGER PRIMARY KEY,
    ID_Client INTEGER NOT NULL,
    ID_Magasin INTEGER NOT NULL,
    Solde_Actuel DECIMAL(10, 2) DEFAULT 0, -- Positif = Dû par le client; Négatif = Avance
    UNIQUE (ID_Client, ID_Magasin),
    FOREIGN KEY (ID_Client) REFERENCES CLIENT(ID_Client),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin)
);


-- ######################################
-- 2. CATALOGUE ET STOCKS
-- ######################################

CREATE TABLE CATEGORIE (
    ID_Categorie INTEGER PRIMARY KEY,
    Nom_Categorie VARCHAR(100) NOT NULL,
    ID_Tenant INTEGER NOT NULL,
    FOREIGN KEY (ID_Tenant) REFERENCES TENANT(ID_Tenant)
);

CREATE TABLE PRODUIT (
    ID_Produit INTEGER PRIMARY KEY,
    Nom_Produit VARCHAR(150) NOT NULL,
    Code_Barre VARCHAR(50),
    Description TEXT,
    Prix_Vente_Standard DECIMAL(10, 2) NOT NULL,
    Coût_Achat_Standard DECIMAL(10, 2), -- Utile pour la marge théorique
    ID_Categorie INTEGER,
    ID_Tenant INTEGER NOT NULL,
    FOREIGN KEY (ID_Categorie) REFERENCES CATEGORIE(ID_Categorie),
    FOREIGN KEY (ID_Tenant) REFERENCES TENANT(ID_Tenant)
);

-- Table de stock (Inventaire instantané)
CREATE TABLE STOCK (
    ID_Stock INTEGER PRIMARY KEY,
    ID_Produit INTEGER NOT NULL,
    ID_Magasin INTEGER NOT NULL,
    Quantite INTEGER DEFAULT 0,
    Seuil_Alerte INTEGER DEFAULT 5,
    FOREIGN KEY (ID_Produit) REFERENCES PRODUIT(ID_Produit),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin),
    UNIQUE(ID_Produit, ID_Magasin)
);

-- NOUVEAU : Historique des mouvements (Traçabilité)
CREATE TABLE MOUVEMENT_STOCK (
    ID_Mvt INTEGER PRIMARY KEY,
    Date_Mvt DATETIME NOT NULL,
    Type_Mvt VARCHAR(50) NOT NULL, -- VENTE, LIVRAISON, PERTE, RETOUR, INVENTAIRE
    Quantite INTEGER NOT NULL, -- Positif (Entrée) ou Négatif (Sortie)
    Commentaire TEXT,
    ID_Produit INTEGER NOT NULL,
    ID_Magasin INTEGER NOT NULL,
    ID_Utilisateur INTEGER, -- Qui a fait le mouvement
    FOREIGN KEY (ID_Produit) REFERENCES PRODUIT(ID_Produit),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin),
    FOREIGN KEY (ID_Utilisateur) REFERENCES UTILISATEUR(ID_Utilisateur)
);


-- ######################################
-- 3. VENTES ET RETOURS
-- ######################################

CREATE TABLE VENTE (
    ID_Vente INTEGER PRIMARY KEY,
    Date_Vente DATETIME NOT NULL,
    Montant_Total DECIMAL(10, 2) NOT NULL,
    Statut_Paiement VARCHAR(50) NOT NULL, -- 'Complet', 'Partiel/Crédit', 'Annulé'
    ID_Client INTEGER,
    ID_Utilisateur INTEGER NOT NULL,
    ID_Magasin INTEGER NOT NULL,
    FOREIGN KEY (ID_Client) REFERENCES CLIENT(ID_Client),
    FOREIGN KEY (ID_Utilisateur) REFERENCES UTILISATEUR(ID_Utilisateur),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin)
);

CREATE TABLE LIGNE_VENTE (
    ID_Ligne_Vente INTEGER PRIMARY KEY,
    ID_Vente INTEGER NOT NULL,
    ID_Produit INTEGER NOT NULL,
    Quantite INTEGER NOT NULL,
    Prix_Unitaire DECIMAL(10, 2) NOT NULL,
    Sous_Total DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (ID_Vente) REFERENCES VENTE(ID_Vente),
    FOREIGN KEY (ID_Produit) REFERENCES PRODUIT(ID_Produit)
);

CREATE TABLE RETOUR (
    ID_Retour INTEGER PRIMARY KEY,
    Date_Retour DATETIME NOT NULL,
    Montant_Remboursé DECIMAL(10, 2),
    Raison_Retour TEXT,
    ID_Vente_Initiale INTEGER NOT NULL,
    ID_Magasin INTEGER NOT NULL,
    FOREIGN KEY (ID_Vente_Initiale) REFERENCES VENTE(ID_Vente),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin)
);

CREATE TABLE LIGNE_RETOUR (
    ID_Ligne_Retour INTEGER PRIMARY KEY,
    ID_Retour INTEGER NOT NULL,
    ID_Ligne_Vente INTEGER NOT NULL,
    Quantité_Retournée INTEGER NOT NULL,
    FOREIGN KEY (ID_Retour) REFERENCES RETOUR(ID_Retour),
    FOREIGN KEY (ID_Ligne_Vente) REFERENCES LIGNE_VENTE(ID_Ligne_Vente)
);


-- ######################################
-- 4. FINANCE ET PAIEMENTS (Centralisé)
-- ######################################

-- Tout flux d'argent passe par ici
CREATE TABLE TRANSACTION (
    ID_Transaction INTEGER PRIMARY KEY,
    Date_Transaction DATETIME NOT NULL,
    Montant DECIMAL(10, 2) NOT NULL,
    Type_Transaction VARCHAR(50) NOT NULL, -- VENTE, REMBOURSEMENT, DEPENSE, APPORT
    Moyen_Paiement VARCHAR(50),
    Description TEXT,
    
    -- Liens optionnels (Une transaction peut être liée à une Vente ou un Retour)
    ID_Vente INTEGER,
    ID_Retour INTEGER,
    
    ID_Magasin INTEGER NOT NULL,
    
    FOREIGN KEY (ID_Vente) REFERENCES VENTE(ID_Vente),
    FOREIGN KEY (ID_Retour) REFERENCES RETOUR(ID_Retour),
    FOREIGN KEY (ID_Magasin) REFERENCES MAGASIN(ID_Magasin)
);
