package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LivreDeRecette  implements Serializable{
    private List<Recette> recettes; // Liste des recettes disponibles

    public LivreDeRecette() {
        this.recettes = new ArrayList<>();
    }

    public void ajouterRecette(Recette recette) {
        recettes.add(recette);
    }

    public List<Recette> getRecettesDisponibles() {
        return recettes;
    }

    public Recette getRecetteByName(String nomRecette) {
        for (Recette recette : recettes) {
            if (recette.getNom().equalsIgnoreCase(nomRecette)) {
                return recette;
            }
        }
        System.out.println("Aucune recette trouvée avec le nom : " + nomRecette);
        return null; // Ou lever une exception si nécessaire
    }
    

    public List<Recette> filtrerRecettesParIngredients(Map<TypeRessource, Integer> inventaire) {
        // Filtrer les recettes craftables avec les ingrédients disponibles
        List<Recette> recettesFiltrees = recettes.stream()
                .filter(recette -> {
                    boolean ingredientsDisponibles = recette.ingredientsDisponibles(inventaire);
                    if (!ingredientsDisponibles) {
                        System.out.println("Recette " + recette.getNom() + " non disponible, ingrédients insuffisants.");
                    }
                    return ingredientsDisponibles;
                })
                .toList();
        return recettesFiltrees;
    }
    

    public Recette get(int index) {
        if (index >= 0 && index < recettes.size()) {
            return recettes.get(index);
        }
        throw new IndexOutOfBoundsException("Index invalide pour le LivreDeRecette.");
    }

    public void afficherRecettes() {
        System.out.println("Recettes disponibles dans le livre de cuisine :");
        for (Recette recette : recettes) {
            System.out.println(recette);
        }
    }
}
