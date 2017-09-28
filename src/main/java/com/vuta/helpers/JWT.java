package com.vuta.helpers;

import com.vuta.Constants;
import com.vuta.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * Created by vuta on 28/06/2017.
 */

public class JWT {

    private static final HashMap<Integer, String> roles = new HashMap<>();

     static {
        roles.put(1, "ADMIN"); // ADMIN
        roles.put(2, "USER"); // USER
        roles.put(3, "VISITOR"); // VISITOR
    }

    public static int getRoleId(String roleName) throws Exception {
         if(roles.containsValue(roleName)) {
             for (Map.Entry<Integer, String> entry: roles.entrySet()) {
                 if(Objects.equals(roleName, entry.getValue()))
                     return entry.getKey();
             }
         }
        throw new Exception("Wrong user role");
    }

    public static String getRoleName(int roleId) throws Exception {
        if(roles.containsKey(roleId)) {
            for (Map.Entry<Integer, String> entry: roles.entrySet()) {
                if(Objects.equals(roleId, entry.getKey()))
                    return entry.getValue();
            }
        }
        throw new Exception("Wrong user role");
    }


    public static Claims verify(String token) throws Exception  {

            // This will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(Constants.JWT_SECRET))
                .parseClaimsJws(token).getBody();
    }

    public static String generate(UserModel user) throws Exception {
        Date date = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setIssuer("Galleo API")
                .setId(user.getId()+"")
                .setSubject("Token")
                .setAudience(JWT.getRoleName(user.getRole()))
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
