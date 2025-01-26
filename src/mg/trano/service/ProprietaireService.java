package mg.trano.service;

import mg.trano.model.Proprietaire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProprietaireService {

    public List<Proprietaire> getAllProprietaires(Connection connection) throws SQLException {
        String sql = "SELECT id, nom FROM proprietaire";
        List<Proprietaire> proprietaires = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                proprietaires.add(new Proprietaire(id, nom));
            }
        }

        return proprietaires;
    }

    public Proprietaire getProprietaireById(Connection connection, int id) throws SQLException {
        String sql = "SELECT id, nom FROM proprietaire WHERE id = ?";
        Proprietaire proprietaire = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    proprietaire = new Proprietaire(id, nom);
                }
            }
        }

        return proprietaire;
    }
}