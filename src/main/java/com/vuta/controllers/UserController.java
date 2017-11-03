package com.vuta.controllers;

import com.vuta.dao.UserDao;
import com.vuta.helpers.JWT;
import com.vuta.helpers.Logger;
import com.vuta.helpers.UserTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.UserModel;
import io.jsonwebtoken.Claims;

import javax.ws.rs.core.Response;
import java.util.Objects;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class UserController {

    private UserDao dao;
    private JwtController jwtController;
    private Logger log = new Logger();

    public UserController() {
        this.dao = new UserDao();
        this.jwtController = new JwtController();
    }

    public void setJwtClaims(Claims claims) {
        this.jwtController.setClaims(claims);
    }

    public Response login(UserModel user) {
        try {
            // check if user data is set
            if (!UserTools.checkLogin(user)) {
                log.logEvent("Login data not privided detected!");
                return Response.ok(new ResponseMessage("User data must be filled!")).status(400).build();}
            user = this.dao.login(user.getUserName(), user.getPassword());

            // check if there's a user  having provided username and password
            // if not return a 401 response
            if (user == null) {
                log.logEvent("Wrong credentials login attempt detected!");
                return Response.ok(new ResponseMessage("Wrong username or password! Please try again with valid credentials"))
                        .status(401).build();
            }

            // If user is disable, set enabled status on login
            if (!user.getIsEnabled()) {
                dao.enableUser(user.getId());
            }

            System.out.println(this.jwtController.generateToken(user));
            return Response.ok(user).header("Authorization", this.jwtController.generateToken(user)).status(200).build();
        } catch (Exception e) {
            log.logError(e.getMessage());
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }

    }

    public Response getAllUsers() {
        try {
            return Response.ok(dao.getAll()).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }

    }

    public Response getById(int id) {
        try {
            if (id == 0) {
                log.logEvent("Trying to get user users without id detected!");
                return Response.ok(new ResponseMessage("You must provide user id")).status(400).build();
            }
            UserModel user = this.dao.getById(id);
            return Response.ok(user).status(200).build();
        } catch (Exception e) {
            log.logError(e.getMessage());
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }

    }

    public Response register(UserModel user) {
        try {
            // Check if the provided user has all required info's
            if (!UserTools.checkRegister(user)) {
                log.logEvent("Register with missing parameter detected");
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();
            }

            // Check if user that we are registering now is asking for ADMIN permission
            // If it ask for admin permission, return a 400 response
            if (Objects.equals(JWT.getRoleName(user.getRole()), "ADMIN")) {
                log.logEvent("Register with denied role detected. Hacker?");
                return Response.ok(new ResponseMessage("You can't register with the asked role")).status(400).build();
            }

            // If the insertion in the DATABASE is successfully, return the new user
            return Response.ok(this.dao.insert(user))
                    .header("Authorization", this.jwtController.generateToken(user))
                    .status(200).build();

        } catch (Exception e) {
            log.logError(e.getMessage());
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }
    }

    public Response delete(int userId) {
        try {
            if (userId == 0){
                log.logEvent("User delete parameters missing detected!");
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();}

            if (this.dao.delete(userId)) {
                log.logEvent("User deleted: id "+ userId);
                return Response.ok(new ResponseMessage("User deleted successfully")).status(200).build();
            } else {
                log.logEvent("Fail attempt to delete user detected!");
                return Response.ok(new ResponseMessage("User can't be deleted")).status(400).build();
            }
        } catch (Exception e) {
            log.logError(e.getMessage());
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }
    }
}
