package Model;

import java.io.Serializable;

public class Monnaie  implements Serializable{
    private int valeur;

    public Monnaie(int valeur) {
        this.valeur = valeur;
    }

    public int getValeur() {
        return valeur;
    }

    public void ajouter(int montant) {
        this.valeur += montant;
    }

    public void retirer(int montant) {
        if (montant <= valeur) {
            this.valeur -= montant;
        }
    }

    @Override
    public String toString() {
        return "Monnaie{" +
                "valeur=" + valeur +
                '}';
    }
}
