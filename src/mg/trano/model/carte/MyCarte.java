package mg.trano.model.carte;

import java.util.List;

import affichage.Carte;
import mg.trano.model.Arrondissement;
import mg.trano.model.Coordonne;
import mg.trano.model.Maison;

public class MyCarte extends Carte {
    public String drawMarker(List<Maison> liste){
        String retour="";
        if(liste!=null && liste.size()>0){
            retour+="<script>var longueur=marker.length;";
            for(int i=0;i<liste.size();i++){
                retour+="var greenIcon = L.icon({\n" +
                "    iconUrl: 'https://img.icons8.com/plasticine/344/bridge.png',\n" +
                "    iconSize:     [40, 40] \n" +
                "});";
                retour+="marker[longueur]=L.marker(["+liste.get(i).getGeom().getLatitude()+", "+liste.get(i).getGeom().getLongitude()+"], {icon: greenIcon}).addTo(map);";
                retour+="marker[longueur].bindPopup(\" "+liste.get(i).getNom()+" <a href='http://localhost:8080/trano/view/impot.html?maisonId="+ liste.get(i).getId() +"&annee=2024'>Impot</a>\").openPopup();";
            }
            retour+="</script>";
        }
        return retour;
    }

    public String drawStyledPolyline(Arrondissement arrondissement,String color){
        String retour="";
        List<Coordonne> liste=arrondissement.getGeom();
        if(liste!=null && liste.size()>0){
            retour="<script>var longueur1=polyline.length;"
                    + "polyline[longueur1] = L.polyline([";
            for(int i=0;i<liste.size();i++){
                retour+="["+liste.get(i).getLatitude()+","+liste.get(i).getLongitude()+"]";
                if(i!=liste.size()-1){
                    retour+=",";
                }
            }
            retour+="]).addTo(map);";
            if(color!=null && color.compareTo("")!=0){
                retour+="polyline[longueur1].setStyle({\n" +
                "    color: '"+color+"'\n" +
                "});";
            }
            retour+="</script>";
        }
        return retour;
    }

    public String drawPolygon(Arrondissement arrondissement){
        String retour="<script>";
        List<Coordonne> liste=arrondissement.getGeom();
        if(liste!=null && liste.size()>0){
            retour+="var polygon = L.polygon([";
            for(int i=0;i<liste.size();i++){
                retour+="["+liste.get(i).getLatitude()+","+liste.get(i).getLongitude()+"]";
                if(i!=liste.size()-1){
                    retour+=",";
                }
            }
            retour+="]).addTo(map);";
            retour+="polygon.bindPopup(\" "+arrondissement.getNom()+" <a href='http://localhost:8080/trano/view/arrondissement.html?arrondissementId="+arrondissement.getId()+"&annee=2024'>Impot</a> \").openPopup();";
        }
        retour+="</script>";
        return retour;
    }
}
