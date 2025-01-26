package mg.trano.model;

import java.math.BigDecimal;
import java.util.List;

public class Maison {
    private int id;
    private String nom;
    private double longueur;
    private double largeur;
    private List<Caracteristique> caracteristiques;
    private Coordonne geom;
    private int etage;
    private Commune commune;
    private Proprietaire proprietaire;

    public Maison(int id, String nom, double longueur, double largeur, List<Caracteristique> caracteristiques,
                  Coordonne geom, int etage, Commune commune, Proprietaire proprietaire) {
        this.id = id;
        this.nom = nom;
        this.longueur = longueur;
        this.largeur = largeur;
        this.caracteristiques = caracteristiques;
        this.geom = geom;
        this.etage = etage;
        this.commune = commune;
        this.proprietaire = proprietaire;
    }

    public Maison() {
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

    public double getLongueur() {
        return longueur;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public double getLargeur() {
        return largeur;
    }

    public void setLargeur(double largeur) {
        this.largeur = largeur;
    }

    public List<Caracteristique> getCaracteristiques() {
        return caracteristiques;
    }

    public void setCaracteristiques(List<Caracteristique> caracteristiques) {
        this.caracteristiques = caracteristiques;
    }

    public Coordonne getGeom() {
        return geom;
    }

    public void setGeom(Coordonne geom) {
        this.geom = geom;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public Commune getCommune() {
        return commune;
    }

    public void setCommune(Commune commune) {
        this.commune = commune;
    }

    public Proprietaire getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
    }

    public boolean isIn(Arrondissement arrondissement) {
        List<Coordonne> geomsList = arrondissement.getGeom();
        return isPointInPolygon(getGeom(), geomsList);
    }

    private boolean isPointInPolygon(Coordonne point, List<Coordonne> polygon) {
        int i, j;
        boolean result = false;
        for (i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            if ((polygon.get(i).getLatitude() > point.getLatitude()) != (polygon.get(j).getLatitude() > point.getLatitude()) &&
                (point.getLongitude() < (polygon.get(j).getLongitude() - polygon.get(i).getLongitude()) * (point.getLatitude() - polygon.get(i).getLatitude()) / (polygon.get(j).getLatitude() - polygon.get(i).getLatitude()) + polygon.get(i).getLongitude())) {
                result = !result;
            }
        }
        return result;
    }

    public BigDecimal getSurface() {
        return BigDecimal.valueOf(getLongueur()).multiply(BigDecimal.valueOf(getLargeur()));
    }

    public BigDecimal getProduitCoefficients() {
        BigDecimal produit = BigDecimal.ONE;
        for (Caracteristique caracteristique : getCaracteristiques()) {
            produit = produit.multiply(BigDecimal.valueOf(caracteristique.getCoefficient()));
        }
        return produit;
    }

    public BigDecimal getImpot() {
        BigDecimal surface = getSurface();
        BigDecimal produitCoefficients = getProduitCoefficients();
        BigDecimal tarif = BigDecimal.valueOf(getCommune().getTarif());
        BigDecimal etageFactor = BigDecimal.valueOf(getEtage() + 1);
        return surface.multiply(produitCoefficients).multiply(tarif).multiply(etageFactor);
    }
}