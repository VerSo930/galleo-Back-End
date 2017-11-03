package com.vuta;

import com.vuta.controllers.JwtController;
import com.vuta.model.UserModel;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class Constants {

    public static final String CONTENT_TYPE = "application/json";
    public static final String API_PATH = "/v1";
    public static final String LOGIN_PATH = "/login";
    public static final String USER_PATH = "/user";
    public static final String GALLERY_PATH = "/gallery";
    public static final String PHOTO_PATH = "/photo";
    public static final String PHOTO_UPLOAD_PATH = "/opt/tomcat/webapps/galleo-images/";
    public static final String LOGS_PATH = "/opt/tomcat/webapps/galleo-logs/";

    // Authentication
    public static final String JWT_SECRET = "pCu/ghCamq9+wS/CG16JJ1NBqur2Ckzl522AA8xbhSQ=";
    public static final int JWT_EXPIRATION_TIME = 60*24;


    public static void main(String[] args0) {

        UserModel user = new UserModel();
        user.setId(1);
        user.setEmail("verso@gmail.com");
        user.setRole(1);

//        System.out.println(JWT.generate(user));
        JwtController jwt = new JwtController();
        System.out.println(jwt.generateToken(user));


       // assert Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws("esyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJHYWxsZW8gQVBJIiwianRpIjoiMSIsInN1YiI6IlRva2VuIiwiYXVkIjoiVVNFUiIsImlhdCI6MTUwNjQyNzQ4MCwiZXhwIjoxNTA2NDI3NzgwLCJ1c2VySWQiOjEsImVtYWlsOiI6InZlcnNvQGdtYWlsLmNvbSJ9.7IuEbS-Z7DYXJ7i0WHK3z-Hu-EzWr4Vp3zMbnnX1nWc").getBody().getAudience().equals("Joe");

    }
}