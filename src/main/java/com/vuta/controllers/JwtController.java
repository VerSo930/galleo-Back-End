package com.vuta.controllers;

import com.vuta.Constants;
import com.vuta.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by vuta on 26/09/2017.
 */
public class JwtController {

    private static Claims claims;
    private static final HashMap<Integer, String> roles = new HashMap<>();


    public JwtController() {
        roles.put(1,"USER");
        roles.put(2,"ADMIN");
    }

    public Boolean verifyToken(String token) {
        try {
            // This will throw an exception if it is not a signed JWS (as expected)
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(Constants.JWT_SECRET))
                    .parseClaimsJws(token).getBody();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Claims getClaims() {
        return claims;
    }

    public String generate(UserModel user) {

        Date date = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setIssuer("Galleo")
                .setId(user.getId()+"")
                .setAudience(roles.get(user.getRole()))
                .setIssuedAt(date)
                .setExpiration(new Date(new Date(System.currentTimeMillis()).getTime() + (Constants.JWT_EXPIRATION_TIME * 60000)))
                .claim("userId", user.getId())
                .claim("email:",user.getEmail())
                .signWith(SignatureAlgorithm.HS256,
                        TextCodec.BASE64.decode(Constants.JWT_SECRET)
                )
                .compact();
    }

}
