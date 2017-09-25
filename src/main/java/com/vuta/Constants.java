package com.vuta;

import com.vuta.model.UserModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
    public static final String PHOTO_UPLOAD_PATH = "uploaded-photos/";

    // Authentication
    public static final String JWT_SECRET = "pCu/ghCamq9+wS/CG16JJ1NBqur2Ckzl522AA8xbhSQ=";
    public static final int JWT_EXPIRATION_TIME = 5;


    public static void main(String[] args0) {
        System.out.println(UUID.randomUUID().toString());
    }
}