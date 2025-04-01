package Model.GameStruct;

import GUI.MenuPrincipal;

public class Run {

    public static void lancerPartie(){
        MenuPrincipal menuPrincipal = new MenuPrincipal();

        if (menuPrincipal.isGameStarted()){
            GameLoop gameLoop = new GameLoop();
            gameLoop.start();
        }else {
            System.out.println("Le jeu n'a pas été démarré.");
        }
    }
    public static void main(String[] args) {
        lancerPartie();
    }
}
