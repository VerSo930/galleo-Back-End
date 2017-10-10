package com.vuta.controllers;

import com.vuta.dao.GalleryDao;
import com.vuta.helpers.GalleryTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.GalleryModel;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */

public class GalleryController {

    private Response.ResponseBuilder rb;
    private GalleryDao dao;

    public GalleryController() {

    }

    public Response getAll(int page, int limit) {
        try {
            int offset;

            if (limit == 0 || page == 0)
                throw new Exception("You must provide pagination data");
            if (page == 1) {
                offset = 0;
            } else {
                offset = (page - 1) * limit;
            }
            this.dao = new GalleryDao();
            Map<Integer, Object> galleryData = dao.getAll(limit, offset);
            return Response.ok(galleryData.get(1))
                    .header("X-Pagination-Count", galleryData.get(2))
                    .header("X-Pagination-Limit", limit)
                    .header("X-Pagination-Page", page)
                    .status(200)
                    .build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(500)
                    .build();
        }
    }

    public Response insert(GalleryModel gallery) {
        try {
            this.dao = new GalleryDao();
            if (!GalleryTools.checkInsert(gallery))
                return Response.ok(new ResponseMessage("You must provide all user details")).status(400).build();
            return Response.ok(this.dao.insert(gallery))
                    .status(200)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(400)
                    .build();
        }

    }

    /**
     * Get all gallery's of a specific User.
     * This is done by providing a valid user id
     */
    public Response getByUserId(int userId, int page, int limit) {
        try {
            this.dao = new GalleryDao();

            int offset;
            if (userId < 1 || limit < 1 || page < 1)
                throw new Exception("You must provide a user id and pagination data");
            if (page == 1) {
                offset = 0;
            } else {
                offset = (page - 1) * limit;
            }
            Map<Integer, Object> galleryData = dao.getByUserId(userId, limit, offset);
            return Response.ok(galleryData.get(1))
                    .header("X-Pagination-Count", galleryData.get(2))
                    .header("X-Pagination-Limit", limit)
                    .header("X-Pagination-Page", page)
                    .status(200)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(400)
                    .build();
        }
    }

    public Response getById(int galleryId) {
        try {
            this.dao = new GalleryDao();
            if (galleryId == 0)
                throw new Exception("You must provide a gallery id");
            return Response.ok(dao.getById(galleryId))
                    .status(200)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(400)
                    .build();
        }

    }

    public Response delete(int galleryId) {
        try {
            this.dao = new GalleryDao();
            if (galleryId == 0)
                throw new Exception("You must provide a gallery id");
            if (dao.delete(galleryId) == 0)
                return Response.ok(new ResponseMessage("Gallery don't exist"))
                        .status(204)
                        .build();
            return Response.ok(new ResponseMessage("Gallery"))
                    .status(200)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(400)
                    .build();
        }

    }

    public Response update(GalleryModel gallery) {
        try {
            this.dao = new GalleryDao();
            if (!GalleryTools.checkInsert(gallery))
                throw new Exception("You must provide a gallery id");
            dao.update(gallery);
            return Response.ok()
                    .status(200)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(new ResponseMessage(e.getMessage()))
                    .status(400)
                    .build();
        }
    }

}
