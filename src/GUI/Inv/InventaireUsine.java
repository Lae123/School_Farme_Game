package GUI.Inv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import Model.Joueur;
import Model.Recette;
import Model.Ressources;
import Model.TypeRessource;
import Model.Machines.Usine;

public class InventaireUsine extends JFrame implements InventaireS {
    private Usine usine;
    private Joueur player;
    private JTable stockTable;
    private JTable recetteTable;
    private JTable recettesDisponiblesTable;
    private Recette recetteActuelle;
    private Map<TypeRessource, Integer> stockMarche; // Stock initial du marché


    public InventaireUsine(Usine usine, Joueur player) {
        this.usine = usine;
        this.player = player;
        this.recetteActuelle = null;
        this.stockMarche = initMarcheStock(); // Initialiser le marché
        initScreen();
        startRefreshTimer();
    }

    private Map<TypeRessource, Integer> initMarcheStock() {
        Map<TypeRessource, Integer> stock = new HashMap<>();
        for (TypeRessource ressource : TypeRessource.values()) {
            stock.put(ressource, 100); // Initialiser avec 100 unités pour chaque ressource
        }
        return stock;
    }

    @Override
    public void initScreen() {
        setTitle("Inventaire Usine");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel principal avec un fond vert pastel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(198, 255, 198)); // Vert pastel clair
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createRecettesDisponiblesPanel(), BorderLayout.NORTH);
        mainPanel.add(createStockPanel(), BorderLayout.CENTER);
        mainPanel.add(createRecettePanel(), BorderLayout.EAST);
        mainPanel.add(createActionPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    @Override
    public void updateDisplay() {
        updateStockDisplay();
        updateRecetteIngredientsDisplay();
        updateRecettesDisponiblesDisplay();
    }

    private JPanel createRecettesDisponiblesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Recettes Disponibles",
            0, 0, panel.getFont().deriveFont(16f)
        ));
        panel.setBackground(new Color(170, 255, 170)); // Vert pastel clair pour le panel

        recettesDisponiblesTable = new JTable();
        updateRecettesDisponiblesDisplay();

        recettesDisponiblesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = recettesDisponiblesTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    recetteActuelle = player.getLivreDeRecette().get(row);
                    updateDisplay();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(recettesDisponiblesTable);
        scrollPane.setBackground(Color.WHITE); // Fond clair pour le tableau
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 150))); // Bordure verte douce

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void updateRecettesDisponiblesDisplay() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Recette", "Détails"}, 0);
        for (Recette recette : player.getLivreDeRecette().getRecettesDisponibles()) {
            model.addRow(new Object[]{recette.getNom(), "Ingrédients: " + recette.getIngredients().size()});
        }
        recettesDisponiblesTable.setModel(model);
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Produits Fabriqués",
            0, 0, panel.getFont().deriveFont(16f)
        ));
        panel.setBackground(new Color(170, 255, 170)); // Vert pastel clair pour le panel

        stockTable = new JTable();
        updateStockDisplay();

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBackground(Color.WHITE); // Fond clair pour le tableau
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(150, 200, 150))); // Bordure verte douce

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void updateStockDisplay() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        Map<TypeRessource, Ressources> stockProduits = usine.getStockProduits();
    
        for (Recette recette : player.getLivreDeRecette().getRecettesDisponibles()) {
            TypeRessource produit = recette.getProduit();
            Ressources ressource = stockProduits.get(produit);
            if (ressource != null) {
                model.addRow(new Object[]{recette.getNom(), ressource.getQuantite()});
            }
        }
    
        stockTable.setModel(model);
    }

    private JPanel createRecettePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Ingrédients Recette",
            0, 0, panel.getFont().deriveFont(16f)
        ));
        panel.setBackground(new Color(170, 255, 170)); // Vert pastel clair pour le panel

        recetteTable = new JTable();
        updateRecetteIngredientsDisplay();

        JButton addToUsineButton = new JButton("Ajouter à l'Usine");
        styleButton(addToUsineButton);
        addToUsineButton.addActionListener(e -> addIngredientsToUsine());

        panel.add(new JScrollPane(recetteTable), BorderLayout.CENTER);
        panel.add(addToUsineButton, BorderLayout.SOUTH);

        return panel;
    }

    private void updateRecetteIngredientsDisplay() {
        if (recetteActuelle != null) {
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Ingrédient", "Requis", "Disponible", "Manquant"}, 0);
            for (Map.Entry<TypeRessource, Integer> ingredient : recetteActuelle.getIngredients().entrySet()) {
                int disponible = usine.getInventaire().getQuantiteRessource(ingredient.getKey());
                int manquant = Math.max(0, ingredient.getValue() - disponible);
                model.addRow(new Object[]{ingredient.getKey().name(), ingredient.getValue(), disponible, manquant});
            }
            recetteTable.setModel(model);
        }
    }

    private void addIngredientsToUsine() {
        if (recetteActuelle != null) {
            boolean transfertReussi = this.player.getCpJoueur().transfererRessourcesVersUsine(this.usine, recetteActuelle);
            if (transfertReussi) {
                updateDisplay();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une recette.", "Erreur", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(170, 255, 170)); // Vert pastel clair pour le panel
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JButton craftButton = new JButton("Produire");
        styleButton(craftButton);
        craftButton.addActionListener(e -> {
            if (canCraft()) {
                craft();
                updateDisplay();
            } else {
                JOptionPane.showMessageDialog(this, "Ressources insuffisantes pour produire.", "Erreur", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(craftButton);
        return panel;
    }

    private boolean canCraft() {
        return recetteActuelle != null && usine.getInventaire().hasSufficientIngredients(recetteActuelle);
    }

    private void craft() {
        if (recetteActuelle != null) {
            usine.getInventaire().craft(recetteActuelle);
            JOptionPane.showMessageDialog(this, "Production réussie !");
        }
    }

    private void startRefreshTimer() {
        Timer refreshTimer = new Timer(1000, e -> updateDisplay());
        refreshTimer.start();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(34, 139, 34)); // Vert pastel plus foncé
        button.setForeground(Color.WHITE); // Texte en blanc pour un meilleur contraste
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Police plus épaisse pour meilleure lisibilité
        button.setBorder(BorderFactory.createLineBorder(new Color(50, 205, 50))); // Bordure verte claire
    }

    
}
