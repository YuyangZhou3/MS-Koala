package com.medsec.api;

import com.medsec.entity.ChangePasswordRequestTemplate;
import com.medsec.entity.User;
import com.medsec.util.ArgumentException;
import com.medsec.util.AuthenticationException;
import com.medsec.util.Database;
import com.medsec.util.DefaultRespondEntity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * RESTful APIs for User Account.
 */
@Path("/")
public class UserAPI {

	@POST
	@Path("user/activate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response activateUser(User requestUser) {

        // Verify user with registered information.
        try {

            User user = verifyUserInformation(requestUser);

            // Now the user is ready to be activated.
            // Set new password
            user.password(requestUser.getPassword());
            updateUserPassword(user);

            return Response.ok(new DefaultRespondEntity()).build();
        } catch (ArgumentException e) {
            // Invalid input
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    //.entity(new DefaultRespondEntity(e.getMessage()))
                    .entity(new DefaultRespondEntity("Invalid input"))
                    .build();

        } catch (AuthenticationException e) {
            // Registered info not match
            return Response
                    .status(Response.Status.NOT_FOUND)
                    //.entity(new DefaultRespondEntity("Registered info did not match"))
                    .entity(new DefaultRespondEntity(e.getMessage()))
                    .build();

        } catch (Exception e) {
            // User has been activated
            return Response
                    .status(Response.Status.FORBIDDEN)
                    //.entity(new DefaultRespondEntity("Catch all"))
                    .entity(new DefaultRespondEntity(e.getStackTrace().toString()))
                    .build();
        }
    }


    @PUT
    @Path("user/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(ChangePasswordRequestTemplate requestUser) {
        try {

            // Authenticate the user using the credentials provided
            User user = AuthenticationAPI.authenticate(requestUser);

            // Update the password
            // Set new password
            user.password(requestUser.getNew_password());
            updateUserPassword(user);

            return Response.ok(new DefaultRespondEntity()).build();

        } catch (ArgumentException e) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new DefaultRespondEntity(e.getMessage()))
                    .build();

        } catch (AuthenticationException e) {

            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(new DefaultRespondEntity(e.getMessage()))
                    .build();

        }
    }

    private User verifyUserInformation(User u) throws ArgumentException, AuthenticationException, Exception {

        if (u.getEmail() == null || u.getDob() == null
                || u.getSurname() == null || u.getFirstname() == null)
            throw new ArgumentException();

        Database db = new Database();
        User user = db.getUserByEmail(u.getEmail().toLowerCase());

        if (user == null
                || ! user.getSurname().equalsIgnoreCase(u.getSurname())
                || ! user.getFirstname().equalsIgnoreCase(u.getFirstname())
                || ! user.getDob().equals(u.getDob())
        ) {
            throw new AuthenticationException(AuthenticationException.REGISTRATION_NOT_MATCH);
        }

        if (user.getPassword() != null)
            throw new Exception("User has been activated");

        return user;
    }

    private void updateUserPassword(User u) throws ArgumentException {

        if (u.getPassword() == null)
            throw new ArgumentException();

        Database db = new Database();
        db.updateUserPassword(u.getId(), u.getPassword());
    }
}
