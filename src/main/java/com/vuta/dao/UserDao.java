package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.UserModel;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by verso_dxr17un on 9/23/2017.
 * DAO METHODS:
 * getAll() : Arraylist<UserModel>
 * getById(int: id) : UserModel
 * insert(UserModel: user) : UserModel
 * login(String: userName, String: password) : UserModel
 * enableUser(int: userId) : boolean
 * delete(int: userId) : boolean
 */

public class UserDao {

    private static Connection connection;
    private PreparedStatement ps;
    private ArrayList<UserModel> usersList = new ArrayList<>();

    public UserDao() {
    }

    /**
     * Get all users from DATABASE
     */
    public ArrayList<UserModel> getAll() throws Exception {
        UserModel user = new UserModel();
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM User WHERE isEnabled=1");
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                mapUser(rs, user);
                usersList.add(user);
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

        return usersList;
    }

    /**
     * Get user from database with specified ID
     */
    public UserModel getById(int userId) throws Exception {
        UserModel user = new UserModel();
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("SELECT u1.*, COUNT(DISTINCT p.id) as photosCount, " +
                    "COUNT(DISTINCT g.id) as galleriesCount FROM User u1 " +
                    " LEFT JOIN Photos p ON u1.id = p.userId" +
                    " LEFT JOIN Gallery g ON u1.id = g.userId WHERE u1.id=?");
            ps.setInt(1, userId);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                mapUser(rs, user);
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
        return user;
    }

    /**
     * Insert new user into DATABASE
     */
    public UserModel insert(UserModel user) throws Exception {

        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("INSERT INTO User (name, lastName, email, userName, password, " +
                    " isEnabled, avatar, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            // map user to Prepared Statement
            mapUserToPs(user);

            // execute query and get the result set
            ps.executeQuery();
            ResultSet rs = ps.getGeneratedKeys();

            // get inserted row id and set to it user object
            while (rs.next()) {
                user.setId(rs.getInt(1));
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

        return user;
    }

    /**
     * Update user status from DISABLED to ENABLED
     */
    public boolean enableUser(int userId) throws Exception {
        int count;
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("UPDATE User SET isEnabled = 1 WHERE id = ? AND isEnabled=0", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);

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

    /**
     * Get user from DATABASE that have provided
     * username and password
     */
    public UserModel login(String username, String password) throws Exception {
        UserModel user = null;
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("SELECT u1.*, COUNT(DISTINCT p.id) as photosCount, " +
                    "COUNT(DISTINCT g.id) as galleriesCount FROM User u1 " +
                    " LEFT JOIN Photos p ON u1.id = p.userId" +
                    " LEFT JOIN Gallery g ON u1.id = g.userId WHERE userName=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                user = new UserModel();
                mapUser(rs, user);
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

        return user;
    }

    /**
     * Delete user from DATABASE with specific id
     * This method throw exception if something goes wrong with
     * the MySql Query
     */
    public boolean delete(int id) throws Exception {

        int count;
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("DELETE FROM User WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, id);

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

    /**
     * Set user properties from Result Set returned from DATABASE
     */
    private void mapUser(ResultSet rs, UserModel user) throws Exception {

        // Map all query's columns to UserModel
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setLastName(rs.getString("lastName"));
        user.setEmail(rs.getString("email"));
        //user.setPassword(rs.getString("password"));
        user.setUserName(rs.getString("userName"));
        user.setCreatedAt(rs.getTimestamp("createdAt").getTime());
        user.setPhotosCount(rs.getInt("photosCount"));
        user.setGalleriesCount(rs.getInt("galleriesCount"));
        if (rs.getTimestamp("lastActivity") != null)
            user.setLastActivity(rs.getTimestamp("lastActivity").getTime());
        user.setIsEnabled(rs.getBoolean("isEnabled"));
        if(rs.getInt("avatar") != 0)
            user.setAvatar(new PhotoDao().getById(rs.getInt("avatar")));
        user.setRole(rs.getInt("role"));

    }

    /**
     * Set user properties to Prepared Statement
     */
    private void mapUserToPs(UserModel user) throws Exception {
        try {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getUserName());
            ps.setString(5, user.getPassword());
            ps.setBoolean(6, user.getIsEnabled());
            if(user.getAvatar() != null) {
                ps.setInt(7, user.getAvatar().getId());
            } else {
                ps.setInt(7, 0);
            }
            ps.setInt(8, user.getRole());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("One or more user properties are not provided");
        }
    }
}
