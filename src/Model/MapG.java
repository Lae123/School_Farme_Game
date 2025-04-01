package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import geometry.Coordinates;

public class MapG implements Serializable {
    private final Random rand = new Random();
    private final Case[][] map;
    private final int largeur = 20;  // Largeur (colonnes)
    private final int hauteur = 10;  // Hauteur (lignes)
    private Coordinates startC;
    private Coordinates marketC;
    private final int nbRessources = rand.nextInt(3) + 4;

    public MapG() {
        this.map = new Case[this.hauteur][this.largeur];  // Initialisation de la carte
        remplirMap();
        affichageTerminal();
    }

    // Remplir la carte
    private void remplirMap() {
        for (int i = 0; i < this.hauteur; i++) {
            for (int j = 0; j < this.largeur; j++) {
                this.map[i][j] = new Case(CaseType.VIDE);  // Initialisation des cases
            }
        }
        placerCaseStart();
        placerCaseMarche();
        placerRessources(this.nbRessources);

        if (!verifierAccessibilite()) {
            remplirMap(); // Recommencer la génération si la carte n'est pas valide
        }
    }

    // Placer la case de démarrage
    private void placerCaseStart() {
        startC = new Coordinates(rand.nextInt(hauteur), largeur - 1);
        map[(int)startC.x()][(int)startC.y()].setType(CaseType.DEMARRAGE);
    }

    // Placer les cases du marché
    private void placerCaseMarche() {
        int bord = rand.nextInt(4);
        switch (bord) {
            case 0 -> marketC = new Coordinates(0, rand.nextInt(largeur));
            case 1 -> marketC = new Coordinates(hauteur - 1, rand.nextInt(largeur));
            case 2 -> marketC = new Coordinates(rand.nextInt(hauteur), 0);
            case 3 -> marketC = new Coordinates(rand.nextInt(hauteur), largeur - 1);
            default -> throw new IllegalStateException("Valeur inattendue pour le bord : " + bord);
        }
        map[(int)marketC.x()][(int)marketC.y()].setType(CaseType.MARCHE);
    }

    // Placer les ressources
    private void placerRessources(int nbRessources) {
        int ressourcesPlacees = 0;
        while (ressourcesPlacees < nbRessources) {
            int x = rand.nextInt(hauteur);
            int y = rand.nextInt(largeur);

            Case currentCase = map[x][y];
            if (currentCase.getType() == CaseType.VIDE) {
                currentCase.setType(CaseType.RESSOURCE);

                // Choisir un type de ressource au hasard, mais sans inclure NONE
                TypeRessource[] typesDisponibles = TypeRessource.values();
                TypeRessource typeRessource;
                do {
                    typeRessource = typesDisponibles[rand.nextInt(typesDisponibles.length)];
                } while (typeRessource == TypeRessource.NONE); // Réessayer si la ressource est de type NONE

                // Créer une nouvelle ressource avec un type valide
                Ressources res = new Ressources(typeRessource, rand.nextInt(5) + 3);
                currentCase.setRessource(res);

                ressourcesPlacees++;
            }
        }
    }


    // Vérification d'accessibilité
    private boolean verifierAccessibilite() {
        boolean[][] visite = new boolean[hauteur][largeur];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{(int)startC.x(), (int)startC.y()});
        visite[(int)startC.x()][(int)startC.y()] = true;

