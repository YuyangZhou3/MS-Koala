package com.medsec.api;

import com.medsec.entity.Resource;
import com.medsec.entity.User;
import com.medsec.filter.Secured;
import com.medsec.util.Database;
import com.medsec.util.DefaultRespondEntity;
import com.medsec.util.TCPServer;
import com.medsec.util.UserRole;
import org.glassfish.jersey.server.JSONP;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * RESTful APIs for resources.
 *
 */
@Path("/")
public class ResourceAPI {

    @GET
    @Path("users/{uid}/resources")
    @Secured(UserRole.ADMIN)
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUserResources(
            @PathParam("uid") String uid) {

        List<Resource> results = retrieveUserResources(uid);

        return Response.ok(results).build();
    }

    @GET
    @Path("me/resources")
    @Secured(UserRole.PATIENT)
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listMyResources(
            @Context SecurityContext sc) {

        String uid = sc.getUserPrincipal().getName();
        List<Resource> results = retrieveUserResources(uid);

        return Response.ok(results).build();
    }

    @GET
    @Path("resources/{resourceID}")
    @Secured
    @JSONP(queryParam = JSONP.DEFAULT_CALLBACK)
    @Produces({MediaType.APPLICATION_JSON})
    public Response getResource(
            @Context SecurityContext sc,
            @PathParam("resourceID") String id) {

        User requestUser = (User) sc.getUserPrincipal();
        UserRole requestRole = requestUser.getRole();
        String requestUid = requestUser.getId();

        Database db = new Database();
        Resource resource = db.getResource(id);

        if (resource == null)
            return Response.status(Response.Status.NOT_FOUND).entity(null).build();

        if (requestRole != UserRole.ADMIN && !requestUid.equals(resource.getUid()))
            return Response.status(Response.Status.FORBIDDEN).entity(null).build();

        return Response.ok(resource).build();
    }

    @DELETE
    @Path("resources/{resourceID}")
    @Secured(UserRole.PATIENT)
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteResource(
            @PathParam("resourceID") String resourceID){
        Database db=new Database();
        Resource resource = db.getResource(resourceID);
        System.out.println(resource.getId());
        if(resource==null){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new DefaultRespondEntity("resource that to be deleted doesn't existed in db"))
                    .build();
        }

        // remove resource file in disk
        boolean isClear = clearSpace(resource.getContent());
        if(isClear)
            System.out.println("Resource pdf file has been removed ");
        else
            System.out.println("Resource pdf file deletion failed");

        db = new Database();
        db.deleteResource(resourceID);
        db.close();
        return Response.ok(new DefaultRespondEntity()).build();
    }

    private boolean clearSpace(String filePath){
        try{
            String resoucePath = TCPServer.class.getResource("/").getPath();
            System.out.println("resourcePath: "+resoucePath);

            String webappsDir = (new File(resoucePath,"../../")).getCanonicalPath();
            System.out.println("webappsDir: "+webappsDir);

            String absoluteFilePath = webappsDir + filePath;
            System.out.println(absoluteFilePath);

            File file = new File(absoluteFilePath);
            if(file.exists())
                file.delete();              // directory won't be removed
            else
                throw new NoSuchFileException("File does not exist");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // Fetch and return a particular resource pdf file
    @GET
    @Path("resources/link/{resourceID}")
    @Secured(UserRole.PATIENT)
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(
            @Context ServletContext sc,
            @PathParam("resourceID") String resourceID){
        try {
            Database db = new Database();
            Resource resource = db.getResource(resourceID);
            String filepath = sc.getRealPath(resource.getContent());
            File file = new File(filepath);
            System.out.println(filepath);
            return Response
                    .ok(file,MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition","attachment;filename=" + resourceID)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    private List <Resource> retrieveUserResources(String uid) {
        Database db = new Database();
        return db.listUserResources(uid);
    }

}
