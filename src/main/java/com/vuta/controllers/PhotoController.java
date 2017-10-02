package com.vuta.controllers;

import com.vuta.dao.PhotoDao;
import com.vuta.helpers.PhotoTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.PhotoModel;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.core.Response;

/**
 * Created by vuta on 25/09/2017.
 */
public class PhotoController {


    private Response.ResponseBuilder rb;
    private PhotoDao dao;

    public PhotoController()  {}

    public Response getAll() {
        try {
            this.dao = new PhotoDao();
            this.rb = Response.ok(dao.getAll());
            this.rb.status(200);
            return rb.build();
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(500);
            return rb.build();
        }

    }

    public Response insert(PhotoModel photo) {
        try {
            this.dao = new PhotoDao();
            if (!PhotoTools.checkInsert(photo))
                return Response.ok(new ResponseMessage("You must provide all photo details")).status(400).build();
            this.rb = Response.ok(this.dao.insert(photo));
            this.rb.status(200);

        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
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
            return Response.ok(dao.getByUserId(userId)).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response getById(int photoId) {
        try {
            this.dao = new PhotoDao();
            if(photoId == 0)
                throw new Exception("You must provide a Photo id");
            return Response.ok(dao.getById(photoId)).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response getByGalleryId(int galleryId) {
        try {
            this.dao = new PhotoDao();
            if(galleryId == 0)
                throw new Exception("You must provide a Gallery id");
            return Response.ok(dao.getByGalleryId(galleryId)).status(200).build();

        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response delete(int photoId) {
        try {
            this.dao = new PhotoDao();
            if(photoId == 0)
                throw new Exception("You must provide a Photo id");

            if(dao.delete(photoId)) {
                return Response.ok(new ResponseMessage("Photo with id " + photoId + " was deleted")).status(200).build();
            } else {
                return Response.ok(new ResponseMessage("Can't delete Photo with id " + photoId)).status(204).build();
            }

        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response update(PhotoModel photo) {
        try {
            this.dao = new PhotoDao();
            if(!PhotoTools.checkInsert(photo))
                throw new Exception("You must provide a photo id");

            if(dao.update(photo)) {
                return Response.ok(new ResponseMessage("Photo with id " + photo.getId() + " was updated")).status(200).build();
            } else {
                return Response.ok(new ResponseMessage("Can't update Photo with id " + photo.getId())).status(204).build();
            }
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
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
