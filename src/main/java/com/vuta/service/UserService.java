package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.AuthenticationController;
import com.vuta.controllers.JwtController;
import com.vuta.helpers.JWT;
import com.vuta.model.ResponseMessage;
import com.vuta.model.UserModel;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */

@Path(Constants.USER_PATH)
@Produces(Constants.CONTENT_TYPE)
@Consumes(Constants.CONTENT_TYPE)
public class UserService {

    private AuthenticationController controller = new AuthenticationController();
    private JwtController jwtController = new JwtController();

    @PermitAll
    @POST
    @Path("/login")
    public Response loginUser(@HeaderParam("Authorization") String authorization, UserModel user) {
        return this.controller.login(user);
    }

    @PermitAll
    @POST
    @Path("/register")
    public Response registerUser(UserModel user) {
        return this.controller.register(user);
    }

    @PermitAll
    @POST
    @Path("/delete")
    public Response deleteUser(@HeaderParam("Authorization") String authorization, UserModel user) {
        return this.controller.delete(user);
    }

    @RolesAllowed("USER")
    @POST
    @Path("/test")
    public Response test(@HeaderParam("Authorization") String authorization, UserModel user) {
        System.out.println("UserId:" + jwtController.getClaims().getIssuer());
        return Response.ok(new ResponseMessage("OK")).build();
    }


}
