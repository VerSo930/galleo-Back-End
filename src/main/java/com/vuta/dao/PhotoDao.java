package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.PhotoModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vuta on 25/09/2017.
 */
public class PhotoDao {

    private static Connection connection;
    private PreparedStatement ps;
    private ArrayList<PhotoModel> photoList = new ArrayList<>();
    private PhotoModel photoModel;

    public PhotoDao() throws Exception {
        // get a connection from tomcat pool
        connection = Database.getConnection();
    }

    public ArrayList<PhotoModel> getAll() throws Exception {

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Photos");
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            int i = 0;
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                photoList.add(mapRsToPhoto(rs));
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return photoList;
    }

    public PhotoModel insert(PhotoModel photo) throws Exception {

        try {
            // prepare  statement
            ps = connection.prepareStatement("INSERT INTO Photos (userId, galleryId, name, description, updatedAt, " +
                    "isPrivate, url, views) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            // map user to Prepared Statement
            mapPhotoToPs(photo);

            // execute query and get the result set
            ps.executeQuery();
            ResultSet rs =ps.getGeneratedKeys();

            // get inserted row id and set to it user object
            while (rs.next()) {
                photo.setId(rs.getInt(1));
            }

            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return photo;
    }

    public PhotoModel getById(int photoId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Photos WHERE id=?");
            ps.setInt(1,photoId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                this.photoModel = mapRsToPhoto(rs);
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return photoModel;
    }

    public ArrayList<PhotoModel> getByGalleryId(int galleryId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Photos WHERE galleryId=?");
            ps.setInt(1,galleryId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                this.photoList.add(mapRsToPhoto(rs));
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return photoList;
    }

    public ArrayList<PhotoModel> getByUserId(int userId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM Photos WHERE userId=?");
            ps.setInt(1, userId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                photoList.add(mapRsToPhoto(rs));
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
        return photoList;
    }

    public void delete(int photoId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("DELETE FROM Photos WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, photoId);

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("Photo not deleted");
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

    public void update(PhotoModel photo) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("UPDATE Photos SET name=?, description=?, updatedAt=?, isPrivate=?, url=?," +
                    "views=?  WHERE id=?", Statement.RETURN_GENERATED_KEYS);
            mapPhotoToPs(photo);
            // photoModel object to Prepared Statement
            ps.setString(1, photo.getName());
            ps.setString(2, photo.getDescription());
            ps.setTimestamp(3, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(4, photo.isPrivate());
            ps.setString(5, photo.getUrl());
            ps.setInt(6, photo.getViews());

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("Photo not deleted");
            }
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
    }

    private PhotoModel mapRsToPhoto(ResultSet rs) throws Exception {

        PhotoModel photo = new PhotoModel();
        try {
        // Map all query's columns to UserModel
        photo.setId(rs.getInt("id"));
        photo.setUserId(rs.getInt("userId"));
        photo.setGalleryId(rs.getInt("galleryId"));
        photo.setName(rs.getString("name"));
        photo.setDescription(rs.getString("description"));
        photo.setCreatedAt(rs.getTimestamp("createdAt").getTime());
        photo.setUpdatedAt(rs.getTimestamp("updatedAt").getTime());
        photo.setPrivate(rs.getBoolean("isPrivate"));
        photo.setUrl(rs.getString("url"));
        } catch (NullPointerException e) {
            throw new Exception("One or more PhotoModel properties are NULL in database");
        }
        return photo;
    }

    private void mapPhotoToPs(PhotoModel photo) throws Exception {
        try {
            ps.setInt(1, photo.getUserId());
            ps.setInt(2, photo.getGalleryId());
            ps.setString(3, photo.getName());
            ps.setString(4, photo.getDescription());
            ps.setTimestamp(5, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(6, photo.isPrivate());
            ps.setString(7, photo.getUrl());
            ps.setInt(8, photo.getViews());
        } catch (SQLException e) {
            throw new Exception("One or more PhotoModel properties are not provided");
        }
    }
}
