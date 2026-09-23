package by.parakhnevich.media.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestForm;

import java.io.File;

/**
 * Created by agallochum on 2026-09-16
 */
@Path("/media")
@RegisterRestClient
public class MediaController {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String sendMultipart(@RestForm("file") File file) {
        return null;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveMultipart(@RestForm("file") File file) {
        return null;
    }
}
