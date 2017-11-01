package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.GalleryModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** Persistence of all galleries
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

    /**
     * Get all galeries from database
     * @param limit is the int number that set the limit of results
     * @param offset is the number that start the query after specified position
     * @return {@code Map<Integer, Object>} that contain the list galleries and the count of all galleries
     * @throws Exception
     */
    public Map<Integer, Object> getAll(int limit, int offset) throws Exception {

        GalleryModel galleryModel;
        Map<Integer, Object> map = new HashMap<>();
        long count = 0;

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT g.*, COUNT(DISTINCT p.id) as photosCount, (SELECT count(*) FROM Gallery) as total FROM Gallery g" +
                    "  LEFT JOIN Photos p ON p.galleryId = g.id " +
                    "GROUP BY g.id  ORDER BY g.updatedAt DESC LIMIT ? OFFSET ?");
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                galleryModel = mapGallery(rs);
                galleryModel.setPhotos(null);
                if ((Integer) rs.getObject("coverImage") != 0)
                    galleryModel.setCoverImage(new PhotoDao().getById(rs.getInt((Integer) rs.getObject("coverImage"))));
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

    /**
     *
     * @param gallery object that will be inserted
     * @return {@code GalleryModel} that contain the provided gallery + generated id
     * @throws Exception if something goes wrong with MySql
     */
    public GalleryModel insert(GalleryModel gallery) throws Exception {

        try {
            // prepare  statement
            ps = connection.prepareStatement("INSERT INTO Gallery (userId, name, description, updatedAt, " +
                    "isPrivate, coverImage, views) VALUES (?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            // map user to Prepared Statement
            mapGalleryToPs(gallery);

            // execute query and get the result set
            ps.executeQuery();
            ResultSet rs = ps.getGeneratedKeys();

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

    /**
     *
     * @param galleryId is the id of requested gallery
     * @return {@code GalleryModel} that contain all gallery data
     * @throws Exception if something goes wrong with MySql
     */
    public GalleryModel getById(int galleryId) throws Exception {
        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT g.*, COUNT(DISTINCT p.id) as photosCount, (SELECT count(*) FROM Gallery) as total FROM Gallery g" +
                    "  LEFT JOIN Photos p ON p.galleryId = g.id WHERE g.id = ? GROUP BY g.id  ORDER BY g.updatedAt DESC");
            ps.setInt(1, galleryId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                this.gallery = mapGallery(rs);
                this.gallery.setPhotos(null);
                if ((Integer) rs.getObject("coverImage") != 0)
                    this.gallery.setCoverImage(new PhotoDao().getById((Integer) rs.getObject("coverImage")));
            }
            // close prepared statement
            ps.close();

            ps = connection.prepareStatement("UPDATE Gallery SET views = views+1 WHERE id = ?");
            ps.setInt(1, galleryId);
            ps.executeQuery();

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

    /**
     *
     * @param userId the id of owner user
     * @param limit the limit of rows that will be fetched
     * @param offset the number that start the query after specified position
     * @return {@code Map<Integer, Object>} that contain the list galleries and the count of all galleries
     * @throws Exception
     */
    public Map<Integer, Object> getByUserId(int userId, int limit, int offset) throws Exception {

        GalleryModel galleryModel;
        Map<Integer, Object> map = new HashMap<>();
        long count = 0;

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT g.*, COUNT(DISTINCT p.id) as photosCount, (SELECT count(*) from Gallery where userId= ?) as total FROM Gallery g" +
                    "   LEFT JOIN Photos p ON p.galleryId = g.id " +
                    "   WHERE g.userId = ? GROUP BY g.id  ORDER BY g.updatedAt DESC LIMIT ? OFFSET ?");
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
                galleryModel.setPhotos(null);
                if ((Integer) rs.getObject("coverImage") != 0)
                    galleryModel.setCoverImage(new PhotoDao().getById((Integer) rs.getObject("coverImage")));
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

    /**
     * Delete gallery by id
     * @param galleryId the id of the gallery
     * @return {@code int} the number of rows that where affected
     * @throws Exception
     */
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

    /**
     * Update Gallery
     * @param gallery the gallery object that will be inserted
     * @return {@code int} the number of rows that where affected
     * @throws Exception
     */
    public int update(GalleryModel gallery) throws Exception {
        int count = 0;
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

    /**
     * Map Reult Set to a {@code GalleryModel} object
     * @param rs the result set resulted after query execution
     * @return {@code GalleryModel} the gallery mapped from Result Set
     * @throws Exception if some fields are null
     */
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
        gallery.setPhotosCount(rs.getInt("photosCount"));
        //gallery.setCoverImage(rs.getInt("coverImage"));
        gallery.setViews(rs.getInt("views"));

        return gallery;
    }

    /**
     * Map Gallery object to Prepared statement
     * @param gallery the gallery object
     * @throws Exception if some fields are null
     */
    private void mapGalleryToPs(GalleryModel gallery) throws Exception {
        try {
            ps.setInt(1, gallery.getUserId());
            ps.setString(2, gallery.getName());
            ps.setString(3, gallery.getDescription());
            ps.setTimestamp(4, new java.sql.Timestamp(new Date(System.currentTimeMillis()).getTime()));
            ps.setBoolean(5, gallery.getIsPrivate());
            ps.setInt(6, (gallery.getCoverImage() == null)? 0 : gallery.getCoverImage().getId());
            ps.setInt(7, gallery.getViews());
        } catch (Exception e) {
            throw new Exception("One or more user properties are not provided");
        }
    }
}
