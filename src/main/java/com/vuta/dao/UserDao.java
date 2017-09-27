package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.UserModel;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class UserDao {

    private static Connection connection;
    private PreparedStatement ps;

    private ArrayList<UserModel> usersList = new ArrayList<>();

    public UserDao() {}

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
            ResultSet rs =ps.getGeneratedKeys();

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

    public int enableUser(int userId) throws Exception {
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
        return count;

    }

    public UserModel getById(int userId) throws Exception {
        UserModel user = new UserModel();
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM User WHERE id=?");
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

    public UserModel login(String username, String password) throws Exception {
        UserModel user = null;
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM User WHERE userName=? AND password=?");
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

    public boolean delete(int id) throws Exception {
        try {
            connection = Database.getConnection();
            // prepare  statement
            ps = connection.prepareStatement("DELETE FROM User WHERE id=?", Statement.RETURN_GENERATED_KEYS);

            // map id to Prepared Statement
            ps.setInt(1, id);

            // execute query and get the result set
            int count = ps.executeUpdate();
            if(count == 0){
                throw new Exception("User not deleted");
            }
            // close prepared statement
            ps.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
    }

    private void mapUser(ResultSet rs, UserModel user) throws Exception {

        // Map all query's columns to UserModel
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            //user.setPassword(rs.getString("password"));
            user.setUserName(rs.getString("userName"));
            user.setCreatedAt(rs.getTimestamp("createdAt").getTime());
            if(rs.getTimestamp("lastActivity") != null)
                user.setLastActivity(rs.getTimestamp("lastActivity").getTime());
            user.setEnabled(rs.getBoolean("isEnabled"));

            user.setAvatar(rs.getInt("avatar"));
            user.setRole(rs.getInt("avatar"));

    }

    private void mapUserToPs(UserModel user) throws Exception {

        try {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getUserName());
            ps.setString(5, user.getPassword());
            ps.setBoolean(6, user.isEnabled());
            ps.setInt(7, user.getAvatar());
            ps.setInt(8, user.getRole());
        } catch (SQLException e) {
            throw new Exception("One or more user properties are not provided");
        }
    }
}
