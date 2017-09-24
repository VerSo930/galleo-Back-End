package com.vuta.controllers;

import com.vuta.dao.UserDao;
import com.vuta.helpers.JWT;
import com.vuta.helpers.UserTools;
import com.vuta.model.ErrorModel;
import com.vuta.model.UserModel;

import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class AuthenticationController {

    private UserModel Puser;
    private Response.ResponseBuilder rb;
    private UserDao dao;

    public AuthenticationController()  {

    }

    public Response login(UserModel user) {
        try {
            this.dao = new UserDao();
            if (!UserTools.checkLogin(user))
                return Response.ok(new ErrorModel("User data must be filled!")).status(400).build();
            this.Puser = this.dao.getById(user);
            this.rb = Response.ok(this.Puser);
            this.rb.header("Token", JWT.generate(this.Puser));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response getAllUsers() {
        try {
            this.dao = new UserDao();
            this.rb = Response.ok(dao.getAll());
            this.rb.status(200);
            return rb.build();
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(500);
            return rb.build();
        }

    }

    public Response register(UserModel user) {
        try {
            this.dao = new UserDao();
            if (!UserTools.checkRegister(user))
                return Response.ok(new ErrorModel("You must provide all user details")).status(400).build();
            this.rb = Response.ok(this.dao.insert(user));
            this.rb.status(200);

        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }
}
