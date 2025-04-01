package Model.Machines;

import java.util.ArrayList;
import java.util.List;

import GUI.Reglage.RecolteuseReglage;
import Model.Machine;
import Model.MapG;
import Model.Ressources;
import Model.TypeRessource;
import geometry.Coordinates;

public class Recolteuse extends Machine {
    private TypeRessource ressourceCible; // Ressource à récolter
    private List<Ressources>inventaireRecolte; // Liste des ressources adjacentes

    public Recolteuse(Coordinates position, int stockMax) {
        super(position, stockMax);
        this.inventaireRecolte = new ArrayList<>(stockMax);;
    }

    // Configurer la récolteuse : Choisir la ressource à récolter
    @Override
    public void reglage(MapG map) {
        new RecolteuseReglage(this, map);
    }

    public List<Ressources> getRecolte() {
        return inventaireRecolte;
    }

    public void setRessourceCible(TypeRessource ressourceCible) {
        this.ressourceCible = ressourceCible;
    }

    // Action périodique : Récolter la ressource cible
    @Override
    public void actionPeriodique(MapG carte) {
        // Récupérer les ressources adjacentes uniquement si elles correspondent à la ressource cible
        List<Ressources> ressourcesAdjacentes = carte.getRessourcesAdjacentes(this.position);
    
        for (Ressources ressource : ressourcesAdjacentes) {
            // Vérifier si la ressource correspond à la ressource cible
            if (ressource.getType() == this.ressourceCible) {
                // Calculer la quantité à récolter (en fonction du stock et de la ressource)
                int quantiteDisponible = ressource.retirerQuantite(1);
                this.stockActuel += 1;
    
                if (quantiteDisponible > 0) {
                    inventaire.ajouterRessource(ressource, quantiteDisponible);
                    System.out.println("Récolté : " + ressource.getType() + " x " + quantiteDisponible);
                } else if (quantiteDisponible == 0){
                    ressource.rechargerRessource();
                }
            }
        }
    }
    

}
