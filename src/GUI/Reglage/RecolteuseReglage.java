package GUI.Reglage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import Model.MapG;
import Model.Ressources;
import Model.TypeRessource;
import Model.Machines.Recolteuse;

public class RecolteuseReglage extends JFrame {
    private Recolteuse recolteuse;
    private JList<String> ressourcesList;
    private DefaultListModel<String> listModel;
    private MapG map;

    public RecolteuseReglage(Recolteuse recolteuse, MapG map) {
        this.recolteuse = recolteuse;
        this.map = map;
        initScreen();
    }

    private void initScreen() {
        setTitle("Réglages de la Récolteuse");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titre
        JLabel titleLabel = new JLabel("Réglages de la Récolteuse", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Liste des ressources adjacentes
        listModel = new DefaultListModel<>();
        for (Ressources ressource : map.getRessourcesAdjacentes(this.recolteuse.getPosition())) {
            listModel.addElement(ressource.getType().name());
        }
        ressourcesList = new JList<>(listModel);
        ressourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ressourcesList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane listScrollPane = new JScrollPane(ressourcesList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Ressources adjacentes"));
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton selectButton = new JButton("Sélectionner");
        selectButton.setFont(new Font("Arial", Font.PLAIN, 14));
        selectButton.addActionListener(new SelectRessourceAction());
        buttonPanel.add(selectButton);

        JButton closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private class SelectRessourceAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = ressourcesList.getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedRessource = listModel.get(selectedIndex);
                TypeRessource type = TypeRessource.valueOf(selectedRessource);
                recolteuse.setRessourceCible(type);
                JOptionPane.showMessageDialog(RecolteuseReglage.this,
                        "Ressource sélectionnée : " + selectedRessource,
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
                        recolteuse.startPeriodicHarvest(map);
            } else {
                JOptionPane.showMessageDialog(RecolteuseReglage.this,
                        "Veuillez sélectionner une ressource.",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}

