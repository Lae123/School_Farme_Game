package GUI.Inv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import Model.Joueur;
import Model.Ressources;
import Model.TypeRessource;
import Model.Machines.Recolteuse;

public class InventaireRecolteuse extends JFrame implements InventaireS {
    private Recolteuse recolteuse;
    private Joueur player;
    private JTable inventaireTable;

    public InventaireRecolteuse(Recolteuse recolteuse, Joueur player) {
        this.recolteuse = recolteuse;
        this.player = player;
        initScreen();
        startRefreshTimer();
    }

    @Override
    public void initScreen() {
        setTitle("Inventaire Récolteuse");
        setSize(800, 400);

        // Panel principal avec un fond vert pastel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(198, 255, 198)); // Vert pastel clair

        // Panel pour le stock de la récolteuse
        JPanel stockPanel = createStockPanel();
        
        // Panel pour les actions avec bouton visible
        JPanel actionPanel = createActionPanel("Extraire les ressources", e -> {
            recolteuse.transfererRessourcesVersJoueur(player.getInv());
            updateDisplay();
        });

        mainPanel.add(stockPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    @Override
    public void updateDisplay() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Ressource", "Quantité"}, 0);
        
        for (Map.Entry<TypeRessource, Ressources> entry : recolteuse.getInventaire().getContenu().entrySet()) {
            model.addRow(new Object[]{
                entry.getKey().name(),
                entry.getValue().getQuantite()
            });
        }
        inventaireTable.setModel(model);
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Stock de la Récolteuse"));
        panel.setBackground(new Color(170, 255, 170)); // Vert pastel clair pour le panel
        
        inventaireTable = new JTable();
        updateDisplay();
        panel.add(new JScrollPane(inventaireTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionPanel(String buttonText, ActionListener action) {
        JPanel actionPanel = new JPanel();
        
        // Création et stylisation du bouton
        JButton actionButton = new JButton(buttonText);
        styleButton(actionButton);
        actionButton.addActionListener(action);
        
        actionPanel.add(actionButton);
        return actionPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(34, 139, 34)); // Vert pastel plus foncé
        button.setForeground(Color.WHITE); // Texte en blanc pour un meilleur contraste
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Police plus épaisse pour meilleure lisibilité
        button.setBorder(BorderFactory.createLineBorder(new Color(50, 205, 50))); // Bordure verte claire
    }

    private void startRefreshTimer() {
        Timer refreshTimer = new Timer(1000, e -> this.updateDisplay());
        refreshTimer.start();
    }
}
