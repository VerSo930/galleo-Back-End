package com.vuta.controllers;

import com.vuta.dao.GalleryDao;
import com.vuta.helpers.GalleryTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.GalleryModel;

import javax.ws.rs.core.Response;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */

public class GalleryController {

    private Response.ResponseBuilder rb;
    private GalleryDao dao;

    public GalleryController()  {

    }

    public Response getAll() {
        try {
            this.dao = new GalleryDao();
            this.rb = Response.ok(dao.getAll());
            this.rb.status(200);
            return rb.build();
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(500);
            return rb.build();
        }

    }

    public Response insert(GalleryModel gallery) {
        try {
            this.dao = new GalleryDao();
            if (!GalleryTools.checkInsert(gallery))
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();
            this.rb = Response.ok(this.dao.insert(gallery));
            this.rb.status(200);

        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    /** Get all gallery's of a specific User.
     * This is done by providing a valid user id */
    public Response getByUserId(int userId) {
        try {
            this.dao = new GalleryDao();
            if(userId == 0)
                throw new Exception("You must provide a user id");
            this.rb = Response.ok(dao.getByUserId(userId));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response getById(int galleryId) {
        try {
            this.dao = new GalleryDao();
            if(galleryId == 0)
                throw new Exception("You must provide a gallery id");
            this.rb = Response.ok(dao.getById(galleryId));
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response delete(int galleryId) {
        try {
            this.dao = new GalleryDao();
            if(galleryId == 0)
                throw new Exception("You must provide a gallery id");
            dao.delete(galleryId);
            this.rb = Response.ok();
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }

    public Response update(GalleryModel gallery) {
        try {
            this.dao = new GalleryDao();
            if(!GalleryTools.checkInsert(gallery))
                throw new Exception("You must provide a gallery id");
            dao.update(gallery);
            this.rb = Response.ok();
            this.rb.status(200);
        } catch (Exception e) {
            this.rb = Response.ok(new ResponseMessage(e.getMessage()));
            this.rb.status(400);
            e.printStackTrace();
        }
        return this.rb.build();
    }
}
