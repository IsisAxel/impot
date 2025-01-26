package mg.trano.model;

public class Commune {
    private int id;
    private String nom;
    private double tarif;

    public Commune() {
    }

    public Commune(int id, String nom, double tarif) {
        this.id = id;
        this.nom = nom;
        this.tarif = tarif;
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

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }
}