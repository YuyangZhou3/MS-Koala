package com.medsec.api;

import com.medsec.entity.Appointment;
import com.medsec.entity.User;
import com.medsec.filter.Secured;
import com.medsec.util.Database;
import com.medsec.util.UserRole;
import org.glassfish.jersey.server.JSONP;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.File;


@Path("/")
public class FileAPI {
    @GET
    @Path("file/link/{file_id}")
    @Secured(UserRole.PATIENT)
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(
            @Context ServletContext sc,
            @PathParam("file_id") String id){
        try {
            Database db = new Database();
            String link = db.getLink(id);
            String filepath = sc.getRealPath(link);
            File file = new File(filepath);
            System.out.println(filepath);
            return Response
                    .ok(file,MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition","attachment;filename=" + id)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("files/{file_id}")
    @Secured
    @JSONP(queryParam = JSONP.DEFAULT_CALLBACK)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getFile(
            @Context SecurityContext sc,
            @PathParam("file_id") String id) {

        Database db = new Database();
        com.medsec.entity.File file = db.selectFileById(id);

        if (file == null)
            return Response.status(Response.Status.NOT_FOUND).entity(null).build();

        User requestUser = (User) sc.getUserPrincipal();
        UserRole requestRole = requestUser.getRole();
        String requestUid = requestUser.getId();

        Database db2 = new Database();
        Appointment appointment = db2.getAppointment(file.getApptid());

        if (appointment == null)
            return Response.status(Response.Status.NOT_FOUND).entity(null).build();

        if (requestRole != UserRole.ADMIN && !requestUid.equals(appointment.getUid()))
            return Response.status(Response.Status.FORBIDDEN).entity(null).build();

        return Response.ok(file).build();
    }
}
