package com.vuta.service;

import com.google.common.net.MediaType;
import com.vuta.Constants;
import com.vuta.controllers.PhotoController;
import com.vuta.model.PhotoModel;
import com.vuta.model.ResponseMessage;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * Created by vuta on 25/09/2017.
 */

@Path(Constants.PHOTO_PATH)
@Consumes(Constants.CONTENT_TYPE)
@Produces(Constants.CONTENT_TYPE)
public class PhotoService {

    @Context
    ServletContext servletContext;
    private PhotoController controller = new PhotoController();

    @PermitAll
    @Path("/")
    @GET
    public Response getAllPhotos() {
        return controller.getAll();
    }

    @PermitAll
    @Path("/{id}")
    @GET
    public Response getPhotoById(@PathParam("id") int id) {
        return controller.getById(id);
    }

    @PermitAll
    @Path("/gallery/{id}")
    @GET
    public Response getPhotoByGalleryId(@PathParam("id") int galleryId) {
        return controller.getByGalleryId(galleryId);
    }

    @PermitAll
    @Path("/")
    @POST
    public Response insertPhoto(PhotoModel photo) {
        return controller.insert(photo);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/{id}")
    @DELETE
    public Response deletePhoto(@PathParam("id") int id) {
        return controller.delete(id);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/")
    @PUT
    public Response updatePhoto(PhotoModel photo) {
        return controller.update(photo);
    }

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    @Produces(Constants.CONTENT_TYPE)
    @RolesAllowed({ "USER", "ADMIN" })
    public Response uploadFile(MultipartFormDataInput input) {
        System.out.println(servletContext.getRealPath("/WEB-INF"));
        return controller.upload(input, servletContext.getRealPath("/WEB-INF/"+ Constants.PHOTO_UPLOAD_PATH));
    }

    @PermitAll
    @Path("/resource/{id}")
    @GET
   // @Consumes(Constants.CONTENT_TYPE)
    @Produces({"image/jpeg", "image/gif"})
    public Response getPhoto(@PathParam("id") String id) {

            File image = new File(servletContext.getRealPath("/WEB-INF/"+ Constants.PHOTO_UPLOAD_PATH + id));
            return Response.ok(image)
                    .status(200).build();


    }
}
