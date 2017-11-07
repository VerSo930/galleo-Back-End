package com.vuta.controllers;

import com.vuta.dao.UserDao;
import com.vuta.helpers.JWT;
import com.vuta.model.UserModel;
import io.jsonwebtoken.Claims;

import java.util.Objects;

/**
 * Created by vuta on 26/09/2017.
 */
public class JwtController {

    private static Claims claims;

    public JwtController() {
    }

    public Boolean verifyToken(String token) {
        try {
            claims = JWT.verify(token);
            UserDao userDao = new UserDao();
            userDao.userCheckout(Integer.parseInt(claims.getId()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims c) {
        claims = c;
    }

    public boolean checkRole(String role) {
        return claims != null && Objects.equals(claims.getAudience(), role);
    }

    public String generateToken(UserModel user) {
        try {
            UserDao userDao = new UserDao();
            userDao.userCheckout(user.getId());
            return JWT.generate(user);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
