package com.medsec.api;

import com.google.gson.Gson;
import com.medsec.entity.*;
import com.medsec.filter.Secured;
import com.medsec.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.Instant;
import java.time.LocalDate;

import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;
import java.util.regex.Pattern;



/**
 * RESTful APIs for sharing data.
 *
 */
@Path("/")
public class ShareAPI {

    @POST
    @Path("share/appointment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public Response shareAppointment(@Context SecurityContext sc, Mail mail) {

        String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        User requestUser = (User) sc.getUserPrincipal();

        mail = new Mail().id("1").mailto("calvinlin0606@gmail.com").content("testing email");
        System.out.println(mail.toString());

        if(mail.getId()==null)
            return Response.status(Response.Status.BAD_REQUEST).entity(new DefaultRespondEntity("The appointment id not specified")).build();

        Database db = new Database();
        Appointment app = db.getAppointment(mail.getId());
        db.close();

        if (app == null)
            return Response.status(Response.Status.NOT_FOUND).entity(new DefaultRespondEntity("Could not find requested appointment")).build();

        if(!app.getUid().equals(requestUser.getId()))
            return Response.status(Response.Status.FORBIDDEN).entity(new DefaultRespondEntity("Requested appointment for different user")).build();

        if(!Pattern.matches(REGEX_EMAIL, mail.getMailto()))
            return Response.status(Response.Status.BAD_REQUEST).entity("The email address not properly formed").build();

        Gson gson = new Gson();
        String appointmentJSON = gson.toJson(app);

        System.out.println(appointmentJSON);

        String textHtml = "";

        if (mail.getMailto() != null) {
            String name = mail.getMailto().split("@")[0];
            Email email = new EmailBuilder()
                              .from("Medical heal team", "medical.heal12345@gmail.com")
                              .to(name, mail.getMailto())
                              .subject("Sharing Appointment Information")
                              .text(appointmentJSON)
                              .build();

            Mailer mailer = new Mailer("smtp.gmail.com", 587, "medical.heal12345@gmail.com", "tttqqqvvv!!!medheal12345%$#@!",
                                        TransportStrategy.SMTP_TLS);
            mailer.sendMail(email);

            return Response.ok(app).build();
        }
        else
            return Response.status(Response.Status.FORBIDDEN).entity("An error occured while sending email").build();
    }
}
