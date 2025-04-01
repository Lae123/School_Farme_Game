package Helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * La classe LoadSound fournit des méthodes utilitaires pour charger des sons et les jouer.
 */
public class LoadSound implements Serializable{

    // Cache des sons chargés
    private static Map<String, Clip> soundCache = new HashMap<>();
            // Variables pour les volumes
            private static float masterVolume = 50.0f;  // Volume général
            private static float musicVolume = 50.0f;   // Volume de la musique
            private static float effectsVolume = 50.0f; // Volume des effets

    /**
     * Charge un son depuis les ressources et le retourne.
     * Utilise un cache pour éviter de charger plusieurs fois le même son.
     *
     * @param name Le nom du fichier son (sans l'extension).
     * @return Le clip audio correspondant au son.
     */
    public static Clip loadSound(String name) {
        Clip clip = soundCache.get(name);
        if (clip == null) {
            // Si le clip n'est pas déjà dans le cache, essayez de le charger
            clip = loadSoundFromResource(name);
            if (clip != null) {
                soundCache.put(name, clip);  // Ajoutez le clip dans le cache
            }
        }
        return clip;
    }
    

    /**
     * Charge un son depuis les ressources en fonction du nom.
     * 
     * @param name Le nom du fichier son (sans l'extension).
     * @return Le clip audio chargé.
     */
    private static Clip loadSoundFromResource(String name) {
        String resourcePath = getResourcePath(name);
        System.out.println("Loading resource from path: " + resourcePath);  // Log du chemin
    
        try (InputStream is = LoadSound.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(is);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                return clip;
            } else {
                throw new IOException("Ressource introuvable: " + resourcePath);
            }
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();  // Imprime l'exception pour aider au débogage
            return null;
        }
    }
    
    

    /**
     * Retourne le chemin de la ressource correspondant au nom du son.
     * 
     * @param name Le nom du son.
     * @return Le chemin du fichier de son.
     */
    private static String getResourcePath(String name) {
        switch (name) {
            case "background_music1": return "Ressources/Tiles/Sons/gameLoop1.wav";
            case "button_click": return "Ressources/Tiles/Sons/boutons.wav";
            // case "background_music2": return "Ressources/Sons/gameLoop2.wav";
            case "game_over": return "Ressources/Sons/finJeu.wav";
            case "effet": return "Ressources/Tiles/Sons/effet.wav";
            default: return "Ressources/Sons/boutons.wav";
        }
    }

    /**
     * Joue le son spécifié.
     * 
     * @param name Le nom du son à jouer.
     */
    public static void playSound(String name) {
        Clip clip = loadSound(name);
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();
        }
    }

    /**
     * Arrête la lecture d'un son spécifique.
     * 
     * @param name Le nom du son à arrêter.
     */
    public static void stopSound(String name) {
        Clip clip = soundCache.get(name);
        if (clip != null) {
            clip.stop();
        }
    }

    
    public static void loopSound(String name) {
        Clip clip = loadSound(name);
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }


    public static void pauseSound(String name) {
        Clip clip = soundCache.get(name);
        if (clip != null) {
            clip.stop();
        }
    }
    
    public static float getMasterVolume() {
        return masterVolume;
    }

    public static void setMasterVolume(float volume) {
        masterVolume = volume;
        updateAllVolumes();
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static void setMusicVolume(float volume) {
        musicVolume = volume;
        updateVolumesForCategory("music");
    }

    public static float getEffectsVolume() {
        return effectsVolume;
    }

    public static void setEffectsVolume(float volume) {
        effectsVolume = volume;
        updateVolumesForCategory("effects");
    }

    

    private static void applyVolume(Clip clip, String name) {
        if (clip != null) {
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = masterVolume;
            if (name.contains("music")) {
                volume = musicVolume;
            } else if (name.contains("effects")) {
                volume = effectsVolume;
            }
            setVolume(clip, volume);
        }
    }

    public static void setVolume(String name, float volume) {
        Clip clip = soundCache.get(name);
        if (clip != null) {
            setVolume(clip, volume);
        } else {
            System.err.println("Erreur : Clip " + name + " non trouvé.");
        }
    }

    private static void setVolume(Clip clip, float volume) {
        if (clip != null) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float minValue = volumeControl.getMinimum();
                float maxValue = volumeControl.getMaximum();
                float db = (float) (Math.log10(volume / 100.0) * 20);
                db = Math.max(minValue, Math.min(db, maxValue));
                volumeControl.setValue(db);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    // Appliquer les volumes mis à jour à toutes les catégories
    private static void updateAllVolumes() {
        soundCache.forEach((name, clip) -> applyVolume(clip, name));
    }

    // Met à jour les volumes pour une catégorie spécifique
    private static void updateVolumesForCategory(String category) {
        soundCache.forEach((name, clip) -> {
            if (name.contains(category)) {
                applyVolume(clip, name);
            }
        });
    }
}
    




