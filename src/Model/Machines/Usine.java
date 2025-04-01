package Model.Machines;

import java.util.List;
import java.util.Map;

import Model.Machine;
import Model.MapG;
import Model.Recette;
import Model.Ressources;
import Model.TypeRessource;
import geometry.Coordinates;

public class Usine extends Machine {
    private Recette recetteActuelle; // La recette actuellement utilisée
    private List<Recette> recettesDisponibles; // Liste des recettes disponibles pour cette usine

    public Usine(Coordinates position, int stockMax) {
        super(position, stockMax);
    }

    public void setRecettesDisponibles(List<Recette> recettesDisponibles) {
        this.recettesDisponibles = recettesDisponibles;
    }

    public void setRecetteActuelle(Recette recetteActuelle) {
        this.recetteActuelle = recetteActuelle;
    }

    public Recette getRecetteActuelle() {
        return recetteActuelle;
    }

    // Méthode pour obtenir les produits fabriqués
    public Map<TypeRessource, Ressources> getStockProduits() {
        return this.getInventaire().getContenu(); // Récupère l'inventaire
    }

    @Override
    public int getStockActuel() {
        return super.getStockActuel();
    }

    @Override
    public void reglage(MapG map) {
        // Pas la peine
    }

    public List<Recette> getRecettesDisponibles() {
        return recettesDisponibles;
    }

    @Override
    protected void actionPeriodique(MapG map) {
        if (!active) {
            System.out.println("L'usine est inactive.");
            return;
        }

        if (recetteActuelle == null) {
            System.out.println("Aucune recette n'a été configurée pour l'usine.");
            return;
        }

        // Vérifier les ingrédients nécessaires à la production
        Map<TypeRessource, Ressources> inventaire = this.getInventaire().getContenu();
        boolean ingredientsDisponibles = checkIngredientsDisponibles(inventaire);

        if (!ingredientsDisponibles) {
            System.out.println("Ingrédients insuffisants pour produire : " + recetteActuelle.getNom());
            return;
        }

        // Si tous les ingrédients sont disponibles, commencer la production
        produire(inventaire);
    }

    // Vérification des ingrédients nécessaires pour la recette
    private boolean checkIngredientsDisponibles(Map<TypeRessource, Ressources> inventaire) {
        return recetteActuelle.getIngredients().entrySet().stream()
                .allMatch(entry -> inventaire.getOrDefault(entry.getKey(), new Ressources(entry.getKey(), 0)).getQuantite() >= entry.getValue());
    }

    // Production des ressources, une fois les ingrédients vérifiés

    private void produire(Map<TypeRessource, Ressources> inventaire) {
        // Consommer les ingrédients nécessaires à la recette
        for (Map.Entry<TypeRessource, Integer> entry : recetteActuelle.getIngredients().entrySet()) {
            TypeRessource ressource = entry.getKey();
            int quantiteNecessaire = entry.getValue();
            Ressources ressourceActuelle = inventaire.get(ressource);
            if (ressourceActuelle != null) {
                ressourceActuelle.retirerQuantite(quantiteNecessaire); // Retirer les ingrédients de l'inventaire
            }
        }
    
        // Vérifier si le stock peut accueillir le produit fabriqué
        int produitQuantite = recetteActuelle.getQuantiteProduit();
        if (stockActuel + produitQuantite > this.getInventaire().getStock()) {
            System.out.println("Stock plein, impossible de produire davantage.");
            return;
        }
    
        // Ajouter le produit fabriqué à l'inventaire
        TypeRessource produitType = recetteActuelle.getProduit();
        Ressources produit = inventaire.get(produitType);
        if (produit == null) {
            produit = new Ressources(produitType, produitQuantite);
            inventaire.put(produitType, produit);
        } else {
            produit.ajouterQuantite(produitQuantite);
        }
    
        stockActuel += produitQuantite; // Mettre à jour le stock actuel
    
        System.out.println("Produit fabriqué : " + produitType +
                " (" + produitQuantite + "). Stock actuel : " + stockActuel);
    }
    

    @Override
    public String toString() {
        return "Usine{" +
                "position=" + position +
                ", recetteActuelle=" + (recetteActuelle != null ? recetteActuelle.getNom() : "Aucune") +
                ", stockActuel=" + stockActuel +
                ", active=" + active +
                '}';
    }
}
