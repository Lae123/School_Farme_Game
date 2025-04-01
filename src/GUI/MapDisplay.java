package GUI;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;

import Control.GameController;
import Helper.Animation;
import Helper.Load;
import Model.Animal;
import Model.Case;
import Model.CaseType;
import Model.MapG;
import Model.Machines.Recolteuse;
import geometry.Coordinates;

public class MapDisplay extends JPanel {
    private int tileSize = 50; // Taille des cases
    private GameController gameController;

    // Liste des images pour les cases vides
    private BufferedImage[] emptyTileImages;

    // Tableau pour mémoriser l'image de chaque case vide
    private BufferedImage[][] cachedEmptyImages;

    public MapDisplay(GameController gameController) {
        this.gameController = gameController;
        gameController.addKeyListenerToPanel(this);
        gameController.addMouseListenerToPanel(this);

        // Charger les images pour les cases vides
        emptyTileImages = new BufferedImage[4]; 
        emptyTileImages[0] = Load.loadSprite("sol1");
        emptyTileImages[1] = Load.loadSprite("sol2");
        emptyTileImages[2] = Load.loadSprite("sol3");
        emptyTileImages[3] = Load.loadSprite("sol4");

        // Initialiser le cache des images pour chaque case vide
        cachedEmptyImages = new BufferedImage[gameController.getGameMap().getHauteur()][gameController.getGameMap().getLargeur()];
    }

    // Redéfinir la taille du composant pour qu'il s'ajuste à la taille de la fenêtre
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(gameController.getGameMap().getLargeur() * tileSize, gameController.getGameMap().getHauteur() * tileSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMap(g);
        drawPlayer(g);
        drawFactories(g);
        drawAnimals(g);
        repaint();
    }

    private void drawMap(Graphics g) {
        Random rand = new Random();
        for (int i = 0; i < gameController.getGameMap().getHauteur(); i++) {
            for (int j = 0; j < gameController.getGameMap().getLargeur(); j++) {
                BufferedImage tileImage;
                switch (gameController.getGameMap().getMap()[i][j].getType()) {
                    case VIDE:
                        // Si l'image de cette case n'a pas encore été définie, on en choisit une aléatoirement
                        if (cachedEmptyImages[i][j] == null) {
                            cachedEmptyImages[i][j] = emptyTileImages[rand.nextInt(emptyTileImages.length)];
                        }
                        tileImage = cachedEmptyImages[i][j];
                        break;
                    case RESSOURCE:
                        tileImage = Load.loadSprite("farm");
                        break;
                    default:
                        tileImage = Load.loadSprite("sol1");
                        break;
                }
                // Dessiner l'image de la tuile
                g.drawImage(tileImage, j * tileSize, i * tileSize, tileSize, tileSize, null);
            }
        }
    }

    private void drawFactories(Graphics g) {
        MapG gameMap = gameController.getGameMap();
        int mapHeight = gameMap.getHauteur();
        int mapWidth = gameMap.getLargeur();

        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                Case currentCase = gameMap.getMap()[i][j];
                BufferedImage sprite = null;

                // Vérifie le type de la case et charge l'image correspondante
                if (currentCase.getType() == CaseType.MACHINE) {
                    if (currentCase.getMachine() instanceof Recolteuse) {
                        sprite = Load.loadSprite("recolteuse");
                    } else {
                        sprite = Load.loadSprite("usine");
                    }
                } else if (currentCase.getType() == CaseType.MARCHE) {
                    sprite = Load.loadSprite("marche");
                } else if (currentCase.getType() == CaseType.DEMARRAGE) {
                    sprite = Load.loadSprite("demarrage");
                }

                // Si une image a été chargée, dessine-la
                if (sprite != null) {
                    g.drawImage(sprite, j * tileSize, i * tileSize, tileSize, tileSize, null);
                }
            }
        }
    }


    private void drawPlayer(Graphics g) {
        Coordinates playerPos = gameController.getPlayer().getPos();
        if (playerPos != null) {
            String direction = gameController.getPlayer().getDirection();
            Animation playerAnimation = gameController.getPlayer().getAnimations().get(direction);
            BufferedImage playerImage = playerAnimation.getCurrentFrame();
            BufferedImage resizedPlayerImage = Load.resizeImage(playerImage, tileSize, tileSize); 
    
            playerAnimation.update();
    
            int playerX = (int) playerPos.y() * tileSize;
            int playerY = (int) playerPos.x() * tileSize;
    
            g.drawImage(resizedPlayerImage, playerX, playerY, tileSize, tileSize, null);

        } else {
            System.out.println("Position du joueur est null.");
        }
    }

    private void drawAnimals(Graphics g) {
        // Supposons que vous avez une liste des animaux dans gameController
        for (Animal animal : gameController.getPartie().getAnimals()) {
            // Obtenez la position de l'animal (en fonction de ses coordonnées sur la carte)
            int animalX = animal.getX() * tileSize;
            int animalY = animal.getY() * tileSize;

            // Obtenez l'animation actuelle de l'animal
            Animation animalAnimation = animal.getCurrentAnimation(); // Récupère l'animation actuelle de l'animal
            BufferedImage animalImage = animalAnimation.getCurrentFrame();

            // Redimensionner l'image de l'animal (si nécessaire)
            BufferedImage resizedAnimalImage = Load.resizeImage(animalImage, tileSize, tileSize);

            // Met à jour l'animation de l'animal
            animalAnimation.update();

            // Dessinez l'animal à la position correcte
            g.drawImage(resizedAnimalImage, animalX, animalY, tileSize, tileSize, null);
        }
    }


    public GameController getGameController() {
        return gameController;
    }
}
