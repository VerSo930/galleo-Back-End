package com.vuta.controllers;

import com.vuta.dao.UserDao;
import com.vuta.helpers.JWT;
import com.vuta.helpers.UserTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.UserModel;

import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class AuthenticationController {

    private UserModel Puser;
    private UserDao dao;

    public AuthenticationController()  {
        this.dao = new UserDao();
    }

    public Response login(UserModel user) {
        try {
            // check if user data is set
            if (!UserTools.checkLogin(user))
                return Response.ok(new ResponseMessage("User data must be filled!")).status(400).build();

            this.Puser = this.dao.getById(user);
            return Response.ok(this.Puser).status(200).header("Token", JWT.generate(this.Puser)).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).header("Token", JWT.generate(this.Puser)).build();
        }

    }

    public Response getAllUsers() {
        try {
            return  Response.ok(dao.getAll()).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }

    }

    public Response register(UserModel user) {
        try {
            if (!UserTools.checkRegister(user))
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();
            return Response.ok(this.dao.insert(user)).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }
    }

    public Response delete(UserModel user) {
        try {
            if (user.getId() == 0)
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();
            if(this.dao.delete(user.getId()))
                return Response.ok(new ResponseMessage("User deleted successfully")).status(200).build();
            else
                return Response.ok(new ResponseMessage("User can't be deleted")).status(400).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }
    }
}
