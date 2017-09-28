package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.UserController;
import com.vuta.model.ResponseMessage;
import com.vuta.model.UserModel;
import io.jsonwebtoken.Claims;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */

@Path(Constants.USER_PATH)
@Produces(Constants.CONTENT_TYPE)
@Consumes(Constants.CONTENT_TYPE)
public class UserService {

    private UserController controller = new UserController();


    @PermitAll
    @POST
    @Path("/login")
    public Response loginUser(UserModel user) {
        return this.controller.login(user);
    }

    @PermitAll
    @POST
    @Path("/register")
    public Response registerUser(UserModel user) {
        return this.controller.register(user);
    }

    @RolesAllowed({"ADMIN"})
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") int userId) {
        return this.controller.delete(userId);
    }

    @RolesAllowed({"ADMIN"})
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") int userId) {
        return this.controller.getById(userId);
    }

    @RolesAllowed("USER")
    @POST
    @Path("/test")
    public Response test(@Context Claims claims,
                         @HeaderParam("Authorization") String authorization,
                         UserModel user) {
        controller.setJwtClaims(claims);
        return Response.ok(new ResponseMessage("OK")).build();
    }


}
