package GUI;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Helper.LoadSound;

public class VolumeSettingsPanel extends JFrame {

    public VolumeSettingsPanel() {
        setTitle("Réglages du Volume");
        setSize(400, 300);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        // Créer les curseurs pour régler le volume
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JLabel musicLabel = new JLabel("Volume de la musique:");
        JSlider musicSlider = new JSlider(0, 100, (int) LoadSound.getMusicVolume());
        musicSlider.addChangeListener(e -> LoadSound.setMusicVolume(musicSlider.getValue()));
    
        JLabel effectsLabel = new JLabel("Volume des effets sonores:");
        JSlider effectsSlider = new JSlider(0, 100, (int) LoadSound.getEffectsVolume());
        effectsSlider.addChangeListener(e -> LoadSound.setEffectsVolume(effectsSlider.getValue()));
    
        JLabel masterLabel = new JLabel("Volume général:");
        JSlider masterSlider = new JSlider(0, 100, (int) LoadSound.getMasterVolume());
        masterSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int masterVolume = masterSlider.getValue();
                LoadSound.setMasterVolume(masterVolume);
                musicSlider.setValue(masterVolume); // Synchroniser
                effectsSlider.setValue(masterVolume); // Synchroniser
            }
        });
    
        panel.add(masterLabel);
        panel.add(masterSlider);
        panel.add(musicLabel);
        panel.add(musicSlider);
        panel.add(effectsLabel);
        panel.add(effectsSlider);
    
        add(panel, BorderLayout.CENTER);
    
        // Bouton de fermeture
        JButton closeButton = new JButton("Fermer");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);
    
        setVisible(true);
    }
    
}
