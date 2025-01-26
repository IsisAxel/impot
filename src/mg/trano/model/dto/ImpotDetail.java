package mg.trano.model.dto;

import java.math.BigDecimal;

public class ImpotDetail {
    private int mois;
    private BigDecimal montantPaye;
    private BigDecimal montantTotal;
    private BigDecimal coefficient;
    public ImpotDetail() {
    }
    public ImpotDetail(int mois, BigDecimal montantPaye, BigDecimal montantTotal, BigDecimal coefficient) {
        this.mois = mois;
        this.montantPaye = montantPaye;
        this.montantTotal = montantTotal;
        this.coefficient = coefficient;
    }
    public int getMois() {
        return mois;
    }
    public void setMois(int mois) {
        this.mois = mois;
    }
    public BigDecimal getMontantPaye() {
        return montantPaye;
    }
    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }
    public BigDecimal getMontantTotal() {
        return montantTotal;
    }
    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }
    public BigDecimal getCoefficient() {
        return coefficient;
    }
    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }
}
