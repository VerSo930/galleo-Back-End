package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.GalleryModel;
import com.vuta.model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class GalleryDao {

    private static Connection connection;
    private PreparedStatement ps;

    private ArrayList<GalleryModel> galleryList = new ArrayList<>();
    private GalleryModel gallery;

    public GalleryDao() throws Exception {
        // get a connection from tomcat pool
        connection = Database.getConnection();
    }

    public ArrayList<GalleryModel> getAll() throws Exception {

        GalleryModel galleryModel;

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Gallery");
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                galleryModel = mapGallery(rs);
                galleryModel.setPhotos(new PhotoDao().getByGalleryId(galleryModel.getId()));
                galleryList.add(galleryModel);
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return galleryList;
    }

    public GalleryModel insert(GalleryModel gallery) throws Exception {

        try {
            // prepare  statement
            ps = connection.prepareStatement("INSERT INTO Gallery (userId, name, description, updatedAt, " +
                    "isPrivate, coverImage, views) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            // map user to Prepared Statement
            mapGalleryToPs(gallery);

            // execute query and get the result set
             ps.executeQuery();
            ResultSet rs =ps.getGeneratedKeys();

            // get inserted row id and set to it user object
            while (rs.next()) {
                gallery.setId(rs.getInt(1));
            }

            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return gallery;
    }


    public GalleryModel getById(int galleryId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Gallery WHERE id=?");
            ps.setInt(1,galleryId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
               this.gallery = mapGallery(rs);
               this.gallery.setPhotos(new PhotoDao().getByGalleryId(this.gallery.getId()));
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return gallery;
    }

    public ArrayList<GalleryModel> getByUserId(int userId) throws Exception {
        GalleryModel galleryModel;
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Gallery WHERE userId=?");
            ps.setInt(1, userId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                galleryModel = mapGallery(rs);
                galleryModel.setPhotos(new PhotoDao().getByGalleryId(galleryModel.getId()));
                galleryList.add(galleryModel);
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
        return galleryList;
    }

    public void delete(int galleryId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("DELETE FROM Gallery WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, galleryId);

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("The gallery that you try to delete don't exist");
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
    }

    public void update(GalleryModel gallery) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("UPDATE Gallery SET name=?, description=?, isPrivate=?, coverImage=?," +
                    "views=?  WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // gallery object to Prepared Statement
            ps.setString(1, gallery.getName());
            ps.setString(2, gallery.getDescription());
            ps.setBoolean(3, gallery.isPrivate());
            ps.setInt(4, gallery.getCoverImage());
            ps.setInt(5, gallery.getViews());
            ps.setInt(6, gallery.getId());

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("Gallery not deleted");
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
    }

    private GalleryModel mapGallery(ResultSet rs) throws Exception {

        GalleryModel gallery = new GalleryModel();
        // Map all query's columns to UserModel

            gallery.setId(rs.getInt("id"));
            gallery.setUserId(rs.getInt("userId"));
            gallery.setName(rs.getString("name"));
            gallery.setDescription(rs.getString("description"));
            gallery.setCreatedAt(rs.getTimestamp("createdAt").getTime());
            gallery.setUpdatedAt(rs.getTimestamp("updatedAt").getTime());
            gallery.setIsPrivate(rs.getBoolean("isPrivate"));
            gallery.setCoverImage(rs.getInt("coverImage"));
            gallery.setViews(rs.getInt("views"));

        return gallery;
    }

    private void mapGalleryToPs(GalleryModel gallery) throws Exception {
        try {
            ps.setInt(1, gallery.getUserId());
            ps.setString(2, gallery.getName());
            ps.setString(3, gallery.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(5, gallery.isPrivate());
            ps.setInt(6, gallery.getCoverImage());
            ps.setInt(7, gallery.getViews());
        } catch (SQLException e) {
            throw new Exception("One or more user properties are not provided");
        }
    }
}
