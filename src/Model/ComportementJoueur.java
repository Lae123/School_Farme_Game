package Model;

import java.io.Serializable;
import java.util.Map;

import GUI.ShopScreen;
import Helper.LoadSound;
import Model.Machines.Recolteuse;
import Model.Machines.Usine;
import geometry.Coordinates;

public class ComportementJoueur implements Actionnable, Serializable {
    private static final long serialVersionUID = 1L;
    private static final long TEMPS_RECOLTE_LABORIEUSE = 3000; // 3 secondes pour la récolte laborieuse

    private Joueur player;

    // Constructeur
    public ComportementJoueur(Joueur j) {
        this.player = j;
    }

    @Override
    public void deplacer(Coordinates pos) {
        if (player.getMouvement()) {
            player.setPos(pos);
        } else {
            System.out.println("Le joueur ne peut pas se déplacer pour le moment.");
        }
    }

    @Override
    public void ajouterRessource(Ressources ressource, int quantite) {
        if (ressource != null) {
            player.getInv().ajouterRessource(ressource, quantite);
        } else {
            System.out.println("Erreur : La ressource est nulle.");
        }
    }

    @Override
    public void retirerArgent(int montant) {
        if (player.getArgent().getValeur() >= montant) {
            player.getArgent().retirer(montant);
        } else {
            System.out.println("Fonds insuffisants !");
        }
    }

    @Override
    public void ajouterArgent(int montant) {
        player.getArgent().ajouter(montant);
    }

    @Override
    public boolean transfererRessourcesVersUsine(Usine usine, Recette recette) {
        Inventaire inventaireJoueur = player.getInv();
        Inventaire inventaireUsine = usine.getInventaire();

        // Vérification si le joueur a toutes les ressources nécessaires
        for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
            TypeRessource type = ingredient.getKey();
            int quantiteRequise = ingredient.getValue();

            if (inventaireJoueur.getQuantiteRessource(type) < quantiteRequise) {
                System.out.println("Ressources insuffisantes pour transférer : " + type);
                return false; // Échec du transfert
            }
        }

        // Transfert des ressources
        for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
            TypeRessource type = ingredient.getKey();
            int quantiteRequise = ingredient.getValue();

