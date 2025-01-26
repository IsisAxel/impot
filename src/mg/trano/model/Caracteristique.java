package mg.trano.model;

public class Caracteristique {
    private int id;
    private String nom;
    private TypeCaracteristique typeCaracteristique;
    private double coefficient;

    public Caracteristique() {
    }

    public Caracteristique(int id, String nom, TypeCaracteristique typeCaracteristique, double coefficient) {
        this.id = id;
        this.nom = nom;
        this.typeCaracteristique = typeCaracteristique;
        this.coefficient = coefficient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeCaracteristique getTypeCaracteristique() {
        return typeCaracteristique;
    }

    public void setTypeCaracteristique(TypeCaracteristique typeCaracteristique) {
        this.typeCaracteristique = typeCaracteristique;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }
}