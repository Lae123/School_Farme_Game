package Helper;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Model.Joueur;
import Model.Machine;
import Model.MapG;
import Model.Partie;
import geometry.Coordinates;

public class GameUtils implements Serializable{
    public static final String SAVE_FILE = "Helper/Sauvegarde/start_sauvegarde.dat";

        // Méthode pour afficher une boîte de dialogue pour choisir une machine
    public static Machine chooseMachineType(JPanel panel, Coordinates pos, MapG map, Joueur player) {
        Machine machine = null;
        
        // Récupération des machines que le joueur possède
        List<Machine> machinesPossedees = player.getMachines(); 
        
        // Si le joueur ne possède aucune machine, afficher un message d'erreur
        if (machinesPossedees.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Vous ne possédez aucune machine.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return null; // Aucune machine à choisir
        }
        
        // Création d'un tableau d'options avec le nom de chaque machine
        String[] options = new String[machinesPossedees.size()];
        for (int i = 0; i < machinesPossedees.size(); i++) {
            options[i] = machinesPossedees.get(i).getClass().getSimpleName(); // Utilise le nom de la classe (type de la machine)
        }
        
        // Affichage de la boîte de dialogue pour choisir la machine
        int choice = JOptionPane.showOptionDialog(
                panel,
                "Quel type de machine voulez-vous placer ?",
                "Choisir une machine",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0] // Valeur par défaut sélectionnée
        );
        
        if (choice == -1) {
            return null; // L'utilisateur a annulé
        }
        
        // Récupérer la machine choisie à partir de la liste de machines du joueur
        machine = machinesPossedees.get(choice);
        
        // Créer la machine avec les coordonnées choisies
        machine.setPosition(pos); // Positionne la machine choisie aux coordonnées spécifiées
        return machine;
    }



    public static void saveGame(Partie partie) {
        try (FileOutputStream fileOut = new FileOutputStream(SAVE_FILE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(partie);  // Sérialisation de l'objet Partie
            System.out.println("Partie sauvegardée avec succès.");
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde de la partie : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static boolean checkIfSaveExists() {
        File saveFile = new File(SAVE_FILE);
        return saveFile.exists() && saveFile.isFile();
    }

    public static Partie loadGame() {
        try (FileInputStream fileIn = new FileInputStream(SAVE_FILE);
            ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Partie loadedPartie = (Partie) in.readObject();  // Désérialisation de l'objet Partie
            System.out.println("Partie chargée avec succès.");
            return loadedPartie;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur lors du chargement de la partie : " + e.getMessage());
            e.printStackTrace();
            return null;  // Retourne null si un problème survient
        }
    }

    public static JButton createRetroButton(String text, BufferedImage defaultImage, BufferedImage hoverImage, BufferedImage pressedImage) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage imgToDraw = defaultImage;
                if (getModel().isPressed() && pressedImage != null) {
                    imgToDraw = pressedImage;
                } else if (getModel().isRollover() && hoverImage != null) {
                    imgToDraw = hoverImage;
                }

                if (imgToDraw != null) {
                    g.drawImage(imgToDraw, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.RED);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.WHITE);
                    g.drawString(text, getWidth() / 2 - 20, getHeight() / 2 + 5);
                }
            }
        };

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public static void switchToMainScreen(JPanel panel) {
        // Obtenez la fenêtre principale (MenuPrincipal) en utilisant SwingUtilities
        
    }
    
    
}
