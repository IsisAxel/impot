package mg.trano.model;

import java.util.List;

public class Arrondissement {
    private int id;
    private String nom;
    private List<Coordonne> geom;

    public Arrondissement() {
    }

    public Arrondissement(int id, String nom, List<Coordonne> geom) {
        this.id = id;
        this.nom = nom;
        this.geom = geom;
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

    public List<Coordonne> getGeom() {
        return geom;
    }

    public void setGeom(List<Coordonne> geom) {
        this.geom = geom;
    }
}