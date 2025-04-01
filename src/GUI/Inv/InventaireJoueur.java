package GUI.Inv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Model.Inventaire;
import Model.Joueur;
import Model.Recette;
import Model.Ressources;
import Model.TypeRessource;

public class InventaireJoueur extends JFrame {
    private Joueur player;
    private JTable recettesTable; // Table des recettes
    private JTable inventaireTable; // Table de l'inventaire des ressources
    private JPanel recettesPanel;
    private JPanel inventairePanel;
    private JTable craftedItemsTable; // Table pour les objets craftés
    private JPanel craftedItemsPanel; // Panneau pour l'inventaire des objets craftés


    public InventaireJoueur(Joueur player) {
        this.player = player;
        initScreen();
    }

    private void initScreen() {
        setTitle("Inventaire et Craft");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(198, 255, 198));

        recettesPanel = createRecettesPanel();
        inventairePanel = createInventairePanel();
        craftedItemsPanel = createCraftedItemsPanel(); // Nouveau panneau pour objets craftés

        mainPanel.add(recettesPanel, BorderLayout.CENTER);
        mainPanel.add(inventairePanel, BorderLayout.EAST);
        mainPanel.add(craftedItemsPanel, BorderLayout.SOUTH); // Ajouter en bas de l'écran

        add(mainPanel);
    }

    private JPanel createRecettesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Recettes disponibles"));
        panel.setBackground(new Color(198, 255, 198));
    
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Recette", "Ingrédients", "Action"}, 0);
        recettesTable = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Rendre uniquement la colonne Action éditable
                return column == 2;
            }
        };
        recettesTable.setRowHeight(30);
    
        for (Recette recette : player.getLivreDeRecette().getRecettesDisponibles()) {
            model.addRow(new Object[]{
                recette.getNom(),
                formatIngredients(recette),
                "Craft" // Texte du bouton
            });
        }
    
        recettesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        recettesTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this::handleCraftButtonClick));
    
        panel.add(new JScrollPane(recettesTable), BorderLayout.CENTER);
    
        return panel;
    }    

    private JPanel createInventairePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Inventaire des ressources"));
        panel.setBackground(new Color(198, 255, 198));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Ressource", "Quantité"}, 0);
        inventaireTable = new JTable(model);
        updateInventaireDisplay();

        panel.add(new JScrollPane(inventaireTable), BorderLayout.CENTER);

        return panel;
    }

    private String formatIngredients(Recette recette) {
        StringBuilder ingredients = new StringBuilder();
        for (Map.Entry<TypeRessource, Integer> entry : recette.getIngredients().entrySet()) {
            ingredients.append(entry.getKey().name()).append(": ").append(entry.getValue()).append(", ");
        }
        return ingredients.toString();
    }

    private JPanel createCraftedItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Objets Craftés"));
        panel.setBackground(new Color(198, 255, 198));
    
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Objet", "Quantité"}, 0);
        craftedItemsTable = new JTable(model);
    
        panel.add(new JScrollPane(craftedItemsTable), BorderLayout.CENTER);
    
        return panel;
    }
    private void handleCraftButtonClick(int rowIndex) {
        Recette recette = player.getLivreDeRecette().getRecettesDisponibles().get(rowIndex);
        Inventaire inventaire = player.getInv();
    
        // Vérifier si le joueur a assez de ressources pour chaque ingrédient
        boolean canCraft = true;
        for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
            int availableQty = inventaire.getQuantiteRessource(ingredient.getKey());
            if (availableQty < ingredient.getValue()) {
                canCraft = false;
                break;
            }
        }
    
        if (canCraft) {
            // Consommer les ressources
            for (Map.Entry<TypeRessource, Integer> ingredient : recette.getIngredients().entrySet()) {
                inventaire.retirerRessource(ingredient.getKey(), ingredient.getValue());
            }
    
            // Ajouter le produit crafté
            TypeRessource produit = recette.getProduit();
            inventaire.ajouterRessource(new Ressources(produit, 1), 1);
    
            // Mettre à jour la table des objets craftés
            updateCraftedItemsDisplay(recette.getNom());
    
            JOptionPane.showMessageDialog(this, "Vous avez crafté : " + recette.getNom(), "Succès", JOptionPane.INFORMATION_MESSAGE);
    
            // Mettre à jour l'inventaire des ressources
            updateInventaireDisplay();
    
        } else {
            JOptionPane.showMessageDialog(this, "Ressources insuffisantes pour craft : " + recette.getNom(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCraftedItemsDisplay(String craftedItemName) {
        DefaultTableModel model = (DefaultTableModel) craftedItemsTable.getModel();
    
        // Vérifier si l'objet existe déjà dans la table
        boolean exists = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            String itemName = (String) model.getValueAt(i, 0);
            if (itemName.equals(craftedItemName)) {
                int currentQty = (int) model.getValueAt(i, 1);
                model.setValueAt(currentQty + 1, i, 1);
                exists = true;
                break;
            }
        }
    
        // Si l'objet n'existe pas, l'ajouter
        if (!exists) {
            model.addRow(new Object[]{craftedItemName, 1});
        }
    }

    private void updateInventaireDisplay() {
        DefaultTableModel model = (DefaultTableModel) inventaireTable.getModel();
        model.setRowCount(0); // Effacer les anciennes données

        Inventaire inventaire = player.getInv();
        for (Map.Entry<TypeRessource, Ressources> entry : inventaire.getContenu().entrySet()) {
            model.addRow(new Object[]{
                entry.getKey().name(),
                entry.getValue().getQuantite()
            });
        }
    }
}
