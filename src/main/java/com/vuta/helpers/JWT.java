package com.vuta.helpers;

import com.vuta.Constants;
import com.vuta.model.UserModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.Set;

/**
 * Created by vuta on 28/06/2017.
 */

public class JWT {

    public static void verify(String jwt, Set<String> roles) throws Exception {

        try {
            //This will throw an exception if it is not a signed JWS (as expected)
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(Constants.JWT_SECRET))
                    .parseClaimsJws(jwt).getBody();

            // If token doesn't contain the requested role, throw exception
            if(!roles.contains(claims.getAudience()))
            {
                throw new Exception();
            }

            // TODO: Delete after debug finished
            System.out.println("ID: " + claims.getId());
            System.out.println("Audience: " + claims.getAudience());
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Issuer: " + claims.getIssuer());
            System.out.println("Expiration: " + claims.getExpiration());

        } catch (Exception e){
            throw new Exception( "Not allowed [ BAD SIGNATURE ]");
        }

    }

    public static String generate(UserModel user) {
        Date date = new Date(System.currentTimeMillis());
        return Jwts.builder()
                .setIssuer("Galleo API")
                .setSubject("Token")
                .setAudience("USER")
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
