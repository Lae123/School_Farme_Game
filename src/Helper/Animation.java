package Helper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Animation implements Serializable {
    private List<byte[]> framesData;  // Stocke les images sous forme de tableaux d'octets
    private int currentFrame;
    private long lastTime;
    private long delay;
        
    public Animation(long delay, List<BufferedImage> frames) {
        this.framesData = new ArrayList<>();
        this.delay = delay;
        this.currentFrame = 0;
        this.lastTime = System.currentTimeMillis();

        // Convertir les images en tableaux d'octets
        for (BufferedImage frame : frames) {
            framesData.add(toByteArray(frame));
        }
    }

    // Méthode pour convertir un BufferedImage en tableau d'octets
    private byte[] toByteArray(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);  // Convertir en PNG
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    // Méthode pour convertir un tableau d'octets en BufferedImage
    private BufferedImage toBufferedImage(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            return ImageIO.read(bais);  // Lire le tableau d'octets comme image
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthode de mise à jour de l'animation
    public void update() {
        if (System.currentTimeMillis() - lastTime >= delay) {
            currentFrame++;
            if (currentFrame >= framesData.size()) {
                currentFrame = 0;  // Repartir au premier frame de l'animation
            }
            lastTime = System.currentTimeMillis();
        }
    }

    // Méthode pour obtenir le frame actuel en tant qu'image
    public BufferedImage getCurrentFrame() {
        return toBufferedImage(framesData.get(currentFrame));
    }

    // Optionnel : Méthode pour sérialiser l'animation
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(framesData);
        out.writeLong(delay);
    }
            
     // Optionnel : Méthode pour désérialiser l'animation
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        framesData = (List<byte[]>) in.readObject();
        delay = in.readLong();
        currentFrame = 0;
        lastTime = System.currentTimeMillis();
    }
}
