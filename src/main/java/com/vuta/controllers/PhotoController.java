package com.vuta.controllers;

import com.vuta.dao.PhotoDao;
import com.vuta.helpers.PhotoTools;
import com.vuta.model.ResponseMessage;
import com.vuta.model.PhotoModel;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Created by vuta on 25/09/2017.
 */
public class PhotoController {

    private PhotoDao dao;

    public PhotoController() {
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

            this.dao = new PhotoDao();
            Map<Integer, Object> photoData = dao.getAll(limit, offset);

            return Response.ok(photoData.get(1))
                    .header("X-Pagination-Count", photoData.get(2))
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

    public Response insert(MultipartFormDataInput input) {
        try {

            this.dao = new PhotoDao();
            PhotoModel photo = PhotoTools.mapFormToPhoto(input);

            if (!PhotoTools.checkInsert(photo))
                return Response.ok(new ResponseMessage("You must provide all photo details")).status(400).build();
            photo.setUrl("");
            this.dao.insert(photo);
            PhotoTools.uploadPhoto(input, photo);
            if(!this.dao.update(photo))
                throw new Exception("Failed to set image URL");

           return Response.ok(this.dao.getById(photo.getId())).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(500).build();
        }
    }

    /**
     * Get all Photo's of a specific User.
     * This is done by providing a valid user id
     */
    public Response getByUserId(int userId) {
        try {
            this.dao = new PhotoDao();
            if (userId == 0)
                throw new Exception("You must provide a User id");
            return Response.ok(dao.getByUserId(userId)).status(200).build();
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response getById(int photoId) {
        try {
            this.dao = new PhotoDao();
            if (photoId == 0)
                throw new Exception("You must provide a Photo id");
            return Response.ok(dao.getById(photoId)).status(200).build();
        } catch (Exception e) {

            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response getByGalleryId(int galleryId, int page, int limit) {
        try {
            int offset;
            if (galleryId < 1 || limit < 1 || page < 1)
                throw new Exception("You must provide a gallery id and pagination data");
            if (page == 1) {
                offset = 0;
            } else {
                offset = (page - 1) * limit;
            }
            this.dao = new PhotoDao();
            Map<Integer, Object> photoData = dao.getByGalleryId(galleryId, limit, offset);

            if (galleryId == 0)
                throw new Exception("You must provide a Gallery id");
           // return Response.ok(dao.getByGalleryId(galleryId, limit, offset)).status(200).build();
            return Response.ok(photoData.get(1))
                    .header("X-Pagination-Count", photoData.get(2))
                    .header("X-Pagination-Limit", limit)
                    .header("X-Pagination-Page", page)
                    .status(200)
                    .build();

        } catch (Exception e) {

            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response delete(int photoId) {
        try {
            this.dao = new PhotoDao();
            if (photoId == 0)
                throw new Exception("You must provide a Photo id");

            if (dao.delete(photoId)) {
                return Response.ok(new ResponseMessage("Photo with id " + photoId + " was deleted")).status(200).build();
            } else {
                return Response.ok(new ResponseMessage("Can't delete Photo with id " + photoId)).status(204).build();
            }

        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    public Response update(PhotoModel photo, int oldGalleryId) {
        try {

            this.dao = new PhotoDao();
            // check if photo has all required params
            if (!PhotoTools.checkInsert(photo))
                throw new Exception("You must provide a photo id");

            // if gallery id was changed we need to move the photo file in a directory corresponding to new gallery id
            if(oldGalleryId != photo.getGalleryId()){
                PhotoTools.movePhotoToGallery(photo.getUrl(), oldGalleryId, photo.getGalleryId(), photo.getUserId());
            }
            // check if update was success or fail
            if (dao.update(photo)) {
                return Response.ok(new ResponseMessage("Photo with id " + photo.getId() + " was updated")).status(200).build();
            } else {
                return Response.ok(new ResponseMessage("Can't update Photo with id " + photo.getId())).status(204).build();
            }
        } catch (Exception e) {
            return Response.ok(new ResponseMessage(e.getMessage())).status(400).build();
        }
    }

    private void incrementViews(int imageId) {
        try {
            this.dao = new PhotoDao();
            this.dao.incrementHits(imageId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Response getImage (int userId, int photoId, int galleryId, String quality, String fileName) {
        if(quality.equals("hd"))
            incrementViews(photoId);

        return Response.ok(PhotoTools.getImage(quality,
                userId,
                galleryId,
                fileName))
                .status(200)
                .build();
    }


}
