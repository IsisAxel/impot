package mg.trano.service;

import mg.trano.model.TypeCaracteristique;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeCaracteristiqueService {
    public List<TypeCaracteristique> getAllTypeCaracteristiques(Connection connection) throws SQLException {
        String sql = "SELECT id, nom FROM type_caracteristique";
        List<TypeCaracteristique> typeCaracteristiques = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                TypeCaracteristique typeCaracteristique = new TypeCaracteristique(id, nom);
                typeCaracteristiques.add(typeCaracteristique);
            }
        }

        return typeCaracteristiques;
    }
}