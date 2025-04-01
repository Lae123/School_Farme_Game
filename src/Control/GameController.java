package Control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import GUI.Inv.InventaireJoueur;
import GUI.Inv.InventaireRecolteuse;
import GUI.Inv.InventaireUsine;
import Helper.GameUtils;
import Helper.LoadSound;
import Model.Animal;
import Model.Case;
import Model.CaseType;
import Model.Joueur;
import Model.Machine;
import Model.MapG;
import Model.Partie;
import Model.Machines.Recolteuse;
import Model.Machines.Usine;
import geometry.Coordinates;

public class GameController implements Serializable{
    
    private final Joueur player;
    private final MapG gameMap;
    private final Partie partie;
    private Timer animalMovementTimer;

    public GameController(Partie partie, MapG gameMap) {
        this.partie = partie;
        this.player = partie.getJoueur();
        this.gameMap = gameMap;
        // Initialisation du timer qui déplace les animaux toutes les 100ms
        animalMovementTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveAnimals();
            }
        });
        animalMovementTimer.start(); // Démarre le timer
    }

    public Partie getPartie() {
        return partie;
    }

    public MapG getMap(){
        return this.gameMap;
    }
    // Méthode pour déplacer le joueur
    public boolean movePlayer(Coordinates newPos) {
        if (gameMap.isValidMove(newPos)) {
            player.getCpJoueur().deplacer(newPos);
            player.getCpJoueur().checkIfOnMarket(gameMap, partie);
            return true;
        } 
        return false;

    }

    public Joueur getPlayer() {
        return player;
    }

    // Méthode pour mettre à jour l'état du jeu
    public void update(double delta) {
        //this.partie.update(delta);
        this.partie.update(delta);
    }

    public MapG getGameMap() {
        return gameMap;
    }


    private void moveAnimals() {
        for (Animal animal : partie.getAnimals()) {
            animal.randomMove(gameMap.getLargeur(), gameMap.getHauteur());  // Déplacement aléatoire progressif
        }
    }

    public void addKeyListenerToPanel(JPanel panel) {
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                Coordinates currentPos = player.getPos();
                Coordinates newPos = null;
    
                switch (keyCode) {
                    case KeyEvent.VK_UP:
    
                        newPos = new Coordinates(currentPos.x() - 1, currentPos.y()); // Haut -> 
                        player.setDirection("UP");
                        break;
                    case KeyEvent.VK_DOWN:
    
                        newPos = new Coordinates(currentPos.x() + 1, currentPos.y()); // Bas -> 
                        player.setDirection("DOWN");
                        break;
                    case KeyEvent.VK_LEFT:
                
                        newPos = new Coordinates(currentPos.x() , currentPos.y() - 1); // Gauche -> 
                        player.setDirection("LEFT");
                        break;
                    case KeyEvent.VK_RIGHT:

                        newPos = new Coordinates(currentPos.x(), currentPos.y() + 1); // Droite -> 
                        player.setDirection("RIGHT");
                        break;

                    case KeyEvent.VK_I : 
                        InventaireJoueur screen = new InventaireJoueur(player);
                        LoadSound.playSound("button_click");
                        screen.setVisible(true);
                    break;

                }
    
                // Si la nouvelle position est valide, déplacer le joueur
                if (newPos != null && gameMap.isValidMove(newPos)) {
                    movePlayer(newPos); // Déplace le joueur si le mouvement est valide
                } else {
                    // Si la position est invalide, afficher un message de débogage
                    System.out.println("Déplacement non valide.!");
                }
            }
        });
    }

    public void addMouseListenerToPanel(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    // Récupération des coordonnées du clic dans le panneau
                    int x = e.getX();
                    int y = e.getY();
                    
                    // Calcul de la taille de la tuile
                    int tileSize = panel.getWidth() / gameMap.getLargeur();
                    if (tileSize <= 0) return;
    
                    // Calcul des coordonnées de la case sur la carte
                    int row = y / tileSize;
                    int col = x / tileSize;
    
                    Coordinates clickedPos = new Coordinates(row, col);
    
                    // Vérification que le mouvement est valide
                    if (gameMap.isValidMove(clickedPos)) {
                        // Gestion des actions selon le bouton de la souris
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            LoadSound.playSound("button_click");
                            handleRightClick(clickedPos); // Traitement du clic droit
                        } else {
                            LoadSound.playSound("button_click");
                            handleLeftClick(clickedPos, panel); // Traitement du clic gauche
                        }
                    }
    
                    // Traitement de la case où le joueur a cliqué
                    handleCaseActions(clickedPos);
    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("Erreur lors du clic sur le panneau : " + ex.getMessage());
                }
            }
        });
    }
    
    private void handleRightClick(Coordinates clickedPos) {
        // Mise à jour de la direction du joueur et mouvement pas à pas
        updatePlayerDirection(clickedPos);
        movePlayerStepByStep(clickedPos);
    }
    
    private void handleLeftClick(Coordinates clickedPos, JPanel panel) {
        // Choisir le type de machine à placer
        Machine machine = GameUtils.chooseMachineType(panel, clickedPos,gameMap, player);
        if (machine != null) {
            // Essayer de placer la machine à la position cliquée
            boolean placed = player.getCpJoueur().placerMachine(machine, clickedPos, gameMap);
            if (!placed) {
                System.out.println("Placement de la machine échoué.");
            }
        }
    }
    
    private void handleCaseActions(Coordinates clickedPos) {
        // Récupérer la case à la position cliquée
        Case place = gameMap.getCaseAt(clickedPos);
        CaseType placeCaseType = place.getType();
    
        if (place != null) {
            // Vérifier si une machine est présente sur la case
            handleMachineAtCase(place);
    
            // Vérifier si la case contient une ressource
            if (placeCaseType == CaseType.RESSOURCE) {
                player.getCpJoueur().recolteLaboriuse(clickedPos, gameMap);
            }
    
            // Vérifier si la case contient une machine pour gestion des réglages
            if (placeCaseType == CaseType.MACHINE) {
                Machine machine = place.getMachine();
                if (machine != null) {
                    machine.reglage(gameMap);
                } else {
                    System.out.println("Aucune machine associée à cette case !");
                }
            }
        }
    }
    
    private void handleMachineAtCase(Case place) {
        // Vérifier et afficher l'inventaire de la machine selon son type
        Machine machinePlacee = place.getMachine();
        if (machinePlacee != null) {
            if (machinePlacee instanceof Recolteuse) {
                LoadSound.playSound("button_click");
                new InventaireRecolteuse((Recolteuse) machinePlacee, player).setVisible(true);
            } else if (machinePlacee instanceof Usine) {
                LoadSound.playSound("button_click");
                new InventaireUsine((Usine) machinePlacee, player).setVisible(true);
            } else {
                System.out.println("Machine inconnue.");
            }
        }
    }
    
    
    
    // Méthode pour mettre à jour la direction du joueur selon la position cliquée
    private void updatePlayerDirection(Coordinates targetPos) {
        Coordinates playerPos = player.getPos();
        double deltaX = targetPos.y() - playerPos.y();
        double deltaY = targetPos.x() - playerPos.x();
    
        // Déterminer la direction basée sur la différence des coordonnées
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            // Si la différence en X est plus grande, on déplace le joueur horizontalement
            if (deltaX > 0) {
                player.setDirection("RIGHT");
            } else {
                player.setDirection("LEFT");
            }
        } else {
            // Si la différence en Y est plus grande, on déplace le joueur verticalement
            if (deltaY > 0) {
                player.setDirection("DOWN");
            } else {
                player.setDirection("UP");
            }
        }
    }
    
    
    public void movePlayerStepByStep(Coordinates targetPos) {
        Coordinates startPos = player.getPos();

        // Trouver le chemin entre la position actuelle et la cible
        List<Coordinates> path = gameMap.findPath(startPos, targetPos);
        
        // Si aucun chemin n'est trouvé, arrêtez le mouvement
        if (path == null || path.isEmpty()) {
            System.out.println("Aucun chemin trouvé vers la cible." + path);
            return;
        }

        // Le nombre de pas à faire et le délai entre chaque pas
        int steps = 50;
        int delay = 30;

        // Commencer le mouvement
        Timer timer = new Timer(delay, new ActionListener() {
            int currentStep = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < steps && currentStep < path.size() - 1) {
                    Coordinates currentPos = path.get(currentStep); // Position actuelle sur le chemin
                    Coordinates nextPos = path.get(currentStep + 1); // Position suivante sur le chemin
                    
                    double progress = (double) currentStep / steps;

                    // Calculer la position intermédiaire entre la position actuelle et la suivante
                    double newX = currentPos.x() + (nextPos.x() - currentPos.x()) * progress;
                    double newY = currentPos.y() + (nextPos.y() - currentPos.y()) * progress;

                    // Vérification que la position intermédiaire est valide avant de déplacer
                    Coordinates intermediatePos = new Coordinates((int) newX, (int) newY);
                    if (gameMap.isValidMove(intermediatePos)) {
                        // Mettre à jour la position du joueur
                        player.setPixelPos(new Coordinates(newX, newY), newX * 50, newY * 50);
                        currentStep++;
                    } else {
                        // Si une case intermédiaire n'est pas valide, arrêtez le déplacement
                        ((Timer)e.getSource()).stop();
                        System.out.println("Déplacement interrompu : obstacle détecté.");
                    }
                } else {
                    // Une fois que toutes les étapes sont complétées, finir le mouvement
                    movePlayer(path.get(path.size() - 1)); // Se déplacer vers la dernière position du chemin
                    ((Timer)e.getSource()).stop(); // Arrêter le timer lorsque le mouvement est complet
                }
            }
        });

        timer.start();
    }

    
}
