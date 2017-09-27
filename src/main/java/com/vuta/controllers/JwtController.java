package com.vuta.controllers;

import com.vuta.Constants;
import com.vuta.helpers.JWT;
import com.vuta.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by vuta on 26/09/2017.
 */
public class JwtController {

    private Claims claims;

    public JwtController() {
    }

    public JwtController(Claims claims) {
        this.claims = claims;
    }

    public Boolean verifyToken(String token) {
        try {
            this.claims = JWT.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims c) {
        this.claims = c;
    }

    public boolean checkRole(String role) {
        return claims != null && Objects.equals(claims.getAudience(), role);
    }

    public String generateToken(UserModel user) {
        try {
            return JWT.generate(user);
        } catch (Exception e) {
            return null;
        }
    }

}
