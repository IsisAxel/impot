package mg.trano.service;

import mg.trano.model.Caracteristique;
import mg.trano.model.TypeCaracteristique;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CaracteristiqueService {

    public List<Caracteristique> getAllCaracteristiques(Connection connection) throws SQLException {
        String sql = "SELECT c.id, c.nom, c.id_type_caracteristique, c.coefficient, tc.id AS tc_id, tc.nom AS tc_nom " +
                     "FROM caracteristique c " +
                     "JOIN type_caracteristique tc ON c.id_type_caracteristique = tc.id";
        List<Caracteristique> caracteristiques = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double coefficient = rs.getDouble("coefficient");

                int typeCaracteristiqueId = rs.getInt("tc_id");
                String typeCaracteristiqueNom = rs.getString("tc_nom");
                TypeCaracteristique typeCaracteristique = new TypeCaracteristique(typeCaracteristiqueId, typeCaracteristiqueNom);

                Caracteristique caracteristique = new Caracteristique(id, nom, typeCaracteristique, coefficient);
                caracteristiques.add(caracteristique);
            }
        }

        return caracteristiques;
    }

    public Caracteristique getCaracteristiqueById(Connection connection, int id) throws SQLException {
        String sql = "SELECT c.id, c.nom, c.id_type_caracteristique, c.coefficient, tc.id AS tc_id, tc.nom AS tc_nom " +
                     "FROM caracteristique c " +
                     "JOIN type_caracteristique tc ON c.id_type_caracteristique = tc.id " +
                     "WHERE c.id = ?";
        Caracteristique caracteristique = null;
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    double coefficient = rs.getDouble("coefficient");
    
                    int typeCaracteristiqueId = rs.getInt("tc_id");
                    String typeCaracteristiqueNom = rs.getString("tc_nom");
                    TypeCaracteristique typeCaracteristique = new TypeCaracteristique(typeCaracteristiqueId, typeCaracteristiqueNom);
    
                    caracteristique = new Caracteristique(id, nom, typeCaracteristique, coefficient);
                }
            }
        }
    
        return caracteristique;
    }
}