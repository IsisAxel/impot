package mg.trano.service;

import mg.trano.model.Commune;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CommuneService {

    public Commune getCommuneByDate(Connection connection, int id, Timestamp date) throws SQLException {
        String sql = "SELECT c.id, c.nom, ct.tarif " +
                     "FROM commune c " +
                     "JOIN commune_tarif ct ON c.id = ct.id_commune " +
                     "WHERE c.id = ? AND ct.date <= ? " +
                     "ORDER BY ct.date DESC " +
                     "LIMIT 1";


        Commune commune = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setTimestamp(2, date);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int communeId = rs.getInt("id");
                    String nom = rs.getString("nom");
                    double tarif = rs.getDouble("tarif");
                    commune = new Commune(communeId, nom, tarif);
                }
            }
        }

        return commune;
    }

    public List<Commune> getAllCommunes(Connection connection) throws SQLException {
        String sql = "SELECT id, nom FROM commune";
        List<Commune> communes = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                communes.add(new Commune(id, nom, 0)); // Tariff is not needed here
            }
        }

        return communes;
    }
}