package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import Helper.GameUtils;
import Helper.Load;
import Helper.LoadSound;
import Model.Partie;

public class MenuPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private boolean isGameStarted;
    private BufferedImage backgroundImage;
    private static final String SAVE_FILE = "savegame.dat";

    // Images de boutons pré-chargées
    private static BufferedImage buttonPlayImg, buttonHoverImg, buttonPressedImg;
    private static BufferedImage buttonSettingImg, buttonSettingCImg;
    private static BufferedImage buttonQuitImg, buttonQuitCImg;
    private static BufferedImage buttonSaveImg, buttonSaveCImg;
    
    static {
        // Charger toutes les images nécessaires dès le début
        buttonPlayImg = Load.loadSprite("button_play");
        buttonHoverImg = Load.loadSprite("button_play");
        buttonPressedImg = Load.loadSprite("button_playClik");
        buttonSettingImg = Load.loadSprite("button_option");
        buttonSettingCImg = Load.loadSprite("button_optionClik");
        buttonQuitImg = Load.loadSprite("button_quit");
        buttonQuitCImg = Load.loadSprite("button_quitClik");
        buttonSaveImg = Load.loadSprite("button_save");
        buttonSaveCImg = Load.loadSprite("button_saveClik");
    }

    public boolean getGame() {
        return isGameStarted;
    }

    public MenuPrincipal() {
        setTitle("Jeu Rétro - Menu Principal");
        setSize(800, 600); // Taille agrandie
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Charger l'image de fond
        backgroundImage = Load.loadSprite("fd_gif");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Ajouter les différents écrans
        mainPanel.add(createMenuPanel(), "MenuPrincipal");
        mainPanel.add(createSettingsPanel(), "Settings");

        add(mainPanel);
        setVisible(true);
    }

    public JPanel getMainPanel() {
        return mainPanel; // Retourne le panneau contenant le CardLayout
    }
    

    public void showMenuPanel() {
        cardLayout.show(mainPanel, "MenuPrincipal"); // Afficher le panneau du menu
    }

    private void navigateTo(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    private void startNewGame() {
        isGameStarted = true;
        Partie partie = new Partie();
        LoadSound.loopSound("background_music1");
        new GameWindow("Affichage de la carte", new MapDisplay(partie.getGc()));
    }

    private void startGameSaved(Partie a) {
        isGameStarted = true;
        Partie partie = a;
        LoadSound.loopSound("background_music1");
        new GameWindow("Affichage de la carte", new MapDisplay(partie.getGc()));
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        menuPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Bienvenue dans le Jeu !", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Press Start 2P", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(150, 50, 500, 80);
        menuPanel.add(titleLabel);

        // Créer les boutons avec une méthode générique
        menuPanel.add(createMenuButton("Nouvelle Partie", buttonPlayImg, buttonHoverImg, buttonPressedImg, 250, 180, e -> {
            LoadSound.playSound("button_click");
            startNewGame();
        }));

        menuPanel.add(createMenuButton("Continuer", buttonSaveImg, buttonSaveImg, buttonSaveCImg, 250, 260, e -> {
            LoadSound.playSound("button_click");
            if(GameUtils.checkIfSaveExists()){startGameSaved(GameUtils.loadGame());}
        }));

        menuPanel.add(createMenuButton("Quitter", buttonQuitImg, buttonHoverImg, buttonQuitCImg, 250, 340, e -> {
            LoadSound.playSound("button_click");
            System.exit(0);
        }));

        menuPanel.add(createMenuButton("Réglages", buttonSettingImg, buttonHoverImg, buttonSettingCImg, 250, 420, e -> {
            LoadSound.playSound("button_click");
            navigateTo("Settings");
        }));

        return menuPanel;
    }

    JPanel createSettingsPanel() {
        // Panel principal avec fond d'image
        JPanel settingsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fond dégradé si pas d'image
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 25), 
                        0, getHeight(), new Color(45, 45, 45));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    
        // Configuration du panel principal
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
    
        // Titre des réglages avec effet d'ombre
        JLabel settingsTitle = createStyledLabel("Réglages", 28);
        settingsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
    
        // Panel pour les contrôles audio
        JPanel audioControlsPanel = createAudioControlsPanel();
    
        // Panel pour les autres options (extensible)
        JPanel optionsPanel = createOptionsPanel();
    
        // Panel pour le bouton retour
        JPanel navigationPanel = createNavigationPanel();
    
        // Assemblage final
        settingsPanel.add(settingsTitle);
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(audioControlsPanel);
        settingsPanel.add(Box.createVerticalStrut(20));
        settingsPanel.add(optionsPanel);
        settingsPanel.add(Box.createVerticalGlue());
        settingsPanel.add(navigationPanel);
    
        return settingsPanel;
    }
    
    public JPanel createAudioControlsPanel() {
        JPanel audioPanel = new JPanel();
        audioPanel.setLayout(new BoxLayout(audioPanel, BoxLayout.Y_AXIS));
        audioPanel.setOpaque(false);
    
        // Sous-titre audio
        JLabel audioTitle = createStyledLabel("Audio", 20);
        audioPanel.add(audioTitle);
        audioPanel.add(Box.createVerticalStrut(15));
    
        // Volume principal
        audioPanel.add(createVolumeControl("Volume Principal", "background_music1", 50));
       // audioPanel.add(Box.createVerticalStrut(10));
    
        // Volume des effets
        audioPanel.add(createVolumeControl("Effets Sonores", "button_click", 50));
    
        return audioPanel;
    }
    
    private JPanel createVolumeControl(String label, String soundKey, int defaultValue) {
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setOpaque(false);
    
        // Label
        JLabel titleLabel = createStyledLabel(label, 16);
        titleLabel.setPreferredSize(new Dimension(200, 30));
    
        // Slider personnalisé
        JSlider volumeSlider = new JSlider(0, 100, defaultValue) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond du slider
                g2d.setColor(new Color(60, 60, 60));
                g2d.fillRoundRect(0, getHeight() / 3, getWidth(), getHeight() / 3, 10, 10);
    
                // Partie active du slider
                g2d.setColor(new Color(0, 255, 127));
                int width = (int) (getWidth() * (getValue() / 100.0));
                g2d.fillRoundRect(0, getHeight() / 3, width, getHeight() / 3, 10, 10);
    
                // Bouton du slider
                g2d.setColor(Color.WHITE);
                int thumbX = (int) (getWidth() * (getValue() / 100.0)) - 6;
                g2d.fillOval(thumbX, getHeight() / 4, 12, 12);
            }
        };
        
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(200, 30));
    
        // Label pour la valeur
        JLabel valueLabel = createStyledLabel(defaultValue + "%", 14);
        valueLabel.setPreferredSize(new Dimension(50, 30));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
        // Gestion des événements
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            valueLabel.setText(volume + "%");
            LoadSound.setVolume(soundKey, volume);
            
            // Jouer un son de test uniquement quand on relâche le slider
            if (!volumeSlider.getValueIsAdjusting() && soundKey.equals("button_click") || soundKey.equals("background_music1")) {
                LoadSound.playSound("button_click");

            }

            if (!volumeSlider.getValueIsAdjusting() && soundKey.equals("background_music1")) {
                LoadSound.playSound("background_music1");
            }
        });
    
        controlPanel.add(titleLabel, BorderLayout.WEST);
        controlPanel.add(volumeSlider, BorderLayout.CENTER);
        controlPanel.add(valueLabel, BorderLayout.EAST);
    
        return controlPanel;
    }
    
    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setOpaque(false);
        return optionsPanel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        navPanel.setOpaque(false);
    
        JButton backButton = GameUtils.createRetroButton("Retour", buttonQuitImg, buttonQuitImg, buttonQuitCImg);
        backButton.addActionListener(e -> {
            LoadSound.playSound("button_click");
            navigateTo("MenuPrincipal");
        });
    
        navPanel.add(backButton);
        return navPanel;
    }
    
    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Press Start 2P", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Effet d'ombre
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 2, 2),
            BorderFactory.createLineBorder(new Color(0, 0, 0, 50))
        ));
        
        return label;
    }

    private JButton createMenuButton(String text, BufferedImage defaultImage, BufferedImage hoverImage, BufferedImage pressedImage, int x, int y, ActionListener action) {
        JButton button = GameUtils.createRetroButton(text, defaultImage, hoverImage, pressedImage);
        button.setBounds(x, y, 300, 60);
        button.addActionListener(action);
        return button;
    }
}
