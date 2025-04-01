package Helper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

/**
 * La classe Load fournit des méthodes utilitaires pour charger des images et effectuer des opérations sur les images.
 */
public class Load {
    private static final Map<String, BufferedImage> spriteCache = new ConcurrentHashMap<>();
    private static final Map<String, Map<String, Animation>> animationCache = new ConcurrentHashMap<>();

    // Gère l'accès aux sprites via le cache
    public static BufferedImage loadSprite(String name) {
        return spriteCache.computeIfAbsent(name, Load::loadSpriteFromResource);
    }

    // Charge un sprite à partir des ressources en fonction du nom
    private static BufferedImage loadSpriteFromResource(String name) {
        String resourcePath = getResourcePath(name);
        try (InputStream is = Load.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return ImageIO.read(is);
            } else {
                throw new IOException("Ressource introuvable: " + resourcePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Retourne le chemin de la ressource correspondant au nom
    private static String getResourcePath(String name) {
        return switch (name) {
            case "fd_book" -> "Ressources/inspiration/book.jpg";
            case "fd_potion" -> "Ressources/inspiration/potion.jpg";
            case "fd_gif" -> "Ressources/inspiration/vert.gif";
            case "button_play" -> "Ressources/buttons/bP.png";
            case "button_playClik" -> "Ressources/buttons/bc.png";
            case "button_quit" -> "Ressources/buttons/Qp.png";
            case "button_quitClik" -> "Ressources/buttons/qc.png";
            case "button_option" -> "Ressources/buttons/Op.png";
            case "button_optionClik" -> "Ressources/buttons/Oc.png";
            case "button_save" -> "Ressources/buttons/Sp.png";
            case "button_saveClik" -> "Ressources/buttons/Sc.png";
            case "demarrage" -> "Ressources/Tiles/Outdoor decoration/House.png";
            case "contourT" -> "Ressources/Tiles/Tiles/Grass_Middle.png";
            case "farm" -> "Ressources/Tiles/Tiles/FarmLand_Tile.png";
            case "marche" -> "Ressources/Tiles/Tiles/Marche.png";
            case "player" -> "Ressources/Tiles/Player/Player.png";
            
            case "usine" -> "Ressources/Tiles/Machine/usine1.png";
            case "recolteuse" -> "Ressources/Tiles/Machine/recolteuse1.png";
            
            case "chicken" -> "Ressources/Tiles/Animals/Chicken/Chicken.png";
            case "cow" -> "Ressources/Tiles/Animals/Cow/Cow.png";
            case "pig" -> "Ressources/Tiles/Animals/Pig/Pig.png";
            case "sheep" -> "Ressources/Tiles/Animals/Sheep/Sheep.png";
            
            case "sol1" -> "Ressources/Tiles/Sols/sol.png";
            case "sol2" -> "Ressources/Tiles/Sols/sol2.png";
            case "sol3" -> "Ressources/Tiles/Sols/sol3.png";
            case "sol4" -> "Ressources/Tiles/Sols/sol4.png";
            
            default -> "Ressources/Tiles/Animals/Pig/Pig.png";
        };
    }

    // Charge les animations du joueur
    public static Map<String, Animation> loadPlayerAnimations(BufferedImage tileSet, int tileSize, int framesPerRow) {
        return animationCache.computeIfAbsent("player", k -> createPlayerAnimations(tileSet, tileSize, framesPerRow));
    }

    private static Map<String, Animation> createPlayerAnimations(BufferedImage tileSet, int tileSize, int framesPerRow) {
        Map<String, Animation> animations = new ConcurrentHashMap<>();

        // Extraction des frames pour chaque direction
        animations.put("DOWN", createAnimation(tileSet, 0, 6, tileSize, tileSize));
        animations.put("UP", createAnimation(tileSet, 2, 6, tileSize, tileSize));
        animations.put("RIGHT", createAnimation(tileSet, 1, 6, tileSize, tileSize));

        // Flip pour les frames de la direction gauche
        List<BufferedImage> rightFrames = extractSpritesFromTile(tileSet, 32, 32, 1, 6, tileSize, tileSize);
        animations.put("LEFT", new Animation(200, flipAnimationFrames(rightFrames)));

        return animations;
    }

    // Crée l'animation des animaux
    public static Map<String, Animation> loadAnimalAnimations(String animalType, BufferedImage tileSet, int spriteWidth, int spriteHeight, int framesPerRow) {
        String cacheKey = "animal:" + animalType;
        return animationCache.computeIfAbsent(cacheKey, k -> createAnimalAnimation(tileSet, spriteWidth, spriteHeight, framesPerRow));
    }

    private static Map<String, Animation> createAnimalAnimation(BufferedImage tileSet, int spriteWidth, int spriteHeight, int framesPerRow) {
        Map<String, Animation> animations = new ConcurrentHashMap<>();
        animations.put("DOWN", createAnimation(tileSet, 0, 2, spriteWidth, spriteHeight));
        animations.put("UP", createAnimation(tileSet, 2, 2, spriteWidth, spriteHeight));
        animations.put("RIGHT", createAnimation(tileSet, 1, 2, spriteWidth, spriteHeight));

        // Frames "LEFT" en miroir
        List<BufferedImage> rightFrames = extractSpritesFromTile(tileSet, spriteWidth, spriteHeight, 1, 2, spriteWidth, spriteHeight);
        animations.put("LEFT", new Animation(200, flipAnimationFrames(rightFrames)));

        return animations;
    }

    // Crée une animation à partir du tileset
    private static Animation createAnimation(BufferedImage tileSet, int rowIndex, int frameCount, int targetWidth, int targetHeight) {
        List<BufferedImage> frames = extractSpritesFromTile(tileSet, 32, 32, rowIndex, frameCount, targetWidth, targetHeight);
        return new Animation(200, frames);
    }

    // Retourne horizontalement toutes les images d'une animation
    public static List<BufferedImage> flipAnimationFrames(List<BufferedImage> frames) {
        List<BufferedImage> flippedFrames = new ArrayList<>();
        for (BufferedImage frame : frames) {
            flippedFrames.add(flipImageHorizontally(frame));
        }
        return flippedFrames;
    }

    // Retourne l'image horizontalement
    private static BufferedImage flipImageHorizontally(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flippedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = flippedImage.createGraphics();
        g2d.drawImage(image, width, 0, -width, height, null);  // Retourner l'image horizontalement
        g2d.dispose();
        return flippedImage;
    }

    // Redimensionne une image à la taille spécifiée
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    // Extrait une liste de sprites d'une ligne donnée du tileset
    public static List<BufferedImage> extractSpritesFromTile(BufferedImage tileSet, int spriteWidth, int spriteHeight, int rowIndex, int frameCount, int targetWidth, int targetHeight) {
        List<BufferedImage> sprites = new ArrayList<>();
        int tileSetWidth = tileSet.getWidth();
        int tileSetHeight = tileSet.getHeight();

        // Extraction des frames sur une ligne donnée
        for (int i = 0; i < frameCount; i++) {
            int x = i * spriteWidth;
            int y = rowIndex * spriteHeight;

            // Vérifie que l'extraction ne dépasse pas les bords du tileset
            if (x + spriteWidth <= tileSetWidth && y + spriteHeight <= tileSetHeight) {
                BufferedImage sprite = tileSet.getSubimage(x, y, spriteWidth, spriteHeight);
                sprite = resizeImage(sprite, targetWidth, targetHeight);
                sprites.add(sprite);
            }
        }

        return sprites;
    }

}
