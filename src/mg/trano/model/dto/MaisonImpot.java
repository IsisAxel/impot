package mg.trano.model.dto;

import java.math.BigDecimal;

import mg.trano.model.Maison;

public class MaisonImpot {
    private Maison maison;
    private boolean paye;
    private BigDecimal montant;

    public MaisonImpot(Maison maison, boolean paye, BigDecimal montant) {
        this.maison = maison;
        this.paye = paye;
        this.montant = montant;
    }

    public Maison getMaison() {
        return maison;
    }

    public void setMaison(Maison maison) {
        this.maison = maison;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }
}