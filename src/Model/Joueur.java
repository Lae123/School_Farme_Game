package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Helper.Animation;
import geometry.Coordinates;

public class Joueur  implements Serializable{
    private static final long serialVersionUID = 1L;
    // Attributs du joueur
    private Coordinates pos;
    private Ressources recolte; // Type de ressource en cours de collecte
    private Monnaie argent; // Classe pour représenter l'argent
    private Inventaire inv; // Inventaire du joueur
    private String nom;
    private double posX, posY; // Positions en pixels
    private String direction;
    private Map<String, Animation> animations;
    private LivreDeRecette livreDeRecette;
    private ComportementJoueur cpJoueur;
    private List<Machine> machines;
    private boolean enMouvement;

    // Constructeur par défaut
    public Joueur(String nom, Coordinates positionInitiale,Map<String, Animation> animations) {
        this.nom = nom;
        this.livreDeRecette = new LivreDeRecette();
        this.pos = positionInitiale;
        this.recolte = new Ressources(); // Initialiser avec aucune ressource au début
        this.argent = new Monnaie(0); // Initialiser avec 0 argent
        this.inv = new Inventaire(100); // Créer un inventaire vide -> 100 elements d office pour le joueur payer pour avoir plus d espace
        this.direction = "UP"; 
        this.animations = animations;  
        this.cpJoueur = new ComportementJoueur(this);
        this.enMouvement = true;
        this.machines = new ArrayList<>();
    }

    public LivreDeRecette getLivreDeRecette() {
        return livreDeRecette;
    }

    public void setLivreDeRecette(LivreDeRecette livreDeRecette) {
        this.livreDeRecette = livreDeRecette;
    }

    public Joueur (){
        this.nom = null;
        this.pos = null;
        this.livreDeRecette = new LivreDeRecette();
        this.recolte = new Ressources(); // Initialiser avec aucune ressource au début
        this.argent = new Monnaie(0); // Initialiser avec 0 argent
        this.inv = new Inventaire(100); // Créer un inventaire vide
        this.direction = "UP";
        this.cpJoueur = new ComportementJoueur(this);
        this.enMouvement = true;
        this.machines = new ArrayList<>();
    }

    public String getDirection() {
        return direction;
    }

    
    

    public void ajouterMachine(Machine machine) {
        if (machine != null) {
            machines.add(machine);  // Ajoute la machine à l'inventaire du joueur
        } else {
            System.out.println("Erreur : La machine ne peut pas être nulle.");
        }
    }
    
    public boolean possedeMachine(Machine machine) {
        if (machine == null) {
            System.out.println("test");
            return false;  // Retourne false si la machine est nulle
        }else if(machines.contains(machine)){
            System.out.println("oui cest moiiiiii");
        }else{
            System.out.println("Je n'existe pas");
        }
        return machines.contains(machine);  // Vérifie si la machine est dans l'inventaire du joueur
    }
    
    
    public List<Machine> getMachines() {
        return machines;
    }

    public ComportementJoueur getCpJoueur() {
        return cpJoueur;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    // Getters et setters
    public Coordinates getPos() {
        return pos;
    }

    public void setPos(Coordinates pos) {
        this.pos = pos;
    }

    public Ressources getRecolte() {
        return recolte;
    }

    public void setRecolte(Ressources recolte) {
        this.recolte = recolte;
    }

    public Monnaie getArgent() {
        return argent;
    }

    public boolean getMouvement(){
        return this.enMouvement;
    }

    public double getPosX() {
        return posX;
    }
    
    public double getPosY() {
        return posY;
    }

    public Map<String, Animation> getAnimations() {
        return animations;
    }

    public void setAnimations(Map<String, Animation> animations) {
        this.animations = animations;
    }

    public void setPixelPos(Coordinates pos, double posX, double posY) {
        this.pos = pos;
        this.posX = pos.y(); // Assurez-vous de convertir avec `tileSize` dans le rendu
        this.posY = pos.x();
    }
    
    public void setPos(double x, double y) {
        this.posX = x;
        this.posY = y;
    }

    public void setMouvement(boolean mv){
        this.enMouvement = mv;
    }
    
    public void setGridPos(Coordinates gridPos) {
        this.pos = gridPos; // Met à jour la position dans la grille
        this.posX = gridPos.y() * 50; // 50 correspond à `tileSize`
        this.posY = gridPos.x() * 50;
    }
    

    public void setArgent(Monnaie argent) {
        this.argent = argent;
    }

    public Inventaire getInv() {
        return inv;
    }

    public void setInv(Inventaire inv) {
        this.inv = inv;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Joueur{" +
                "nom ='" + nom + '\'' +
                ", position =" + pos +
                ", argent =" + argent +
                ", inventaire =" + inv +
                '}';
    }
}