            // Retirer du joueur et ajouter à l'usine
            inventaireJoueur.retirerRessource(type, quantiteRequise);
            if (!inventaireUsine.addRessource(type, quantiteRequise)) {
                System.out.println("Capacité de l'inventaire de l'usine dépassée pour : " + type);
                // Annuler le transfert déjà effectué
                inventaireJoueur.ajouterRessource(new Ressources(type, quantiteRequise), quantiteRequise);
                return false; // Échec du transfert
            }
        }

        System.out.println("Transfert des ressources effectué avec succès.");
        return true; // Succès du transfert
    }
    
    public void afficherMachines() {
        // Parcourt la liste des machines du joueur
        if (player.getMachines().isEmpty()) {
            System.out.println("Le joueur ne possède aucune machine.");
        } else {
            System.out.println("Machines du joueur :");
            for (Machine machine : player.getMachines()) {
                System.out.println(machine); // Cela appelle la méthode toString de chaque machine
            }
        }
    }
    
    

    @Override
    public boolean placerMachine(Machine machine, Coordinates position, MapG carte) {
        if (machine == null || position == null || carte == null) {
            System.out.println("Erreur : Paramètres invalides pour placer une machine.");
            return false;
        }

        afficherMachines();

        // Check if the player owns the machine
        if (!player.possedeMachine(machine)) {
            System.out.println("Le joueur ne possède pas cette machine.");
            return false;
        }

        Case caseCourante = carte.getCaseAt(position);
        if (!carte.isValidMove(position) || caseCourante.getType() != CaseType.VIDE) {
            System.out.println("Impossible de placer la machine : case non valide ou occupée.");
            return false;
        }

        if (machine instanceof Recolteuse && !carte.estAdjacenteARessource(position)) {
            System.out.println("Impossible de placer une récolteuse : aucune ressource adjacente.");
            return false;
        }
        LoadSound.playSound("button_click");
        caseCourante.setType(CaseType.MACHINE);
        caseCourante.setMachine(machine);
        machine.setPosition(position);

        System.out.println("Machine placée avec succès à la position : " + position +
                " - ID Machine : " + System.identityHashCode(machine));
        return true;
    }


    @Override
    public void checkIfOnMarket(MapG map, Partie partie) {
        if (map == null) {
            System.out.println("Erreur : La carte est nulle.");
            return;
        }

        Coordinates playerPosition = player.getPos();
        Case currentCase = map.getCaseAt(playerPosition);

        if (currentCase != null && currentCase.getType() == CaseType.MARCHE) {
            System.out.println("Le joueur est sur le marché !");

            // Vérifie si un marché existe déjà
            if (partie.getShopScreen() == null || !partie.getShopScreen().isVisible()) {
                ShopScreen shop = new ShopScreen(player, partie);
                partie.setShopScreen(shop);
                shop.setVisible(true);
            } else {
                System.out.println("Le marché est déjà ouvert.");
            }
        }
    }


    @Override
    public void recolteLaboriuse(Coordinates positionCible, MapG carte) {
        if (!carte.estAdjacenteARessource(player.getPos())) {
            System.out.println("Le joueur doit être adjacent à la ressource.");
            return;
        }

        Case caseRessource = carte.getCaseAt(positionCible);
        if (caseRessource == null || caseRessource.getType() != CaseType.RESSOURCE) {
            System.out.println("Pas de ressource à récolter ici.");
            return;
        }

        Ressources ressource = caseRessource.getRessource();
        if (ressource == null || !ressource.estPrêteARecolter()) {
            System.out.println("La ressource n'est pas prête à être récoltée.");
            return;
        }

        // Bloquer le mouvement du joueur
        player.setMouvement(false);

        try {
            System.out.println("Récolte laborieuse en cours...");
            Thread.sleep(TEMPS_RECOLTE_LABORIEUSE);
            
            // Récupérer une unité de ressource
            if (!ressource.estVide()) {
                Ressources nouvelleRessource = new Ressources(ressource.getType(), 1);
                ajouterRessource(nouvelleRessource, 1);
                ressource.retirerQuantite(1);
                
                if (ressource.estVide()) {
                    ressource.rechargerRessource();
                }
                System.out.println("Récolte laborieuse réussie !");
            }
        } catch (InterruptedException e) {
            System.out.println("Récolte interrompue");
        } finally {
            // Débloquer le mouvement du joueur
            player.setMouvement(true);
        }
    }


    // Récolte temporisée - action instantanée mais avec délai avant disponibilité
    public void recolteTemporisee(Coordinates positionCible, MapG carte) {
        if (!carte.estAdjacenteARessource(player.getPos())) {
            System.out.println("Le joueur doit être adjacent à la ressource.");
            return;
        }

        Case caseRessource = carte.getCaseAt(positionCible);
        if (caseRessource == null || caseRessource.getType() != CaseType.RESSOURCE) {
            System.out.println("Pas de ressource à récolter ici.");
            return;
        }

        Ressources ressource = caseRessource.getRessource();
        if (ressource == null || !ressource.estPrêteARecolter()) {
            System.out.println("La ressource n'est pas prête à être récoltée.");
            return;
        }

        // Récolte instantanée
        if (!ressource.estVide()) {
            Ressources nouvelleRessource = new Ressources(ressource.getType(), 1);
            ajouterRessource(nouvelleRessource, 1);
            ressource.retirerQuantite(1);
            
            // Force un délai avant la prochaine récolte en mettant à jour dernierRechargeTime
            ressource.rechargerRessource();
            System.out.println("Récolte temporisée effectuée ! La ressource sera à nouveau disponible dans " 
                             + (Ressources.DELAI_RECHARGEMENT/1000) + " secondes.");
        }
    }

    // Méthode utilitaire pour vérifier si une ressource est récoltable
    public boolean peutRecolter(Coordinates positionCible, MapG carte) {
        if (!carte.estAdjacenteARessource(player.getPos())) {
            return false;
        }

        Case caseRessource = carte.getCaseAt(positionCible);
        if (caseRessource != null && caseRessource.getType() == CaseType.RESSOURCE) {
            Ressources ressource = caseRessource.getRessource();
            return ressource != null && ressource.estPrêteARecolter();
        }
        return false;
    }
}
