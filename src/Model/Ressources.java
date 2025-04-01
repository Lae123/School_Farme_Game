package Model;

import java.io.Serializable;

public class Ressources  implements Serializable{
    private TypeRessource type;
    private int quantite;
    private long dernierRechargeTime; 
    protected static final int DELAI_RECHARGEMENT = 5000; 

    // Constructeur par défaut : aucune ressource
    public Ressources() {
        this.type = TypeRessource.NONE;
        this.quantite = 0;
        this.dernierRechargeTime = 0;
    }

    // Constructeur avec type et quantité
    public Ressources(TypeRessource type, int quantite) {
        this.type = type;
        this.quantite = quantite;
        this.dernierRechargeTime = 0; // Pas de délai de recharge initial
    }

    // Getters et setters
    public TypeRessource getType() {
        return type;
    }


    public void setType(TypeRessource type) {
        this.type = type;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // Méthode pour ajouter une quantité
    public void ajouterQuantite(int quantite) {
        this.quantite += quantite;
    }

    // Méthode pour retirer une quantité
    public int retirerQuantite(int quantite) {
        if (this.quantite >= quantite) {
            this.quantite -= quantite;
            return quantite;
        }
        return 0;
    }
    

    // Vérifie si la ressource est vide
    public boolean estVide() {
        if (this.quantite == 0) {
            return true;
        }
        return false;
    }

    // Méthode pour recharger la ressource après un certain délai
    public void rechargerRessource() {
        if (System.currentTimeMillis() - dernierRechargeTime >= DELAI_RECHARGEMENT) {
            int nouvelleQuantite = genererNouvelleQuantite();
            this.quantite = nouvelleQuantite; // Réinitialiser la ressource avec une nouvelle quantité
            dernierRechargeTime = System.currentTimeMillis();
            System.out.println("Recharge de la ressource : " + nouvelleQuantite + " unités ajoutées.");
        }
    }

    // Générer une nouvelle quantité lors de la recharge
    private int genererNouvelleQuantite() {
        // Par défaut, une valeur aléatoire entre 1 et 5
        return (int) (Math.random() * 5) + 1;
    }

    // Méthode pour vérifier si la ressource est prête à être récoltée
    public boolean estPrêteARecolter() {
        if (!estVide() || (System.currentTimeMillis() - dernierRechargeTime >= DELAI_RECHARGEMENT)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Ressources{" +
                "type=" + type +
                ", quantite=" + quantite +
                '}';
    }
}
