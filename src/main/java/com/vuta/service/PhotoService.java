package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.PhotoController;
import com.vuta.model.PhotoModel;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;


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

    @Path("/getAll")
    @GET
    @PermitAll
    public Response getAllPhotos() {
        return controller.getAll();
    }

    @Path("/getById")
    @GET
    @PermitAll
    public Response getPhotoById(@QueryParam("id") int id) {
        return controller.getById(id);
    }

    @Path("/getByGalleryId")
    @GET
    @PermitAll
    public Response getPhotoByGalleryId(@QueryParam("galleryId") int galleryId) {
        return controller.getByGalleryId(galleryId);
    }

    @Path("/insert")
    @POST
    @PermitAll
    public Response insertPhoto(PhotoModel photo) {
        return controller.insert(photo);
    }

    @Path("/delete")
    @POST
    @PermitAll
    public Response deletePhoto(PhotoModel photo) {
        return controller.delete(photo.getId());
    }

    @Path("/update")
    @POST
    @PermitAll
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

    @Path("/showPhoto")
    @GET
    @Consumes(Constants.CONTENT_TYPE)
    @Produces({"image/png", "image/jpeg", "image/gif"})
    public Response getPhoto(@QueryParam("id") String id) {
        File repositoryFile = new File(servletContext.getRealPath("/WEB-INF/"+ Constants.PHOTO_UPLOAD_PATH + id));
        return Response.ok(repositoryFile).status(200).build();
    }
}
