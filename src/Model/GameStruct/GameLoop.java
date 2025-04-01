package Model.GameStruct;

import Control.GameController;
import GUI.MapDisplay;
import Helper.GameUtils;
import Model.Partie;

public class GameLoop {
    private boolean running;
    private int fps;
    private Partie partie; // État du jeu (carte, joueur, etc.)
    private GameController gameController; // Contrôle du jeu
    //private GameRenderer gameRenderer;
    private MapDisplay mainGamePanel; // Panel pour l'affichage

    public GameLoop() {
        this.running = false;
        this.fps = 30;
    }

    public void start() {
        System.out.println("Démarrage de la boucle de jeu...");
        running = true;

        // Initialiser le jeu
        init();

        // Boucle principale
        gameLoop();
    }

    private void init() {
        System.out.println("Initialisation des ressources...");
        // Créer une partie et son contrôleur
        this.partie = new Partie();
        gameController = new GameController(this.partie, partie.getCarte());

        // Initialiser le panneau principal d'affichage
        mainGamePanel = new MapDisplay(gameController);
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        double nsPerFrame = 1_000_000_000.0 / fps;

        while (running) {
            long now = System.nanoTime();
            double delta = (now - lastTime) / nsPerFrame;
            lastTime = now;

            // Mise à jour logique
            update(delta);


            // Limiter la fréquence d'images
            try {
                Thread.sleep(1000 / fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Condition de fin
            if (shouldStop()) {
                stop();
            }
        }
    }

    private void update(double delta) {
        // Appel à gameController pour mettre à jour la logique du jeu avec delta
        gameController.update(delta);  // Mettre à jour la logique via le contrôleur
    }

    private void stop() {
        GameUtils.saveGame(partie);
        System.out.println("Arrêt de la boucle de jeu...");
        running = false;
    }

    private boolean shouldStop() {
        return false; // Condition d'arrêt à implémenter selon les règles du jeu
    }

    public MapDisplay getMainGamePanel() {
        return mainGamePanel;
    }
}
