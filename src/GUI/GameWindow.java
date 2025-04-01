package GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Helper.GameUtils;
import Helper.Load;
import Helper.LoadSound;

public class GameWindow extends JFrame {

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int MARGIN_LEFT = 20;
    private static final int MARGIN_TOP = 100;
    private static final int BORDER_SIZE = 50;

    // Stockage des images des boutons pour éviter de les recharger à chaque fois
    private static BufferedImage buttonSaveImg, buttonSaveHoverImg, buttonSavePressedImg;
    private static BufferedImage buttonQuitImg, buttonQuitHoverImg, buttonQuitPressedImg;
    private static BufferedImage buttonopttImg, buttonoptHoverImg;

    static {
        // Charger les images au démarrage pour ne pas les charger à chaque appel
        buttonSaveImg = Load.loadSprite("button_save");
        buttonSaveHoverImg = Load.loadSprite("button_saveClik");
        buttonSavePressedImg = Load.loadSprite("button_saveClik");

        buttonQuitImg = Load.loadSprite("button_quit");
        buttonQuitHoverImg = Load.loadSprite("button_quitClik");
        buttonQuitPressedImg = Load.loadSprite("button_quitClik");

        buttonopttImg = Load.loadSprite("button_option");
        buttonoptHoverImg = Load.loadSprite("button_optionClik");
    }

    public GameWindow(String title, MapDisplay mapDisplay) {
        super(title);

        // Initialiser et configurer la fenêtre
        setupWindow(mapDisplay);

        // Créer le panneau principal et configurer son contenu
        JPanel panel = createMainPanel(mapDisplay);

        // Ajouter le panneau à la fenêtre
        add(panel);

        // Configuration finale
        finalizeWindowSetup(panel, mapDisplay);
    }

    private void setupWindow(MapDisplay mapDisplay) {
        // Calculer les dimensions de la fenêtre selon la taille de la carte
        int mapWidth = mapDisplay.getPreferredSize().width;
        int mapHeight = mapDisplay.getPreferredSize().height;

        
        setSize(mapWidth + BORDER_SIZE, mapHeight + (MARGIN_TOP) * 2);

        // Configurer la fermeture et centrer la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre à l'écran
    }

    private JButton createButton(String text, ActionListener actionListener,
                                BufferedImage defaultImage, BufferedImage hoverImage, BufferedImage pressedImage) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage imgToDraw = defaultImage;

                // Gérer l'image en fonction de l'état du bouton
                if (getModel().isPressed() && pressedImage != null) {
                    imgToDraw = pressedImage;
                } else if (getModel().isRollover() && hoverImage != null) {
                    imgToDraw = hoverImage;
                }

                // Dessiner l'image ou le texte sur le bouton
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
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(actionListener);

        return button;
    }

    
    private JPanel createMainPanel(MapDisplay mapDisplay) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
    
        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBounds(0, 0, getWidth(), MARGIN_TOP);
        buttonPanel.setOpaque(false);
    
        // Bouton Save
        buttonPanel.add(createGameButton("save", e -> GameUtils.saveGame(mapDisplay.getGameController().getPartie()), 
                buttonSaveImg, buttonSavePressedImg));
    
        // Bouton Quit
        buttonPanel.add(createGameButton("quit", e -> {
            LoadSound.playSound("button_click");
            System.exit(0);
        }, buttonQuitImg, buttonQuitPressedImg));
    
        // Bouton Options
        buttonPanel.add(createGameButton("options", e -> {
            new VolumeSettingsPanel(); // Ouvrir les réglages de volume
        }, buttonopttImg, buttonoptHoverImg));
    
        // Ajouter le panneau de boutons au panneau principal
        panel.add(buttonPanel);
    
        // Positionner la carte au centre
        int yPosition = (getHeight() - mapDisplay.getPreferredSize().height - MARGIN_TOP) / 2;
        mapDisplay.setBounds(MARGIN_LEFT, yPosition, mapDisplay.getPreferredSize().width, mapDisplay.getPreferredSize().height);
        panel.add(mapDisplay);
        panel.setBackground(new Color(179, 217, 123));
    
        // Rendre le panneau focusable
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    
        return panel;
    }
    

    private JButton createGameButton(String buttonText, ActionListener actionListener, BufferedImage buttonImage, BufferedImage buttonPress) {
        return createButton(buttonText, actionListener, buttonImage, buttonPress, buttonPress);
    }

    private void finalizeWindowSetup(JPanel panel, MapDisplay mapDisplay) {
        mapDisplay.getGameController().addKeyListenerToPanel(panel);
        setVisible(true);
    }
}
