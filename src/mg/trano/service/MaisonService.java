package mg.trano.service;

import mg.trano.model.Maison;
import mg.trano.model.Proprietaire;
import mg.trano.model.Arrondissement;
import mg.trano.model.Caracteristique;
import mg.trano.model.Commune;
import mg.trano.model.TypeCaracteristique;
import mg.trano.model.dto.ImpotDetail;
import mg.trano.model.dto.MaisonImpot;
import mg.trano.model.Coordonne;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaisonService {

    public static final double impotParMettreCarre = 3000.0; 

    public void insertMaison(Connection connection, Maison maison, Timestamp date) throws SQLException {
        String insertMaisonSQL = "INSERT INTO maison (nom, id_commune, geom) VALUES (?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326)) RETURNING id";
        String insertEtatMaisonSQL = "INSERT INTO etat_maison (id_proprietaire, id_maison, longueur, largeur, etage, date) VALUES (?, ?, ?, ?, ?, ?)";
        String insertMaisonCaracteristiqueSQL = "INSERT INTO maison_caracteristique (id_maison, id_caracteristique, id_type_caracteristique, date) VALUES (?, ?, ?, ?)";

        try (
            PreparedStatement maisonStmt = connection.prepareStatement(insertMaisonSQL, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement etatMaisonStmt = connection.prepareStatement(insertEtatMaisonSQL);
            PreparedStatement maisonCaracteristiqueStmt = connection.prepareStatement(insertMaisonCaracteristiqueSQL)
        ) {
            maisonStmt.setString(1, maison.getNom());
            maisonStmt.setInt(2, maison.getCommune().getId());
            maisonStmt.setDouble(3, maison.getGeom().getLongitude());
            maisonStmt.setDouble(4, maison.getGeom().getLatitude());
            maisonStmt.executeUpdate();

            try (ResultSet rs = maisonStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int maisonId = rs.getInt(1);

                    etatMaisonStmt.setInt(1, maison.getProprietaire().getId());
                    etatMaisonStmt.setInt(2, maisonId);
                    etatMaisonStmt.setDouble(3, maison.getLongueur());
                    etatMaisonStmt.setDouble(4, maison.getLargeur());
                    etatMaisonStmt.setInt(5, maison.getEtage());
                    etatMaisonStmt.setTimestamp(6, date);
                    etatMaisonStmt.executeUpdate();

                    for (Caracteristique caracteristique : maison.getCaracteristiques()) {
                        CaracteristiqueService caracteristiqueService = new CaracteristiqueService();
                        Caracteristique cr = caracteristiqueService.getCaracteristiqueById(connection, caracteristique.getId());

                        if (cr != null) {
                            maisonCaracteristiqueStmt.setInt(1, maisonId);
                            maisonCaracteristiqueStmt.setInt(2, cr.getId());
                            maisonCaracteristiqueStmt.setInt(3, cr.getTypeCaracteristique().getId());
                            maisonCaracteristiqueStmt.setTimestamp(4, date);
                            maisonCaracteristiqueStmt.addBatch();
                        }
                    }
                    maisonCaracteristiqueStmt.executeBatch();
                } else {
                    throw new SQLException("Failed to retrieve generated key for maison.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<Maison> getAllMaisons(Connection connection, Timestamp date) throws SQLException {
        String maisonSql = "SELECT m.id, m.nom, m.id_commune, ST_X(m.geom) AS longitude, ST_Y(m.geom) AS latitude " +
                        "FROM maison m";

        String etatMaisonSql = "SELECT em.id_proprietaire, em.longueur, em.largeur, em.etage, em.date " +
                            "FROM etat_maison em " +
                            "WHERE em.id_maison = ? AND em.date <= ? " +
                            "ORDER BY em.date DESC LIMIT 1";

        String caracteristiqueSql = "SELECT c.id, c.nom, c.id_type_caracteristique, c.coefficient, " +
                                    "tc.id AS tc_id, tc.nom AS tc_nom " +
                                    "FROM caracteristique c " +
                                    "JOIN maison_caracteristique mc ON c.id = mc.id_caracteristique " +
                                    "JOIN type_caracteristique tc ON c.id_type_caracteristique = tc.id " +
                                    "WHERE mc.id_maison = ? AND mc.date <= ?";

        List<Maison> maisons = new ArrayList<>();

        try (PreparedStatement maisonPstmt = connection.prepareStatement(maisonSql);
            PreparedStatement etatMaisonPstmt = connection.prepareStatement(etatMaisonSql);
            PreparedStatement caracteristiquePstmt = connection.prepareStatement(caracteristiqueSql)) {

            try (ResultSet maisonRs = maisonPstmt.executeQuery()) {
                while (maisonRs.next()) {
                    int maisonId = maisonRs.getInt("id");
                    String nom = maisonRs.getString("nom");
                    int communeId = maisonRs.getInt("id_commune");
                    double longitude = maisonRs.getDouble("longitude");
                    double latitude = maisonRs.getDouble("latitude");
                    Coordonne geom = new Coordonne(longitude, latitude);

                    etatMaisonPstmt.setInt(1, maisonId);
                    etatMaisonPstmt.setTimestamp(2, date);
                    double longueur = 0;
                    double largeur = 0;
                    int etage = 0;
                    int proprietaireId = 0;

                    try (ResultSet etatMaisonRs = etatMaisonPstmt.executeQuery()) {
                        if (etatMaisonRs.next()) {
                            longueur = etatMaisonRs.getDouble("longueur");
                            largeur = etatMaisonRs.getDouble("largeur");
                            etage = etatMaisonRs.getInt("etage");
                            proprietaireId = etatMaisonRs.getInt("id_proprietaire");
                        }
                    }

                    List<Caracteristique> caracteristiques = new ArrayList<>();
                    caracteristiquePstmt.setInt(1, maisonId);
                    caracteristiquePstmt.setTimestamp(2, date);

                    try (ResultSet caracteristiqueRs = caracteristiquePstmt.executeQuery()) {
                        while (caracteristiqueRs.next()) {
                            int caracteristiqueId = caracteristiqueRs.getInt("id");
                            String caracteristiqueNom = caracteristiqueRs.getString("nom");
                            double coefficient = caracteristiqueRs.getDouble("coefficient");

                            int typeCaracteristiqueId = caracteristiqueRs.getInt("tc_id");
                            String typeCaracteristiqueNom = caracteristiqueRs.getString("tc_nom");
                            TypeCaracteristique typeCaracteristique = new TypeCaracteristique(typeCaracteristiqueId, typeCaracteristiqueNom);

                            Caracteristique caracteristique = new Caracteristique(caracteristiqueId, caracteristiqueNom, typeCaracteristique, coefficient);
                            caracteristiques.add(caracteristique);
                        }
                    }

                    CommuneService communeService = new CommuneService();
                    Commune commune = communeService.getCommuneByDate(connection, communeId, date);

                    ProprietaireService proprietaireService = new ProprietaireService();
                    Proprietaire proprietaire = proprietaireService.getProprietaireById(connection, proprietaireId);

                    Maison maison = new Maison(maisonId, nom, longueur, largeur, caracteristiques, geom, etage, commune, proprietaire);
                    maisons.add(maison);
                }
            }
        }

        return maisons;
    }


    public Maison getMaisonById(Connection connection, int maisonId, Timestamp date) throws SQLException {
        String maisonSql = "SELECT m.id, m.nom, m.id_commune, ST_X(m.geom) AS longitude, ST_Y(m.geom) AS latitude " +
                           "FROM maison m WHERE m.id = ?";
    
        String etatMaisonSql = "SELECT em.id_proprietaire, em.longueur, em.largeur, em.etage, em.date " +
                               "FROM etat_maison em " +
                               "WHERE em.id_maison = ? AND em.date <= ? " +
                               "ORDER BY em.date DESC LIMIT 1";
    
        String caracteristiqueSql = "SELECT c.id, c.nom, c.id_type_caracteristique, c.coefficient, " +
                                    "tc.id AS tc_id, tc.nom AS tc_nom " +
                                    "FROM caracteristique c " +
                                    "JOIN maison_caracteristique mc ON c.id = mc.id_caracteristique " +
                                    "JOIN type_caracteristique tc ON c.id_type_caracteristique = tc.id " +
                                    "WHERE mc.id_maison = ? AND mc.date <= ?";
    
        try (PreparedStatement maisonPstmt = connection.prepareStatement(maisonSql);
             PreparedStatement etatMaisonPstmt = connection.prepareStatement(etatMaisonSql);
             PreparedStatement caracteristiquePstmt = connection.prepareStatement(caracteristiqueSql)) {
    
            maisonPstmt.setInt(1, maisonId);
            Maison maison = null;
    
            try (ResultSet maisonRs = maisonPstmt.executeQuery()) {
                if (maisonRs.next()) {
                    String nom = maisonRs.getString("nom");
                    int communeId = maisonRs.getInt("id_commune");
                    double longitude = maisonRs.getDouble("longitude");
                    double latitude = maisonRs.getDouble("latitude");
                    Coordonne geom = new Coordonne(longitude, latitude);
    
                    etatMaisonPstmt.setInt(1, maisonId);
                    etatMaisonPstmt.setTimestamp(2, date);
                    double longueur = 0;
                    double largeur = 0;
                    int etage = 0;
                    int proprietaireId = 0;
    
                    try (ResultSet etatMaisonRs = etatMaisonPstmt.executeQuery()) {
                        if (etatMaisonRs.next()) {
                            longueur = etatMaisonRs.getDouble("longueur");
                            largeur = etatMaisonRs.getDouble("largeur");
                            etage = etatMaisonRs.getInt("etage");
                            proprietaireId = etatMaisonRs.getInt("id_proprietaire");
                        }
                    }
    
                    List<Caracteristique> caracteristiques = new ArrayList<>();
                    caracteristiquePstmt.setInt(1, maisonId);
                    caracteristiquePstmt.setTimestamp(2, date);
    
                    try (ResultSet caracteristiqueRs = caracteristiquePstmt.executeQuery()) {
                        while (caracteristiqueRs.next()) {
                            int caracteristiqueId = caracteristiqueRs.getInt("id");
                            String caracteristiqueNom = caracteristiqueRs.getString("nom");
                            double coefficient = caracteristiqueRs.getDouble("coefficient");
    
                            int typeCaracteristiqueId = caracteristiqueRs.getInt("tc_id");
                            String typeCaracteristiqueNom = caracteristiqueRs.getString("tc_nom");
                            TypeCaracteristique typeCaracteristique = new TypeCaracteristique(typeCaracteristiqueId, typeCaracteristiqueNom);
    
                            Caracteristique caracteristique = new Caracteristique(caracteristiqueId, caracteristiqueNom, typeCaracteristique, coefficient);
                            caracteristiques.add(caracteristique);
                        }
                    }
    
                    CommuneService communeService = new CommuneService();
                    Commune commune = communeService.getCommuneByDate(connection, communeId, date);
    
                    ProprietaireService proprietaireService = new ProprietaireService();
                    Proprietaire proprietaire = proprietaireService.getProprietaireById(connection, proprietaireId);
    
                    maison = new Maison(maisonId, nom, longueur, largeur, caracteristiques, geom, etage, commune, proprietaire);
                }
            }
            return maison;
        }
    }

    public void payerImpot(Connection connection, int maisonId, int annee, int mois) throws SQLException {
        String sql = "INSERT INTO paiement_impot (id_maison, annee, mois) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, maisonId);
            pstmt.setInt(2, annee);
            pstmt.setInt(3, mois);
            pstmt.executeUpdate();
        }
    }

    public boolean[] getAllImpotByYear(Connection connection, int maisonId, int annee) throws SQLException {
        String sql = "SELECT mois.mois, (paiement_impot.id IS NOT NULL) AS paye " +
                     "FROM generate_series(1, 12) AS mois(mois) " +
                     "LEFT JOIN paiement_impot ON paiement_impot.mois = mois.mois AND paiement_impot.id_maison = ? AND paiement_impot.annee = ?";
        boolean[] impots = new boolean[12];
    
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, maisonId);
            pstmt.setInt(2, annee);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int mois = rs.getInt("mois");
                    boolean paye = rs.getBoolean("paye");
                    impots[mois - 1] = paye;
                }
            }
        }
    
        return impots;
        }

    public List<ImpotDetail> getMonthlyImpotValuesByYear(Connection connection, int maisonId, int annee) throws SQLException {
        String sql = "SELECT mois.mois, (paiement_impot.id IS NOT NULL) AS paye " +
                 "FROM generate_series(1, 12) AS mois(mois) " +
                 "LEFT JOIN paiement_impot ON paiement_impot.mois = mois.mois AND paiement_impot.id_maison = ? AND paiement_impot.annee = ?";
        List<ImpotDetail> impots = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, maisonId);
            pstmt.setInt(2, annee);
            int mois = 1;
            try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                boolean paye = rs.getBoolean("paye");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, annee);
                calendar.set(Calendar.MONTH, mois - 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0); 
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
                Maison m = getMaisonById(connection, maisonId, timestamp);
                BigDecimal montant = m.getImpot();
                BigDecimal produitCoefficients = m.getProduitCoefficients();
                ImpotDetail impotDetail = new ImpotDetail(mois, paye ? montant : BigDecimal.ZERO, montant, produitCoefficients);
                impots.add(impotDetail);
                mois++;
            }
            }
        }

        return impots;
    }

    public BigDecimal[] getAllImpotByYearAndArrondissement(Connection connection, int arrondissementId, int annee) throws SQLException {
        BigDecimal[] impots = new BigDecimal[2]; // [0] = total payé, [1] = total à payer
        impots[0] = BigDecimal.ZERO;
        impots[1] = BigDecimal.ZERO;
        Arrondissement arrondissement = new ArrondissementService().getById(connection, arrondissementId);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, annee);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        List<Maison> maisons = getAllMaisons(connection,timestamp);
        List<Maison> maisonsInArrondissement = new ArrayList<>();
        for (Maison maison : maisons) {
            if (maison.isIn(arrondissement)) {
                maisonsInArrondissement.add(maison);
            }
        }
        for (Maison maison : maisonsInArrondissement) {
            boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
            for (boolean paye : impotsMaison) {
                if (paye) {
                    impots[0] = impots[0].add(maison.getImpot());
                }
                impots[1] = impots[1].add(maison.getImpot());
            }
        }

        return impots;
    }

    public BigDecimal[] getAllImpotByYearAndCommune(Connection connection, int communeId, int annee) throws SQLException {
        BigDecimal[] impots = new BigDecimal[2]; // [0] = total payé, [1] = total à payer
        impots[0] = BigDecimal.ZERO;
        impots[1] = BigDecimal.ZERO;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, annee);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        List<Maison> maisons = getAllMaisons(connection,timestamp);
        List<Maison> maisonsInCommune = new ArrayList<>();
        for (Maison maison : maisons) {
            if (maison.getCommune().getId() == communeId) {
                maisonsInCommune.add(maison);
            }
        }
        for (Maison maison : maisonsInCommune) {
            boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
            for (boolean paye : impotsMaison) {
                if (paye) {
                    impots[0] = impots[0].add(maison.getImpot());
                }
                impots[1] = impots[1].add(maison.getImpot());
            }
        }

        return impots;
    }

    public BigDecimal[] getAllImpotByYearAndProprietaire(Connection connection, int proprietaireId, int annee) throws SQLException {
        BigDecimal[] impots = new BigDecimal[2]; // [0] = total payé, [1] = total à payer
        impots[0] = BigDecimal.ZERO;
        impots[1] = BigDecimal.ZERO;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, annee);
        calendar.set(Calendar.MONTH,11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
        List<Maison> maisons = getAllMaisons(connection,timestamp);
        List<Maison> maisonsInCommune = new ArrayList<>();
        for (Maison maison : maisons) {
            if (maison.getProprietaire().getId() == proprietaireId) {
                maisonsInCommune.add(maison);
            }
        }
        for (Maison maison : maisonsInCommune) {
            boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
            for (boolean paye : impotsMaison) {
                if (paye) {
                    impots[0] = impots[0].add(maison.getImpot());
                }
                impots[1] = impots[1].add(maison.getImpot());
            }
        }

        return impots;
    }

    public Map<String, List<MaisonImpot>> getMonthlyImpotDetailsByYearAndArrondissement(Connection connection, int arrondissementId, int annee) throws SQLException {
        Map<String, List<MaisonImpot>> monthlyImpotDetails = new HashMap<>();
        Arrondissement arrondissement = new ArrondissementService().getById(connection, arrondissementId);

        for (int mois = 1; mois <= 12; mois++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, annee);
            calendar.set(Calendar.MONTH, mois - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            List<Maison> maisons = getAllMaisons(connection, timestamp);
            List<MaisonImpot> maisonImpots = new ArrayList<>();

            for (Maison maison : maisons) {
                if (maison.isIn(arrondissement)) {
                    boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
                    BigDecimal montant = maison.getImpot();
                    MaisonImpot maisonImpot = new MaisonImpot(maison, impotsMaison[mois - 1], montant);
                    maisonImpots.add(maisonImpot);
                }
            }

            String monthName = new DateFormatSymbols().getMonths()[mois - 1];
            monthlyImpotDetails.put(monthName, maisonImpots);
        }

        return monthlyImpotDetails;
    }

    public Map<String, List<MaisonImpot>> getMonthlyImpotDetailsByYearAndCommune(Connection connection, int communeId, int annee) throws SQLException {
        Map<String, List<MaisonImpot>> monthlyImpotDetails = new HashMap<>();

        for (int mois = 1; mois <= 12; mois++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, annee);
            calendar.set(Calendar.MONTH, mois - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

            List<Maison> maisons = getAllMaisons(connection, timestamp);
            List<MaisonImpot> maisonImpots = new ArrayList<>();

            for (Maison maison : maisons) {
                if (maison.getCommune().getId() == communeId) {
                    boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
                    BigDecimal montant = maison.getImpot();
                    MaisonImpot maisonImpot = new MaisonImpot(maison, impotsMaison[mois - 1], montant);
                    maisonImpots.add(maisonImpot);
                }
            }

            String monthName = new DateFormatSymbols().getMonths()[mois - 1];
            monthlyImpotDetails.put(monthName, maisonImpots);
        }

        return monthlyImpotDetails;
    }

    public Map<String, List<MaisonImpot>> getMonthlyImpotDetailsByYearAndProprietaire(Connection connection, int proprietaireId, int annee) throws SQLException {
        Map<String, List<MaisonImpot>> monthlyImpotDetails = new HashMap<>();
    
        for (int mois = 1; mois <= 12; mois++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, annee);
            calendar.set(Calendar.MONTH, mois - 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
    
            List<Maison> maisons = getAllMaisons(connection, timestamp);
            List<MaisonImpot> maisonImpots = new ArrayList<>();
    
            for (Maison maison : maisons) {
                if (maison.getProprietaire().getId() == proprietaireId) {
                    boolean[] impotsMaison = getAllImpotByYear(connection, maison.getId(), annee);
                    BigDecimal montant = maison.getImpot();
                    MaisonImpot maisonImpot = new MaisonImpot(maison, impotsMaison[mois - 1], montant);
                    maisonImpots.add(maisonImpot);
                }
            }
    
            String monthName = new DateFormatSymbols().getMonths()[mois - 1];
            monthlyImpotDetails.put(monthName, maisonImpots);
        }
    
        return monthlyImpotDetails;
    }

    public void updateEtat(Connection connection, Maison maison, Timestamp date) throws SQLException {
        String insertEtatMaisonSQL = "INSERT INTO etat_maison (id_proprietaire, id_maison, longueur, largeur, etage, date) VALUES (?, ?, ?, ?, ?, ?)";
        String insertMaisonCaracteristiqueSQL = "INSERT INTO maison_caracteristique (id_maison, id_caracteristique, id_type_caracteristique, date) VALUES (?, ?, ?, ?)";
    
        try (PreparedStatement insertEtatMaisonStmt = connection.prepareStatement(insertEtatMaisonSQL);
             PreparedStatement insertMaisonCaracteristiqueStmt = connection.prepareStatement(insertMaisonCaracteristiqueSQL)) {
    
            insertEtatMaisonStmt.setInt(1, maison.getProprietaire().getId());
            insertEtatMaisonStmt.setInt(2, maison.getId());
            insertEtatMaisonStmt.setDouble(3, maison.getLongueur());
            insertEtatMaisonStmt.setDouble(4, maison.getLargeur());
            insertEtatMaisonStmt.setInt(5, maison.getEtage());
            insertEtatMaisonStmt.setTimestamp(6, date);
            insertEtatMaisonStmt.executeUpdate();
    
            for (Caracteristique caracteristique : maison.getCaracteristiques()) {
                Caracteristique cr = new CaracteristiqueService().getCaracteristiqueById(connection, caracteristique.getId());
                insertMaisonCaracteristiqueStmt.setInt(1, maison.getId());
                insertMaisonCaracteristiqueStmt.setInt(2, cr.getId());
                insertMaisonCaracteristiqueStmt.setInt(3, cr.getTypeCaracteristique().getId());
                insertMaisonCaracteristiqueStmt.setTimestamp(4, date);
                insertMaisonCaracteristiqueStmt.addBatch();
            }
            insertMaisonCaracteristiqueStmt.executeBatch();
        }
    }
}