package mg.trano.web;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mg.trano.model.Arrondissement;
import mg.trano.model.Caracteristique;
import mg.trano.model.Commune;
import mg.trano.model.Maison;
import mg.trano.model.Proprietaire;
import mg.trano.model.TypeCaracteristique;
import mg.trano.model.carte.MyCarte;
import mg.trano.model.dto.ArrondissementRequest;
import mg.trano.model.dto.ImpotDetail;
import mg.trano.model.dto.MaisonImpot;
import mg.trano.model.dto.PayerImpotRequest;
import mg.trano.service.ArrondissementService;
import mg.trano.service.CaracteristiqueService;
import mg.trano.service.CommuneService;
import mg.trano.service.MaisonService;
import mg.trano.service.ProprietaireService;
import mg.trano.service.TypeCaracteristiqueService;
import utilitaire.UtilDB;

@Path("/trano")
public class TranoRessource {
    @GET
    @Path("/caracteristiques")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Caracteristique> getAllCaracteristiques() {
        try (Connection connection = new UtilDB().GetConn()) {
            CaracteristiqueService service = new CaracteristiqueService();
            return service.getAllCaracteristiques(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch caracteristiques");
        }
    }

    @GET
    @Path("/typecaracteristiques")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TypeCaracteristique> getAllTypeCaracteristiques() {
        try (Connection connection = new UtilDB().GetConn()) {
            TypeCaracteristiqueService service = new TypeCaracteristiqueService();
            return service.getAllTypeCaracteristiques(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch type caracteristiques");
        }
    }
    
    @POST
    @Path("/maison")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertMaison(Maison maison) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            Timestamp currentTimestamp = Timestamp.from(Instant.now());
            service.insertMaison(connection, maison, currentTimestamp);
            return Response.ok(maison).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to insert maison").build();
        }
    }

    @GET
    @Path("/carte")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCarte() {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            List<Maison> maisons =  service.getAllMaisons(connection , Timestamp.from(Instant.now()));
            MyCarte carte = new MyCarte();
            String message = carte.getHtml();
            message += "\n" + carte.drawMarker(maisons);
            List<Arrondissement> arrondissements = new ArrondissementService().getAllArrondissements(connection);
            for (Arrondissement arrondissement : arrondissements) {
                message += "\n" + carte.drawPolygon(arrondissement);
            }
            return Response.ok(message).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch maisons").build(); 
        }
    }

    @GET
    @Path("/maisons")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllMaisons() {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            List<Maison> maisons = service.getAllMaisons(connection,Timestamp.from(Instant.now()));
            return Response.ok(maisons).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch maisons").build();
        }
    }

    @GET
    @Path("/arrondissements")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllArrondissements() {
        try (Connection connection = new UtilDB().GetConn()) {
            ArrondissementService service = new ArrondissementService();
            List<Arrondissement> arrondissements = service.getAllArrondissements(connection);
            return Response.ok(arrondissements).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch arrondissement").build();
        }
    }

    @POST
    @Path("/payerImpot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response payerImpot(PayerImpotRequest request) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            service.payerImpot(connection, request.getMaisonId(), request.getAnnee(), request.getMois());
            return Response.ok().build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to pay impot").build();
        }
    }

    @POST
    @Path("/arrondissement")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertArrondissement(Arrondissement arrondissement) {
        try (Connection connection = new UtilDB().GetConn()) {
            ArrondissementService service = new ArrondissementService();
            service.insertArrondissement(connection, arrondissement);
            return Response.ok(arrondissement).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to insert arrondissement").build();
        }
    }

    @POST
    @Path("/arrondissementImpot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImpotByArrondissementAndYear(ArrondissementRequest request) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            BigDecimal[] impots = service.getAllImpotByYearAndArrondissement(connection, request.getArrondissementId(), request.getAnnee());
            return Response.ok(impots).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch arrondissement impot").build();
        }
    }

    @PUT
    @Path("/maison/{id}/etat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEtat(@PathParam("id") int id, @QueryParam("timestamp") String timestampStr, Maison maison) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            maison.setId(id);
            Timestamp timestamp = Timestamp.valueOf(timestampStr.replace("T", " ") + ":00");
            service.updateEtat(connection, maison, timestamp);
            return Response.ok(maison).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update etat").build();
        }
    }

    @GET
    @Path("/maison/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMaisonById(@PathParam("id") int id) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            Timestamp currentTimestamp = Timestamp.from(Instant.now());
            return Response.ok(service.getMaisonById(connection, id, currentTimestamp)).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update etat").build();
        }
    }

    @GET
    @Path("/maison/{id}/impots/{annee}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyImpotValuesByYear(@PathParam("id") int maisonId, @PathParam("annee") int annee) {
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            List<ImpotDetail> impots = service.getMonthlyImpotValuesByYear(connection, maisonId, annee);
            return Response.ok(impots).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch impots").build();
        }
    }

    @GET
    @Path("/communes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCommunes() {
        try (Connection connection = new UtilDB().GetConn()) {
            CommuneService service = new CommuneService();
            List<Commune> communes = service.getAllCommunes(connection);
            return Response.ok(communes).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch communes").build();
        }
    }

    @POST
    @Path("/arrondissementImpotDetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyImpotDetailsByArrondissement(Map<String, Integer> params) {
        int arrondissementId = params.get("arrondissementId");
        int annee = params.get("annee");
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            Map<String, List<MaisonImpot>> impots = service.getMonthlyImpotDetailsByYearAndArrondissement(connection, arrondissementId, annee);
            return Response.ok(impots).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch impots").build();
        }
    }

    @POST
    @Path("/communeImpot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImpotByCommune(Map<String, Integer> params) {
        int communeId = params.get("communeId");
        int annee = params.get("annee");
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            BigDecimal[] impots = service.getAllImpotByYearAndCommune(connection, communeId, annee);
            return Response.ok(impots).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch impots").build();
        }
    }

    @POST
    @Path("/communeImpotDetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonthlyImpotDetailsByCommune(Map<String, Integer> params) {
        int communeId = params.get("communeId");
        int annee = params.get("annee");
        try (Connection connection = new UtilDB().GetConn()) {
            MaisonService service = new MaisonService();
            Map<String, List<MaisonImpot>> impots = service.getMonthlyImpotDetailsByYearAndCommune(connection, communeId, annee);
            return Response.ok(impots).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch impots").build();
        }
    }

    @GET
    @Path("/proprietaires")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProprietaires() {
        try (Connection connection = new UtilDB().GetConn()) {
            ProprietaireService service = new ProprietaireService();
            List<Proprietaire> proprietaires = service.getAllProprietaires(connection);
            return Response.ok(proprietaires).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to fetch proprietaires").build();
        }
    }

}