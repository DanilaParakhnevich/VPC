package by.parakhnevich.media.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

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
    public String sendMultipart(@MultipartForm File file) {
        return null;
    }

    @POST
    @Path("/load")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveMultipart(@MultipartForm File file) {
        return null;
    }
}
