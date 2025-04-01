package Model;

import Model.Machines.Usine;
import geometry.Coordinates;

public interface Actionnable {
    abstract void deplacer(Coordinates pos);
    abstract void ajouterRessource(Ressources ressource, int quantite);
    abstract void retirerArgent(int montant);
    abstract void ajouterArgent(int montant);
    abstract boolean placerMachine(Machine machine, Coordinates position, MapG carte);
    abstract boolean transfererRessourcesVersUsine(Usine usine, Recette recette);
    abstract void recolteLaboriuse(Coordinates positionCible, MapG carte);
    abstract void checkIfOnMarket(MapG map, Partie partie);
    abstract void recolteTemporisee(Coordinates positionCible, MapG carte);
}
