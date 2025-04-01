package Model;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Control.GameController;
import GUI.ShopScreen;
import Helper.Animation;
import Helper.Load;

public class Partie implements Serializable {
    private static final long serialVersionUID = 1L;
    private MapG carte;
    private Joueur joueur;
    private GameController gc;
    private LivreDeRecette livreDeRecette;
    private ShopScreen shopScreen;
    private final List<Animal> animals;
    private transient Scanner scanner = new Scanner(System.in);

    // Créer une partie
    public Partie (){
        createMap();
        creerJoueur();
        this.livreDeRecette = new LivreDeRecette();
        creerLivreDeRecette();
        this.gc = new GameController(this, carte);
        this.animals = new ArrayList<>(); // Initialisation
        this.animals.addAll(createRandomAnimals(5)); // Ajout des animaux générés
    }

    public ShopScreen getShopScreen() {
        return shopScreen;
    }

    public void setShopScreen(ShopScreen shopScreen) {
        this.shopScreen = shopScreen;
    }

    public MapG getCarte() {
        return carte;
    }

    public GameController getGc() {
        return gc;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public LivreDeRecette getLivreDeRecette() {
        return livreDeRecette;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public void createMap(){
        this.carte = new MapG();
        this.carte.affichageTerminal();
    }

    public void update(double delta) {
    }

    public void creerLivreDeRecette(){
        // Recette 1 : Outils en bois
        Map<TypeRessource, Integer> ingredientsOutilsBois = new HashMap<>();
        ingredientsOutilsBois.put(TypeRessource.BOIS, 5);
        Recette outilsBois = new Recette("Outils en bois", ingredientsOutilsBois, TypeRessource.BOIS, 1, 0 , 15);
        livreDeRecette.ajouterRecette(outilsBois);

        // Recette 2 : Pierre taillée
        Map<TypeRessource, Integer> ingredientsPierreTaillee = new HashMap<>();
        ingredientsPierreTaillee.put(TypeRessource.PIERRE, 10);
        Recette pierreTaillee = new Recette("Pierre taillée", ingredientsPierreTaillee, TypeRessource.PIERRE, 2, 5000, 30); // 5 secondes
        livreDeRecette.ajouterRecette(pierreTaillee);

        // Recette 3 : Lingot de fer
        Map<TypeRessource, Integer> ingredientsLingotFer = new HashMap<>();
        ingredientsLingotFer.put(TypeRessource.FER, 3);
        Recette lingotFer = new Recette("Lingot de fer", ingredientsLingotFer, TypeRessource.FER, 1, 10000, 50); // 10 secondes
        livreDeRecette.ajouterRecette(lingotFer);

        // Recette 4 : Bijou en or
        Map<TypeRessource, Integer> ingredientsBijouOr = new HashMap<>();
        ingredientsBijouOr.put(TypeRessource.OR, 2);
        ingredientsBijouOr.put(TypeRessource.FER, 1);
        Recette bijouOr = new Recette("Bijou en or", ingredientsBijouOr, TypeRessource.OR, 1, 15000, 150); // 15 secondes
        livreDeRecette.ajouterRecette(bijouOr);

        Map<TypeRessource, Integer> ingredientsEpeeFer = new HashMap<>();
        ingredientsEpeeFer.put(TypeRessource.FER, 5);
        Recette epeeFer = new Recette("Épée en fer", ingredientsEpeeFer, TypeRessource.FER, 1, 20000, 120);  // 20 secondes
        livreDeRecette.ajouterRecette(epeeFer);

        // Recette 6 : Hache en pierre
        Map<TypeRessource, Integer> ingredientsHachePierre = new HashMap<>();
        ingredientsHachePierre.put(TypeRessource.PIERRE, 8);
        ingredientsHachePierre.put(TypeRessource.BOIS, 3);
        Recette hachePierre = new Recette("Hache en pierre", ingredientsHachePierre, TypeRessource.PIERRE, 1, 15000, 60);  // 15 secondes
        livreDeRecette.ajouterRecette(hachePierre);

        // Recette 7 : Casque en fer
        Map<TypeRessource, Integer> ingredientsCasqueFer = new HashMap<>();
        ingredientsCasqueFer.put(TypeRessource.FER, 6);
        ingredientsCasqueFer.put(TypeRessource.OR, 1);
        Recette casqueFer = new Recette("Casque en fer", ingredientsCasqueFer, TypeRessource.FER, 1, 25000,50);  // 25 secondes
        livreDeRecette.ajouterRecette(casqueFer);
    }

    public List<Animal> createRandomAnimals(int numberOfAnimals) {
        // Liste des types d'animaux disponibles
        String[] animalTypes = {"pig", "chicken", "cow", "sheep"};
        
        // Vérifier que la carte de jeu est valide
        if (gc == null || gc.getGameMap() == null) {
            throw new IllegalStateException("La carte de jeu (GameMap) n'est pas initialisée.");
        }
    
        // Générer des animaux aléatoirement
        for (int i = 0; i < numberOfAnimals; i++) {
            // Sélectionner un type d'animal aléatoire
            String randomAnimalType = animalTypes[(int) (Math.random() * animalTypes.length)];
    
            // Charger le tileset spécifique à l'animal
            BufferedImage tileSet = Load.loadSprite(randomAnimalType);
    
            // Charger les animations spécifiques de l'animal
            Map<String, Animation> animations = Load.loadAnimalAnimations(randomAnimalType, tileSet, 64, 64, 2);
    
            // Générer une position aléatoire
            int x = (int) (Math.random() * gc.getGameMap().getLargeur());
            int y = (int) (Math.random() * gc.getGameMap().getHauteur());
    
            // Associer l'animation par défaut (par exemple "DOWN")
            Animation defaultAnimation = animations.getOrDefault("DOWN", null);
    
            // Vérifiez si l'animation par défaut est présente, sinon affichez une erreur
            if (defaultAnimation != null) {
                Animal animal = new Animal(x, y, 50, 50, 2, defaultAnimation);
    
                // Définir les animations de l'animal (comme pour le joueur)
                animal.setAnimations(animations);  // Ajoutez cette ligne pour affecter toutes les animations possibles à l'animal
    
                animals.add(animal);
            } else {
                System.err.println("Erreur : Animation par défaut introuvable pour l'animal de type " + randomAnimalType);
            }
        }
    
        return animals;
    }
    

    public void creerJoueur() {
        this.joueur = new Joueur();
        this.joueur.setPos(this.carte.getStartC()); // Définit la position du joueur
        this.joueur.setArgent(new Monnaie(100));
        this.joueur.setAnimations(Load.loadPlayerAnimations(
            Load.loadSprite("player"),  // Provide the tileSet
            50, 0 // Assuming tile size is 64 // Assuming there are 6 frames per row
        ));

        // Création d'un champ de texte avec une couleur de fond et une police mignonne
        JTextField textField = new JTextField();
        textField.setBackground(new java.awt.Color(255, 228, 255));  // Fond rose pastel
        textField.setFont(new Font("Comic Sans MS", Font.BOLD, 18));  // Police mignonne
        textField.setPreferredSize(new Dimension(200, 30));  // Taille personnalisée

        // Panneau personnalisé pour ajouter des bordures arrondies
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new java.awt.Color(255, 240, 245));  // Fond crème

        // Titre mignon
        JLabel label = new JLabel("Quel est votre pseudo ?", SwingConstants.CENTER);
        label.setFont(new Font("Comic Sans MS", Font.BOLD, 20));  // Police mignonne et claire
        label.setForeground(new java.awt.Color(255, 105, 10)); 
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ajout du champ de texte au panneau
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        // Personnalisation des boutons
        Object[] options = {"Ok", "Annuler"};
        
        // Affichage du pop-up avec des boutons personnalisés
        int option = JOptionPane.showOptionDialog(
            null, // Centrer la fenêtre
            panel, // Le panneau personnalisé contenant le champ de texte
            "Bienvenue !", // Titre de la fenêtre
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.PLAIN_MESSAGE,
            null, // Pas d'icône par défaut
            options, // Liste des options de boutons
            options[0] // Bouton par défaut ("Ok")
        );
        // Vérifie si l'utilisateur a appuyé sur "Ok"
        if (option == JOptionPane.YES_OPTION) {
            String prenom = textField.getText(); // Récupère le texte saisi
            if (!prenom.trim().isEmpty()) {
                this.joueur.setNom(prenom); // Définit le nom du joueur
            } else {
                JOptionPane.showMessageDialog(null, "Vous n'aurez pas de nom! Tant pis On vous attribura un par defaut", "Erreur", JOptionPane.ERROR_MESSAGE);
                this.joueur.setNom("Je n'ai pas de nom");
            }
        }
    }
}
