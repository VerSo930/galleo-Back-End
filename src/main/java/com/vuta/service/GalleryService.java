package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.GalleryController;
import com.vuta.model.GalleryModel;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/24/2017.
 */
@Path(Constants.GALLERY_PATH)
@Consumes(Constants.CONTENT_TYPE)
@Produces(Constants.CONTENT_TYPE)
public class GalleryService {

    private GalleryController controller = new GalleryController();

    @Path("/getAll")
    @GET
    @PermitAll
    public Response getAllGallery() {
        return controller.getAll();
    }

    @Path("/getById")
    @GET
    @PermitAll
    public Response getGalleryById(@QueryParam("id") int id) {
        return controller.getById(id);
    }

    @Path("/getByUserId")
    @GET
    @PermitAll
    public Response getGalleryByUserId(@QueryParam("id") int id) {
        return controller.getByUserId(id);
    }

    @Path("/insert")
    @POST
    @PermitAll
    public Response insertGallery(GalleryModel gallery) {
        return controller.insert(gallery);
    }

    @Path("/delete")
    @POST
    @PermitAll
    public Response deleteGallery( GalleryModel gallery) {
        return controller.delete(gallery.getId());
    }

    @Path("/update")
    @POST
    @PermitAll
    public Response updateGallery(GalleryModel gallery) {
        return controller.update(gallery);
    }
}
