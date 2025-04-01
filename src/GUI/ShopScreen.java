package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import Model.Joueur;
import Model.Machine;
import Model.Partie;
import Model.Recette;
import Model.TypeRessource;
import Model.Machines.Recolteuse;
import Model.Machines.Usine;
import geometry.Coordinates;

public class ShopScreen extends JFrame {
    private JPanel mainPanel;
    private JPanel bottomPanel;
    private Joueur player;
    private Partie partie;

    public ShopScreen(Joueur player, Partie partie) {
        this.player = player;
        this.partie = partie;
        setTitle("Marché");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel and layout
        mainPanel = new JPanel(new BorderLayout());
        initUI();

        add(mainPanel);
    }

    private void initUI() {
        mainPanel.setBackground(Color.DARK_GRAY);
    
        // Titre
        JLabel titleLabel = new JLabel("Bienvenue au Marché", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
    
        // Créer le SplitPane principal
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(500);  // Divise la fenêtre en deux sections égales
        mainSplitPane.setDividerSize(5);
        mainSplitPane.setResizeWeight(0.5);
    
        // Ajouter les sections Achat et Vente
        mainSplitPane.setLeftComponent(createBuyPanel());
        mainSplitPane.setRightComponent(createSellPanel());
    
        // Ajouter le SplitPane au panneau principal
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
    
        // Ajouter le panneau inférieur pour l'inventaire et le bouton de sortie
        bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.DARK_GRAY);
    
        JTextArea playerInventory = new JTextArea("Votre inventaire :\n- Or : " + player.getArgent().getValeur() + " pièces\n");
        playerInventory.setEditable(false);
        playerInventory.setBackground(Color.BLACK);
        playerInventory.setForeground(Color.WHITE);
        bottomPanel.add(playerInventory, BorderLayout.CENTER);
    
        JButton exitButton = new JButton("Quitter le Marché");
        exitButton.addActionListener(e -> dispose());
        bottomPanel.add(exitButton, BorderLayout.SOUTH);
    
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private JScrollPane createBuyPanel() {
        JPanel buyPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buyPanel.setBackground(Color.BLACK);

        // Ajouter des machines disponibles à l'achat
        addMachineItem(buyPanel, new Recolteuse(new Coordinates(0, 0), 100), 50);
        addMachineItem(buyPanel, new Usine(new Coordinates(0, 0), 100), 100);

        // Ajouter des recettes disponibles à l'achat
        for (Recette recette : partie.getLivreDeRecette().getRecettesDisponibles()) {
            addRecetteItem(buyPanel, recette);
        }

        JScrollPane buyScroll = new JScrollPane(buyPanel);
        buyScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Section Achat", 0, 0, new Font("SansSerif", Font.BOLD, 14), Color.WHITE));
        return buyScroll;
    }

    private JScrollPane createSellPanel() {
        JPanel sellPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        sellPanel.setBackground(Color.BLACK);

        // Ajouter des ressources disponibles à la vente
        addSellResourceItem(sellPanel, "Bois", TypeRessource.BOIS, 10);
        addSellResourceItem(sellPanel, "Pierre", TypeRessource.PIERRE, 15);
        addSellResourceItem(sellPanel, "Fer", TypeRessource.FER, 20);
        addSellResourceItem(sellPanel, "Or", TypeRessource.OR, 50);

        JScrollPane sellScroll = new JScrollPane(sellPanel);
        sellScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Section Vente", 0, 0, new Font("SansSerif", Font.BOLD, 14), Color.WHITE));
        return sellScroll;
    }

    private void updatePlayerInventory() {
        JTextArea playerInventory = new JTextArea("Votre inventaire :\n- Or : " + player.getArgent().getValeur() + " pièces\n");
        playerInventory.setEditable(false);
        playerInventory.setBackground(Color.BLACK);
        playerInventory.setForeground(Color.WHITE);

        bottomPanel.removeAll();
        bottomPanel.add(playerInventory, BorderLayout.CENTER);
        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private void addMachineItem(JPanel machinesPanel, Machine machine, int price) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        itemPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(machine.getClass().getSimpleName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        JLabel priceLabel = new JLabel("Prix : " + price + " pièces", SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        priceLabel.setForeground(Color.YELLOW);

        JButton buyButton = new JButton("Acheter Machine");
        buyButton.addActionListener(e -> {
            if (player.getArgent().getValeur() >= price) {
                player.getArgent().retirer(price);
                player.ajouterMachine(machine);
                JOptionPane.showMessageDialog(this, "Vous avez acheté une machine !", "Machine achetée", JOptionPane.INFORMATION_MESSAGE);
                updatePlayerInventory();
            } else {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas assez d'or pour acheter cette machine.", "Or insuffisant", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(priceLabel, BorderLayout.CENTER);
        itemPanel.add(buyButton, BorderLayout.SOUTH);

        machinesPanel.add(itemPanel);
    }

    private void addRecetteItem(JPanel recettesPanel, Recette recette) {
        // Vérifier si la recette a déjà été achetée
        if (player.getLivreDeRecette().getRecettesDisponibles().contains(recette)) {
            return;  // Si la recette a déjà été achetée, ne pas l'afficher
        }
    
        JPanel recettePanel = new JPanel(new BorderLayout());
        recettePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        recettePanel.setOpaque(false);
    
        JLabel nameLabel = new JLabel(recette.getNom(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
    
        // Affichage du prix de la recette
        JLabel priceLabel = new JLabel("Prix: " + recette.getPrix() + " pièces", SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        priceLabel.setForeground(Color.YELLOW);
    
        JLabel statusLabel = new JLabel("Achetez cette recette pour l'ajouter à votre livre.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        statusLabel.setForeground(Color.GREEN);
    
        // Bouton pour acheter la recette
        JButton buyButton = new JButton("Acheter Recette");
        buyButton.addActionListener(e -> {
            // Vérifier si le joueur a suffisamment d'or
            if (player.getArgent().getValeur() >= recette.getPrix()) {
                // Déduire le prix de l'or du joueur
                player.getArgent().retirer(recette.getPrix());
    
                // Ajouter la recette au livre du joueur
                Map<TypeRessource, Integer> ingredients = recette.getIngredients();
                TypeRessource productType = recette.getProduit();
                int productQuantity = recette.getQuantiteProduit();
                int duration = recette.getDuree();
    
                player.getLivreDeRecette().ajouterRecette(new Recette(recette.getNom(), ingredients, productType, productQuantity, duration, recette.getPrix()));
    
                // Mise à jour de l'or et de l'affichage
                updatePlayerInventory();
    
                // Supprimer la recette de la liste disponible pour l'achat
                recettesPanel.remove(recettePanel);
                recettesPanel.revalidate();
                recettesPanel.repaint();
    
                JOptionPane.showMessageDialog(this, "Vous avez ajouté une nouvelle recette à votre livre !", "Recette ajoutée", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Si le joueur n'a pas assez d'or
                JOptionPane.showMessageDialog(this, "Vous n'avez pas assez d'or pour acheter cette recette.", "Or insuffisant", JOptionPane.WARNING_MESSAGE);
            }
        });
    
        recettePanel.add(nameLabel, BorderLayout.NORTH);
        recettePanel.add(priceLabel, BorderLayout.CENTER);  // Ajout du prix dans le panneau
        recettePanel.add(statusLabel, BorderLayout.SOUTH);
        recettePanel.add(buyButton, BorderLayout.EAST);
    
        recettesPanel.add(recettePanel);
    }

    private void addSellResourceItem(JPanel sellPanel, String resourceName, TypeRessource resourceType, int sellPrice) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        itemPanel.setOpaque(false);
    
        JLabel nameLabel = new JLabel(resourceName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);
    
        JLabel priceLabel = new JLabel("Prix de vente : " + sellPrice + " pièces", SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        priceLabel.setForeground(Color.YELLOW);
    
        JLabel quantityLabel = new JLabel("Quantité : " + player.getInv().getQuantiteRessource(resourceType), SwingConstants.CENTER);
        quantityLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        quantityLabel.setForeground(Color.LIGHT_GRAY);
    
        JButton sellButton = new JButton("Vendre");
        sellButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Entrez la quantité à vendre :", "Vendre " + resourceName, JOptionPane.PLAIN_MESSAGE);
            if (input != null) {
                try {
                    int quantityToSell = Integer.parseInt(input);
                    int availableQuantity = player.getInv().getQuantiteRessource(resourceType);
                    if (quantityToSell > 0 && quantityToSell <= availableQuantity) {
                        int totalEarnings = quantityToSell * sellPrice;
                        player.getInv().removeRessource(resourceType, quantityToSell);
                        player.getArgent().ajouter(totalEarnings);
    
                        updatePlayerInventory(); // Met à jour l'inventaire du joueur
                        quantityLabel.setText("Quantité : " + player.getInv().getQuantiteRessource(resourceType));
    
                        JOptionPane.showMessageDialog(this, "Vous avez vendu " + quantityToSell + " " + resourceName + " pour " + totalEarnings + " pièces.", "Vente réussie", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Quantité invalide ou insuffisante.", "Erreur de vente", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(priceLabel, BorderLayout.CENTER);
        itemPanel.add(quantityLabel, BorderLayout.SOUTH);
        itemPanel.add(sellButton, BorderLayout.EAST);
    
        sellPanel.add(itemPanel);
    }

    private void addSellItem(JPanel itemsPanel, String resourceName, TypeRessource resourceType, int availableQuantity, int sellPrice) {
        final int[] quantity = {availableQuantity};

        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        itemPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(resourceName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(Color.WHITE);

        JLabel quantityLabel = new JLabel("Quantité disponible : " + quantity[0], SwingConstants.CENTER);
        quantityLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        quantityLabel.setForeground(Color.LIGHT_GRAY);

        JLabel priceLabel = new JLabel("Prix d'achat : " + sellPrice + " pièces", SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        priceLabel.setForeground(Color.LIGHT_GRAY);

        JButton buyButton = new JButton("Acheter");
        buyButton.addActionListener(e -> {
            if (quantity[0] > 0) {
                quantity[0]--;
                player.getInv().addRessource(resourceType, 1);
                updatePlayerInventory();
                JOptionPane.showMessageDialog(this, "Vous avez acheté " + resourceName + " pour " + sellPrice + " pièces.", "Achat effectué", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Stock épuisé pour " + resourceName, "Stock épuisé", JOptionPane.WARNING_MESSAGE);
            }
        });

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(quantityLabel, BorderLayout.CENTER);
        itemPanel.add(priceLabel, BorderLayout.SOUTH);
        itemPanel.add(buyButton, BorderLayout.SOUTH);

        itemsPanel.add(itemPanel);
    }

    
}