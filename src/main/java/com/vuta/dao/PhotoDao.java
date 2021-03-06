package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.PhotoModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vuta on 25/09/2017.
 * PhotoDao METHODS:
 *    getAll() : ArrayList<PhotoModel>
 *    getById(int: photoId) : PhotoModel
 *    getByGalleryId(int: galleryId) : ArrayList<PhotoModel>
 *    getByUserId(int: userId) : ArrayList<PhotoModel>
 *    insert(PhotoModel: photo) : PhotoModel
 *    delete(int: photoId) : boolean
 *    update(int: photoId) : boolean
 */
public class PhotoDao {

    /**
     * Database Connection
     */
    private Connection connection;
    /**
     * Prepared statement
     */
    private PreparedStatement ps;
    /**
     * Array of photos
     */
    private ArrayList<PhotoModel> photoList = new ArrayList<>();
    /**
     * Temporary photo object
     */
    private PhotoModel photoModel;

    public PhotoDao() throws Exception {
    }

    /**
     * Get all photos from database if they are enabled
     * @return an {@code ArrayList} of type PhotoModel
     * @throws Exception if something goes wrong with Mysql Query
     */
    public Map<Integer, Object> getAll(int limit, int offset) throws Exception {
        Map<Integer, Object> map = new HashMap<>();
        long count = 0;
        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("SELECT *, COUNT(DISTINCT p1.id)" +
                    " AS total FROM Photos p1" +
                    "   LEFT JOIN Photos p2 ON p2.isPrivate = 0 WHERE p1.isPrivate = 0 GROUP BY 1 LIMIT ? OFFSET ?");
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            int i = 0;
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                photoList.add(mapRsToPhoto(rs));
                count = (long) rs.getObject("total");
            }
            map.put(1, photoList);
            map.put(2, count);
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return map;
    }

    /**
     * Get photo that have specified id
     * @param photoId the {@code int} of photo
     * @return an object {@code PhotoModel} that contains all photo informations
     * @throws Exception if something goes wrong with Mysql Query
     */
    public PhotoModel getById(int photoId) throws Exception {
        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("SELECT * FROM Photos WHERE id=?");
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

    /**
     * Get photos with specified galleryId
     * @param galleryId the {@code int} of gallery
     * @return a map {@code Hasmap<PhotoModel>} with all photos and count
     * that have specified gallery id, if there are no photos return an empty ArrayList
     * @throws Exception
     */
    public Map<Integer, Object> getByGalleryId(int galleryId, int limit, int offset) throws Exception {

        long count = 0;
        Map<Integer, Object> map = new HashMap<>();

        try {

            // prepare  statement
            ps = Database.getConnection().prepareStatement("SELECT *, (SELECT count(*) FROM Photos where galleryId = ?)" +
                    " AS total FROM Photos p WHERE p.galleryId = ? LIMIT ? OFFSET ?");
            ps.setInt(1,galleryId);
            ps.setInt(2,galleryId);
            ps.setInt(3,limit);
            ps.setInt(4,offset);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                this.photoList.add(mapRsToPhoto(rs));
                count = (long) rs.getObject("total");
            }
            map.put(1, photoList);
            map.put(2, count);
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return map;
    }

    /**
     * Get a list of photos with specified userId
     * @param userId the {@code int} of specific user
     * @return an {@code ArrayList} with all photos that have specified userId,
     * if there are no photos an empty array will be returned
     * @throws Exception if something goes wrong with Mysql Query or Mapping {@code ResultSet} to {@code User}
     */
    public ArrayList<PhotoModel> getByUserId(int userId) throws Exception {
        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("SELECT * FROM Photos WHERE userId=?");
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

    /**
     * Insert new Photo
     * @param photo the {@code PhotoModel} object
     * @return an {@code PhotoModel} that contain the new photo data including the Generated ID
     * @throws Exception if something goes wrong with Mysql Query
     */
    public PhotoModel insert(PhotoModel photo) throws Exception {

        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("INSERT INTO Photos (userId, galleryId, name, description, updatedAt, " +
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

    /**
     * Delete photo from Database with specified ID
     * @param photoId the {@code int} ID of photo
     * @throws Exception
     */
    public boolean delete(int photoId) throws Exception {
        int count;
        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("DELETE FROM Photos WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, photoId);

            // execute query and get the result set
            count = ps.executeUpdate();
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
        return count != 0;
    }

    public void incrementHits(int photoId) throws Exception {
        ps = Database.getConnection().prepareStatement("UPDATE Photos SET views=views+1 WHERE id =?", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, photoId);
        ps.executeUpdate();
        ps.close();

        Database.close(connection);
    }

    public boolean update(PhotoModel photo) throws Exception {
        int count;
        try {
            // prepare  statement
            ps = Database.getConnection().prepareStatement("UPDATE Photos SET galleryId=?, name=?, description=?, updatedAt=?, isPrivate=?, url=?," +
                    "views=?  WHERE id=?", Statement.RETURN_GENERATED_KEYS);
            mapPhotoToPs(photo);
            // photoModel object to Prepared Statement
            ps.setInt(1, photo.getGalleryId());
            ps.setString(2, photo.getName());
            ps.setString(3, photo.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(5, photo.getIsPrivate());
            ps.setString(6, photo.getUrl());
            ps.setInt(7, photo.getViews());
            ps.setInt(8, photo.getId());

            // execute query and get the result set
            count = ps.executeUpdate();

            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DAO ERROR:" + e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
        return count != 0;
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
        photo.setViews(rs.getInt("views"));
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
            ps.setBoolean(6, photo.getIsPrivate());
            ps.setString(7, photo.getUrl());
            ps.setInt(8, photo.getViews());
        } catch (SQLException e) {
            throw new Exception("One or more PhotoModel properties are not provided");
        }
    }
}
