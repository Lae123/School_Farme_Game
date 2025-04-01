package Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Inventaire  implements Serializable{
    private Map<TypeRessource, Ressources> contenu;
    private int capaciteMax; // Capacité maximale totale de l'inventaire

    public Inventaire(int capaciteMax) {
        this.capaciteMax = capaciteMax; // Initialiser la capacité maximale
        this.contenu = new HashMap<>();
        for (TypeRessource type : TypeRessource.values()) {
            if (type != TypeRessource.NONE) {
                contenu.put(type, new Ressources(type, 0));
            }
        }
    }

    public Map<TypeRessource, Ressources> getContenu() {
        return contenu;
    }

    public int getQuantiteRessource(TypeRessource type) {
        // Vérifie si le type de ressource existe dans l'inventaire
        if (contenu.containsKey(type)) {
            // Retourne la quantité de cette ressource spécifique
            return contenu.get(type).getQuantite();
        }
        // Si la ressource n'existe pas, retourne 0
        return 0;
    }
    

    public int getStock() {
        return contenu.values().stream().mapToInt(Ressources::getQuantite).sum();
    }

    public boolean addRessource(TypeRessource type, int quantite) {
        // Vérifie la capacité maximale avant d'ajouter
        if (getStock() + quantite > capaciteMax) {
            System.out.println("Capacité maximale dépassée. Impossible d'ajouter " + quantite + " unités.");
            return false;
        }
    
        if (contenu.containsKey(type)) {
            contenu.get(type).ajouterQuantite(quantite);
        } else {
            contenu.put(type, new Ressources(type, quantite));
        }
        return true;
    }
    
    public void removeRessource(TypeRessource type, int quantite) {
        if (type != TypeRessource.NONE && contenu.containsKey(type)) {
            contenu.get(type).retirerQuantite(quantite);
        }
    }
    

    public boolean ajouterRessource(Ressources ressource, int quantite) {
        int stockActuel = getStock();
        if (stockActuel + quantite > capaciteMax) {
            System.out.println("Capacité maximale dépassée. Impossible d'ajouter " + quantite + " unités.");
            return false; // Ajout échoué
        }

        TypeRessource type = ressource.getType();
        System.out.println("Voila le TYPEPEJZOSDMKLHN?.D%MLK : " + type);
        if (contenu.containsKey(type)) {
            Ressources ressourceExistante = contenu.get(type);
            ressourceExistante.ajouterQuantite(quantite);
        } else {
            contenu.put(type, new Ressources(type, quantite));
        }
        return true; // Ajout réussi
    }

    public void retirerRessource(Ressources ressource, int quantite) {
        TypeRessource type = ressource.getType();

        if (type != TypeRessource.NONE && contenu.containsKey(type)) {
            contenu.get(type).retirerQuantite(quantite);
        }   
    }

    public void retirerRessource(TypeRessource type, int quantite){
        if (type != TypeRessource.NONE && contenu.containsKey(type)) {
            contenu.get(type).retirerQuantite(quantite);
        }   
    }

    public void afficherContenu() {
        System.out.println("Contenu de l'inventaire :");
        for (Ressources ressource : contenu.values()) {
            if (!ressource.estVide()) {
                System.out.println(" - " + ressource);
            }
        }
        System.out.println("Stock actuel : " + getStock() + "/" + capaciteMax);
    }

    public boolean estVide() {
        return contenu.values().stream().allMatch(Ressources::estVide);
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public boolean hasSufficientIngredients(Recette recette) {
        // Parcourt les ingrédients de la recette
        for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
            TypeRessource type = ingredient.getKey();
            int quantiteRequise = ingredient.getValue();
            
            // Vérifie si la quantité de ressource disponible est suffisante
            if (getQuantiteRessource(type) < quantiteRequise) {
                return false; // Si une ressource est insuffisante, retourne false
            }
        }
        return true; // Si toutes les ressources sont suffisantes
    }

    public void craft(Recette recette) {
        if (hasSufficientIngredients(recette)) {
            // Retirer les ingrédients nécessaires
            for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
                TypeRessource type = ingredient.getKey();
                int quantiteRequise = ingredient.getValue();
                
                // Retirer les ressources de l'inventaire
                Ressources ressource = contenu.get(type);
                ressource.retirerQuantite(quantiteRequise);
            }
    
            // Ajouter le produit fabriqué
            TypeRessource produit = recette.getProduit();
            ajouterRessource(new Ressources(produit, 1), 1);  // Supposons que 1 unité du produit est fabriquée
        } else {
            System.out.println("Ressources insuffisantes pour fabriquer l'objet.");
        }
    }
    
    

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }
}
