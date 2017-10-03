package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.GalleryModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    public Map<Integer, Object> getAll(int limit, int offset) throws Exception {

        GalleryModel galleryModel;
        Map<Integer, Object> map = new HashMap<>();
        long count = 0;

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT *, (SELECT count(*) FROM Gallery)" +
                    " AS total FROM Gallery LIMIT ? OFFSET ?");
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                galleryModel = mapGallery(rs);
                galleryModel.setPhotos(new PhotoDao().getByGalleryId(galleryModel.getId()));
                if(rs.getInt(8) != 0)
                    galleryModel.setCoverImage(new PhotoDao().getById(rs.getInt(8)));
                galleryList.add(galleryModel);
                count = (long) rs.getObject("total");

            }
            map.put(1, galleryList);
            map.put(2, count);
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }

        return map;
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

    public Map<Integer, Object> getByUserId(int userId, int limit, int offset) throws Exception {

        GalleryModel galleryModel;
        Map<Integer, Object> map = new HashMap<>();
        long count = 0;

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT *, (SELECT count(*) FROM Gallery where userId = ?)" +
            " AS total FROM Gallery g WHERE g.userId = ? LIMIT ? OFFSET ?");
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                galleryModel = mapGallery(rs);
                galleryModel.setPhotos(new PhotoDao().getByGalleryId(galleryModel.getId()));
                galleryList.add(galleryModel);
                count = (long) rs.getObject("total");
            }
            map.put(1, galleryList);
            map.put(2, count);
            // close prepared statement
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
        return map;
    }

    public int delete(int galleryId) throws Exception {
        int count;
        try {
            // prepare  statement
            ps = connection.prepareStatement("DELETE FROM Gallery WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, galleryId);

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
        return count;
    }

    public void update(GalleryModel gallery) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("UPDATE Gallery SET name=?, description=?, isPrivate=?, coverImage=?," +
                    "views=?  WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // gallery object to Prepared Statement
            ps.setString(1, gallery.getName());
            ps.setString(2, gallery.getDescription());
            ps.setBoolean(3, gallery.getIsPrivate());
            ps.setInt(4, gallery.getCoverImage().getId());
            ps.setInt(5, gallery.getViews());
            ps.setInt(6, gallery.getId());

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("Gallery not updated");
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

    private int count(int userId) throws Exception {
        int count;
        try {
            // prepare  statement
            StringBuilder sql = new StringBuilder().append("SELECT count(*) FROM Gallery");
            if (userId != 0) {
                sql.append(" WHERE userId=?");
            }
            ps = connection.prepareStatement("SELECT count(*) FROM Gallery", Statement.RETURN_GENERATED_KEYS);

            if (userId != 0) {
              ps.setInt(1, userId);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            } else {
                throw new Exception("Database error");
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
        return count;

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
            //gallery.setCoverImage(rs.getInt("coverImage"));
            gallery.setViews(rs.getInt("views"));

        return gallery;
    }

    private void mapGalleryToPs(GalleryModel gallery) throws Exception {
        try {
            ps.setInt(1, gallery.getUserId());
            ps.setString(2, gallery.getName());
            ps.setString(3, gallery.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(5, gallery.getIsPrivate());
            ps.setInt(6, gallery.getCoverImage().getId());
            ps.setInt(7, gallery.getViews());
        } catch (SQLException e) {
            throw new Exception("One or more user properties are not provided");
        }
    }
}
