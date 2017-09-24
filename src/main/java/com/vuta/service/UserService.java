package com.vuta.service;

import com.vuta.Constants;
import com.vuta.controllers.AuthenticationController;
import com.vuta.model.UserModel;

import javax.annotation.security.PermitAll;
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


}
