package com.medsec.api;

import com.medsec.entity.Resource;
import com.medsec.entity.User;
import com.medsec.filter.Secured;
import com.medsec.util.Database;
import com.medsec.util.DefaultRespondEntity;
import com.medsec.util.UserRole;
import org.glassfish.jersey.server.JSONP;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
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

    @DELETE														// delete http 请求
    @Path("resources/{resourceID}")		// 请求路径
    @Secured(UserRole.PATIENT)				// 病人身份
    @JSONP(queryParam = "callback")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteResource(
            @PathParam("resourceID") String resourceID){
        Database db=new Database();
        Resource resource = db.getResource(resourceID);
        if(resource==null){
            db.close();
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(new DefaultRespondEntity("resource that to be deleted doesn't existed in db"))
                    .build();
        }else{
            db.deleteResource(resourceID);
            db.close();
            return Response.ok(new DefaultRespondEntity()).build();
        }
    }

    private List <Resource> retrieveUserResources(String uid) {

        Database db = new Database();
        return db.listUserResources(uid);
    }

}
