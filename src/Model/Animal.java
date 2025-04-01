package Model;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Map;
import java.util.Random;

import javax.swing.Timer;

import Helper.Animation;
import geometry.Coordinates;

public class Animal implements Serializable{
    private int x, y, speed;
    private int directionX, directionY;
    private int width, height;
    private Map<String, Animation> animations;  // Pour stocker les animations
    private Animation currentAnimation;
    private Coordinates targetPos;  // La position cible
    private double stepProgress = 0.0; // La progression du mouvement

    public Animal(int x, int y, int width, int height, int speed, Animation initialAnimation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.currentAnimation = initialAnimation;
        this.targetPos = new Coordinates(x, y); // Initialement, l'animal est à sa position actuelle
        randomizeDirection();
    }

    public void setAnimations(Map<String, Animation> animations) {
        this.animations = animations;
        this.currentAnimation = animations.getOrDefault("DOWN", null);  // Animation par défaut
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void moveStepByStep(Coordinates newPos, int delay) {
        // Changer d'animation en fonction de la direction du mouvement
        if (newPos.x() > x) {
            setCurrentAnimation("RIGHT");
        } else if (newPos.x() < x) {
            setCurrentAnimation("LEFT");
        } else if (newPos.y() > y) {
            setCurrentAnimation("DOWN");
        } else {
            setCurrentAnimation("UP");
        }
    
        this.targetPos = newPos;
        this.stepProgress = 0.0;  // Réinitialise la progression
    
        Timer timer = new Timer(delay, new ActionListener() {
            int currentStep = 0;
    
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < 10) { 
                    stepProgress = (double) currentStep / 10; 
    
                    // Calculer les nouvelles positions en fonction de la progression
                    int newX = (int) (x + (targetPos.x() - x) * stepProgress);
                    int newY = (int) (y + (targetPos.y() - y) * stepProgress);
    
                    // Met à jour la position de l'animal
                    x = newX;
                    y = newY;
    
                    currentStep++;
                } else {
                    // Quand le mouvement est terminé, on arrête le timer
                    ((Timer)e.getSource()).stop();
                }
            }
        });
    
        timer.start();
    }
    

    // Met à jour la position de l'animal de manière aléatoire
    public void randomMove(int mapWidth, int mapHeight) {
        Random random = new Random();

        // Générer une nouvelle position cible aléatoire
        int targetX = x + (random.nextInt(3) - 1) * speed; // -1, 0 ou 1
        int targetY = y + (random.nextInt(3) - 1) * speed; // -1, 0 ou 1

        // Vérifier que la nouvelle position est dans les limites de la carte
        if (targetX >= 0 && targetX < mapWidth - width && targetY >= 0 && targetY < mapHeight - height) {
            moveStepByStep(new Coordinates(targetX, targetY), 100); // Déplacer progressivement
        }
    }


    // Changer aléatoirement la direction de l'animal
    private void randomizeDirection() {
        Random random = new Random();
        directionX = random.nextInt(3) - 1; // -1, 0, ou 1
        directionY = random.nextInt(3) - 1; // -1, 0, ou 1
    }

    // Dessine l'animal avec l'animation actuelle
    public void draw(Graphics g) {
        if (currentAnimation != null) {
            g.drawImage(currentAnimation.getCurrentFrame(), x, y, width, height, null);
        }
    }

    // Met à jour l'animation actuelle de l'animal
    public void setCurrentAnimation(String animationName) {
        Animation newAnimation = animations.get(animationName);
        if (newAnimation != null) {
            currentAnimation = newAnimation;
        }
    }
}
