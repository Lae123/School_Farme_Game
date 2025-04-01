package geometry;

import java.io.Serializable;
import java.util.Objects;

public class Coordinates implements Serializable{
    private final double x;
    private final double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    // Méthode d'interpolation linéaire
    public Coordinates interpolate(Coordinates target, double progress) {
        double newX = this.x + (target.x - this.x) * progress;
        double newY = this.y + (target.y - this.y) * progress;
        return new Coordinates(newX, newY);
    }

    // Méthode pour additionner des coordonnées (utile pour les déplacements directionnels)
    public Coordinates add(Coordinates other) {
        return new Coordinates(this.x + other.x, this.y + other.y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Coordinates)) return false;
        Coordinates other = (Coordinates) obj;
        // Utiliser une petite marge d'erreur pour la comparaison des doubles
        return Math.abs(this.x - other.x()) < 0.0001 && 
               Math.abs(this.y - other.y()) < 0.0001;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
