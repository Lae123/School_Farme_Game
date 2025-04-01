package Model;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import geometry.Coordinates;

public abstract class Machine implements Serializable {
    protected Coordinates position;
    protected Inventaire inventaire;
    protected int stockActuel;
    protected boolean active;
    private transient ScheduledExecutorService scheduler;

    public Machine(Coordinates position, int stockMax) {
        this.position = position;
        this.inventaire = new Inventaire(stockMax);
        this.stockActuel = this.inventaire.getStock();
        this.active = true;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Machine machine = (Machine) obj;
        return position.equals(machine.position); // Comparez en fonction des attributs pertinents, ici position
    }

    @Override
    public int hashCode() {
        return Objects.hash(position); // HashCode basé sur la position ou un autre critère
    }

    // Transférer les ressources vers le joueur
    public void transfererRessourcesVersJoueur(Inventaire inventaireJoueur) {
        if (inventaire.estVide()) {
            System.out.println("Machine : Aucune ressource à transférer.");
            return;
        }

        for (TypeRessource type : inventaire.getContenu().keySet()) {
            Ressources ressource = inventaire.getContenu().get(type);
            inventaireJoueur.ajouterRessource(ressource, ressource.getQuantite());
            ressource.retirerQuantite(ressource.getQuantite());
            System.out.println("Transféré : " + type + " x " + ressource.getQuantite());
        }
        System.out.println("Toutes les ressources ont été transférées au joueur.");
    }


    public abstract void reglage(MapG map);

    protected abstract void actionPeriodique(MapG map);

    // Gestion de l'action périodique
    public void startPeriodicHarvest(MapG map) {
        if (scheduler != null && !scheduler.isShutdown()) {
            return; // Déjà en cours
        }
        this.active = true;

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (active) {
                try {
                    actionPeriodique(map); 
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3, TimeUnit.SECONDS); // Exécution toutes les 3 secondes
    }

    private void stopPeriodicAction() {
        this.active = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // Méthode pour arrêter proprement le scheduler
    public void close() {
        stopPeriodicAction();
    }

}