        boolean marcheAtteint = false;
        int ressourcesAtteintes = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];

            // Directions de déplacement (haut, bas, gauche, droite)
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] dir : directions) {
                int nx = x + dir[0], ny = y + dir[1];
                if (nx >= 0 && nx < hauteur && ny >= 0 && ny < largeur && !visite[nx][ny]) {
                    visite[nx][ny] = true;
                    queue.add(new int[]{nx, ny});

                    CaseType caseType = map[nx][ny].getType();
                    if (caseType == CaseType.MARCHE) {
                        marcheAtteint = true;
                    } else if (caseType == CaseType.RESSOURCE) {
                        ressourcesAtteintes++;
                    }
                }
            }
        }
        return marcheAtteint && ressourcesAtteintes == nbRessources;
    }

    // Vérifier si une case est adjacente à une ressource
    public boolean estAdjacenteARessource(Coordinates position) {
        int x = (int)position.x(), y = (int)position.y();

        // Vérifier les cases adjacentes
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int nx = x + dir[0], ny = y + dir[1];
            if (nx >= 0 && nx < hauteur && ny >= 0 && ny < largeur) {
                if (map[nx][ny].getType() == CaseType.RESSOURCE) {
                    return true;
                }
            }
        }
        return false;
    }

    // Vérifier si une position est valide
    public boolean isValidMove(Coordinates newPos) {
        int x = (int)newPos.x(), y = (int)newPos.y();
        return x >= 0 && x < hauteur && y >= 0 && y < largeur && (map[x][y].getType() == CaseType.VIDE || map[x][y].getType() == CaseType.MARCHE);
    }

    public Case getCaseAt(Coordinates position) {
        int x = (int)position.x();
        int y = (int)position.y();
    
        // Vérification des limites avant l'accès au tableau
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            System.out.println("Coordonnées hors limites : (" + x + ", " + y + ")");
            return null; // Retourner null si les coordonnées sont hors limites
        }
    
        return map[x][y]; // Retourner la case demandée
    }
    

    public void setCaseAt(Coordinates pos, Case a) {
        map[(int)pos.x()][(int)pos.y()] = a;
    }

    public void affichageTerminal() {
        for (int i = 0; i < hauteur; i++) {
            for (int j = 0; j < largeur; j++) {
                System.out.print(map[i][j].toString() + " ");
            }
            System.out.println();
        }
    }

    public Coordinates getStartC() {
        return startC;
    }

    public List<Ressources> getRessourcesAdjacentes(Coordinates position) {
        List<Ressources> ressourcesAdjacentes = new ArrayList<>();
    
        if (position == null) {
            System.out.println("Erreur : La position est nulle.");
            return ressourcesAdjacentes;
        }
    
        // Liste des directions adjacentes (haut, bas, gauche, droite et diagonales)
        int[][] directions = {
            {0, 1},    // Haut
            {0, -1},   // Bas
            {-1, 0},   // Gauche
            {1, 0},    // Droite
            {-1, -1},  // Diagonale haut-gauche
            {-1, 1},   // Diagonale haut-droite
            {1, -1},   // Diagonale bas-gauche
            {1, 1}     // Diagonale bas-droite
        };
    
        for (int[] direction : directions) {
            // Calcul de la position adjacente
            Coordinates adjacentPos = new Coordinates(
                position.x() + direction[0],
                position.y() + direction[1]
            );
    
            // Récupérer la case adjacente
            Case adjacentCase = getCaseAt(adjacentPos);
    
            // Vérifier si la case contient une ressource
            if (adjacentCase != null && adjacentCase.getType() == CaseType.RESSOURCE) {
                Ressources ressource = adjacentCase.getRessource();
                if (ressource != null) {
                    ressourcesAdjacentes.add(ressource);
                }
            }
        }
    
        return ressourcesAdjacentes;
    }

    public List<Coordinates> findPath(Coordinates start, Coordinates end) {
        System.out.println("Recherche de chemin de " + start + " vers " + end);
        
        boolean[][] visited = new boolean[hauteur][largeur];
        Queue<List<Coordinates>> queue = new LinkedList<>();
        
        List<Coordinates> startPath = new ArrayList<>();
        startPath.add(start);
        queue.add(startPath);
        visited[(int)start.x()][(int)start.y()] = true;
        
        int targetX = (int)end.x();
        int targetY = (int)end.y();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    
        while (!queue.isEmpty()) {
            List<Coordinates> path = queue.poll();
            Coordinates current = path.get(path.size() - 1);
            
            // Vérification avec les coordonnées entières
            if ((int)current.x() == targetX && (int)current.y() == targetY) {
                System.out.println("Chemin trouvé !");
                return path;
            }
            
            for (int[] dir : directions) {
                int nx = (int)current.x() + dir[0];
                int ny = (int)current.y() + dir[1];
                
                if (nx >= 0 && nx < hauteur && ny >= 0 && ny < largeur && !visited[nx][ny]) {
                    Case currentCase = map[nx][ny];
                    if ((nx == targetX && ny == targetY) || 
                        currentCase.getType() == CaseType.VIDE || 
                        currentCase.getType() == CaseType.RESSOURCE) {
                        visited[nx][ny] = true;
                        List<Coordinates> newPath = new ArrayList<>(path);
                        newPath.add(new Coordinates(nx, ny));
                        queue.add(newPath);
                    }
                }
            }
        }
        
        System.out.println("Aucun chemin trouvé");
        return null;
    }
    
    
    public int getHauteur() {
        return hauteur;
    }

    public int getLargeur() {
        return largeur;
    }

    public Case[][] getMap() {
        return map;
    }
}
