package mg.trano.model.dto;

public class PayerImpotRequest {
    private int maisonId;
    private int annee;
    private int mois;

    public int getMaisonId() {
        return maisonId;
    }

    public void setMaisonId(int maisonId) {
        this.maisonId = maisonId;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getMois() {
        return mois;
    }

    public void setMois(int mois) {
        this.mois = mois;
    }
}