package mg.trano.model.dto;

public class ArrondissementRequest {
    private int arrondissementId;
    private int annee;

    public int getArrondissementId() {
        return arrondissementId;
    }

    public void setArrondissementId(int arrondissement) {
        this.arrondissementId = arrondissement;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }
}