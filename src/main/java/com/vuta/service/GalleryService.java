package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.GalleryController;
import com.vuta.model.GalleryModel;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
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

    @Path("/")
    @GET
    @PermitAll
    public Response getAllGallery(@HeaderParam("X-Pagination-Page") int page,
                                  @HeaderParam("X-Pagination-Limit") int limit) {
        return controller.getAll(page, limit);
    }

    @Path("/{id}")
    @GET
    @PermitAll
    public Response getGalleryById(@PathParam("id") int id) {
        return controller.getById(id);
    }

    @Path("/user/{id}")
    @GET
    @PermitAll
    public Response getGalleryByUserId(@PathParam("id") int id,
                                       @HeaderParam("X-Pagination-Page") int page,
                                       @HeaderParam("X-Pagination-Limit") int limit) {
        return controller.getByUserId(id, page, limit);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/")
    @POST
    public Response insertGallery(GalleryModel gallery) {
        return controller.insert(gallery);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/{id}")
    @DELETE
    public Response deleteGallery(@PathParam("id") int id) {
        return controller.delete(id);
    }

    @RolesAllowed({"ADMIN", "USER"})
    @Path("/")
    @PUT
    public Response updateGallery(GalleryModel gallery) {
        return controller.update(gallery);
    }


}
