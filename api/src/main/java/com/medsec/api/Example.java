package com.medsec.api;

import com.medsec.util.Authentication;
import com.medsec.util.Response;
import org.glassfish.jersey.server.JSONP;
import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import javax.ws.rs.*;
import java.util.regex.Pattern;

/**
 * An RESTful API demo route.
 * Root resource (exposed at "example/" path)
 */
@Path("example")
public class Example {

    /**
     * Simply respond with query content.
     * Request method: GET.
     * Parameters are passed via QueryParams.
     * JSONP supported, subject to specified Accept Header.
     * @param content query content, if nothing provided, Default value 'Hello world' would be used.
     * @return Response.
     */
    @GET
    @JSONP(queryParam = "callback")
    @Produces({"application/javascript", "application/json"})
    public String echo(
            @DefaultValue("Hello world") @QueryParam("content") String content) {  //Extract parameters from url query string

        System.out.println(Authentication.auth("1", "qwerty"));



        return content;
    }

    /**
     * Send a email with customised content to an address.
     * Request method: GET.
     * Parameters are passed via QueryParams.
     * JSONP supported, subject to specified Accept Header.
     * @param mailto The address the email would be sent to.
     * @param content Email content, if nothing provided, Default value 'Hello world' would be used.
     * @return Response.
     */
    @GET
    @Path("sendMail")
    @JSONP(queryParam = "callback")
    @Produces({"application/javascript", "application/json"})
    public String sendEmailExample(
            @QueryParam("mailto") String mailto,
            @DefaultValue("Hello world") @QueryParam("content") String content) {
        String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

        if (mailto != null && Pattern.matches(REGEX_EMAIL,mailto)) {
            String name = mailto.split("@")[0];

            Email email = new EmailBuilder()
                    .from("Project ICU", "icu.swen90014@gmail.com")
                    .to(name, mailto)
                    .subject("Java Mail Test")
                    .text("This is a test mail sent with java mail\n"+content)
                    .build();

            new Mailer("smtp.gmail.com", 587, "icu.swen90014@gmail.com", "swen90014",
                    TransportStrategy.SMTP_TLS).sendMail(email);

            return Response.success();
        }
        else return Response.error("wrong email address");
    }




}
