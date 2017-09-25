package com.vuta.controllers;

import com.sun.org.apache.regexp.internal.RE;
import com.vuta.Constants;
import com.vuta.dao.PhotoDao;
import com.vuta.helpers.PhotoTools;
import com.vuta.model.ErrorModel;
import com.vuta.model.PhotoModel;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by vuta on 25/09/2017.
 */
public class PhotoController {


    private Response.ResponseBuilder rb;
    private PhotoDao dao;

    public PhotoController()  {

    }

    public Response getAll() {
        try {
            this.dao = new PhotoDao();
            this.rb = Response.ok(dao.getAll());
            this.rb.status(200);
            return rb.build();
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(500);
            return rb.build();
        }

    }

    public Response insert(PhotoModel photo) {
        try {
            this.dao = new PhotoDao();
            if (!PhotoTools.checkInsert(photo))
                return Response.ok(new ErrorModel("You must provide all photo details")).status(400).build();
            this.rb = Response.ok(this.dao.insert(photo));
            this.rb.status(200);

        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    /** Get all Photo's of a specific User.
     * This is done by providing a valid user id */
    public Response getByUserId(int userId) {
        try {
            this.dao = new PhotoDao();
            if(userId == 0)
                throw new Exception("You must provide a User id");
            this.rb = Response.ok(dao.getByUserId(userId));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response getById(int photoId) {
        try {
            this.dao = new PhotoDao();
            if(photoId == 0)
                throw new Exception("You must provide a Photo id");
            this.rb = Response.ok(dao.getById(photoId));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response getByGalleryId(int galleryId) {
        try {
            this.dao = new PhotoDao();
            if(galleryId == 0)
                throw new Exception("You must provide a Gallery id");
            this.rb = Response.ok(dao.getByGalleryId(galleryId));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response delete(int photoId) {
        try {
            this.dao = new PhotoDao();
            if(photoId == 0)
                throw new Exception("You must provide a Photo id");
            dao.delete(photoId);
            this.rb = Response.ok();
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response update(PhotoModel photo) {
        try {
            this.dao = new PhotoDao();
            if(!PhotoTools.checkInsert(photo))
                throw new Exception("You must provide a gallery id");
            dao.update(photo);
            this.rb = Response.ok();
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ErrorModel(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response upload(MultipartFormDataInput input, String servletPath) {

        try {

            this.rb = Response.ok(PhotoTools.uploadPhoto(input, servletPath));
            this.rb.status(200);
        } catch (Exception e) {
            e.printStackTrace();
            this.rb = Response.ok(e.getMessage());
            this.rb.status(400);
        }
        return this.rb.build();
    }
}
