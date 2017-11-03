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
    public Response getAllPhotos(@HeaderParam("X-Pagination-Page") int page,
                                 @HeaderParam("X-Pagination-Limit") int limit) {
        return controller.getAll(page, limit);
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
    public Response getPhotoByGalleryId(@PathParam("id") int galleryId,
                                        @HeaderParam("X-Pagination-Page") int page,
                                        @HeaderParam("X-Pagination-Limit") int limit) {
        return controller.getByGalleryId(galleryId, page, limit);
    }

    @PermitAll
    @Path("/")
    @POST
    @Consumes("multipart/form-data")
    public Response insertPhoto(MultipartFormDataInput input) {
        // return controller.insert(input, servletContext.getRealPath("/WEB-INF/" + Constants.PHOTO_UPLOAD_PATH));
        return controller.insert(input);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/{id}")
    @DELETE
    public Response deletePhoto(@PathParam("id") int id) {
        return controller.delete(id);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/{oldGalleryId}")
    @PUT
    public Response updatePhoto(PhotoModel photo,
                                @PathParam("oldGalleryId") int oldGalleryId) {
        return controller.update(photo, oldGalleryId);
    }


    @PermitAll
    @Path("/resource/{userId}/{galleryId}/{photoId}/{quality}/{filename}")
    @GET
    @Consumes("multipart/form-data")
    @Produces({"image/jpeg", "image/gif"})
    public Response getPhoto(@PathParam("userId") int userId,
                             @PathParam("photoId") int photoId,
                             @PathParam("galleryId") int galleryId,
                             @PathParam("quality") String quality,
                             @PathParam("filename") String filename) {

       return this.controller.getImage(userId, photoId, galleryId, quality, filename);
    }
}
