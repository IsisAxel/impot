package mg.trano.service;

import mg.trano.model.Arrondissement;
import mg.trano.model.Coordonne;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArrondissementService {

    public List<Arrondissement> getAllArrondissements(Connection connection) throws SQLException {
        String sql = "SELECT id, nom, ST_AsText(geom) AS geom FROM arrondissement";
        List<Arrondissement> arrondissements = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String geomText = rs.getString("geom");

                List<Coordonne> geom = parseGeom(geomText);

                Arrondissement arrondissement = new Arrondissement(id, nom, geom);
                arrondissements.add(arrondissement);
            }
        }

        return arrondissements;
    }

    public Arrondissement getById(Connection connection, int id) throws SQLException {
        String sql = "SELECT id, nom, ST_AsText(geom) AS geom FROM arrondissement WHERE id = ?";
        Arrondissement arrondissement = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    String geomText = rs.getString("geom");

                    List<Coordonne> geom = parseGeom(geomText);

                    arrondissement = new Arrondissement(id, nom, geom);
                }
            }
        }

        return arrondissement;
    }

    public void insertArrondissement(Connection connection, Arrondissement arrondissement) throws SQLException {
        String sql = "INSERT INTO arrondissement (nom, geom) VALUES (?, ST_GeomFromText(?, 4326))";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, arrondissement.getNom());
            pstmt.setString(2, formatGeom(arrondissement.getGeom()));
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    arrondissement.setId(rs.getInt(1));
                }
            }
        }
    }

    private List<Coordonne> parseGeom(String geomText) {
        List<Coordonne> coordonnes = new ArrayList<>();
        geomText = geomText.replace("POLYGON((", "").replace("))", "");
        String[] points = geomText.split(",");
        System.out.println(geomText);
        for (String point : points) {
            String[] coords = point.split(" ");
            double longitude = Double.parseDouble(coords[0]);
            double latitude = Double.parseDouble(coords[1]);
            coordonnes.add(new Coordonne(longitude, latitude));
        }
        return coordonnes;
    }

    private String formatGeom(List<Coordonne> geom) {
        StringBuilder sb = new StringBuilder("POLYGON((");
        for (int i = 0; i < geom.size(); i++) {
            Coordonne coordonne = geom.get(i);
            sb.append(coordonne.getLongitude()).append(" ").append(coordonne.getLatitude());
            if (i < geom.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("))");
        return sb.toString();
    }
}