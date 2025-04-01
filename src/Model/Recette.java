package Model;

import java.io.Serializable;
import java.util.Map;

public class Recette  implements Serializable{

    private String nom; 
    private Map<TypeRessource, Integer> ingredients; 
    private TypeRessource produit; 
    private int quantiteProduit; 
    private int duree; // Temps en millisecondes. 0 = recette instantanée.
    private int prix;

    public Recette(String nom, Map<TypeRessource, Integer> ingredients, TypeRessource produit, int quantiteProduit, int duree, int prix) {
        this.nom = nom;
        this.ingredients = ingredients;
        this.produit = produit;
        this.quantiteProduit = quantiteProduit;
        this.duree = duree; // Si 0, c'est une recette instantanée
        this.prix = prix;
    }

    public int getPrix() {
        return prix;
    }

    public String getNom() {
        return nom;
    }

    public Map<TypeRessource, Integer> getIngredients() {
        return ingredients;
    }

    public TypeRessource getProduit() {
        return produit;
    }

    public int getQuantiteProduit() {
        return quantiteProduit;
    }

    public int getDuree() {
        return duree;
    }

    public boolean estInstantanee() {
        return duree == 0;
    }

    public boolean ingredientsDisponibles(Map<TypeRessource, Integer> inventaire) {
        return ingredients.entrySet().stream()
                .allMatch(entry -> inventaire.getOrDefault(entry.getKey(), 0) >= entry.getValue());
    }

    public void consommerIngredients(Map<TypeRessource, Integer> inventaire) {
        ingredients.forEach((type, quantite) -> {
            inventaire.put(type, inventaire.get(type) - quantite);
        });
    }

    @Override
    public String toString() {
        return "Recette{" +
                "nom='" + nom + '\'' +
                ", ingredients=" + ingredients +
                ", produit=" + produit +
                ", quantiteProduit=" + quantiteProduit +
                ", duree=" + duree +
                '}';
    }
}
