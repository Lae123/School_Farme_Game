package Model;

import java.io.Serializable;

public class Case implements Serializable{
    private CaseType type;         // Type de la case (VIDE, MACHINE, etc.)
    private Ressources ressource; // Ressources associées (uniquement pour RESSOURCE)
    private Machine machine;       // Machine associée (uniquement pour MACHINE)
    private int clics;             // Compteur de clics sur cette case

    // Constructeur
    public Case(CaseType type) {
        this.type = type;
    }

    // Getters et setters
    public CaseType getType() {
        return type;
    }

    public void setType(CaseType type) {
        this.type = type;
        // Réinitialiser les données spécifiques au changement de type
        if (type != CaseType.RESSOURCE) {
            this.ressource = null;
        }
        if (type != CaseType.MACHINE) {
            this.machine = null;
        }
    }

    public Ressources getRessource() {
        if (type != CaseType.RESSOURCE) {
            throw new IllegalStateException("Cette case n'est pas de type RESSOURCE.");
        }
        return ressource;
    }

    public void setRessource(Ressources ressource) {
        if (type != CaseType.RESSOURCE) {
            throw new IllegalStateException("Impossible d'associer une ressource : la case n'est pas de type RESSOURCE.");
        }
        this.ressource = ressource;
    }

    public Machine getMachine() {
        if (type != CaseType.MACHINE) {
            return null;
        }
        return machine;
    }

    public void setMachine(Machine machine) {
        if (type != CaseType.MACHINE) {
            throw new IllegalStateException("Impossible d'associer une machine : la case n'est pas de type MACHINE.");
        }
        this.machine = machine;
    }

    // Gestion des clics
    public void incrementClics() {
        clics++;
    }

    public int getClics() {
        return clics;
    }

    public void resetClics() {
        clics = 0;
    }

    // Méthode pour réinitialiser la case
    public void reset() {
        this.type = CaseType.VIDE;
        this.ressource = null;
        this.machine = null;
        this.clics = 0;
    }

    @Override
    public String toString() {
        switch (type) {
            case VIDE: return "V";
            case DEMARRAGE: return "D";
            case RESSOURCE: return ressource != null ? "R" : "V";
            case MACHINE: return machine != null ? "M" : "V";
            case MARCHE: return "S";
            default: return " ";
        }
    }
}
