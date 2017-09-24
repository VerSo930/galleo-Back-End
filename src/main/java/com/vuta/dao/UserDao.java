package com.vuta.dao;

import com.vuta.helpers.Database;
import com.vuta.model.RoleModel;
import com.vuta.model.UserModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by verso_dxr17un on 9/23/2017.
 */
public class UserDao {

    private static Connection connection;
    private PreparedStatement ps;

    private ArrayList<UserModel> usersList = new ArrayList<>();

    public UserDao() throws Exception {
        // get a connection from tomcat pool
        connection = Database.getConnection();
    }

    public ArrayList<UserModel> getAll() throws Exception {

        try {
            // prepare  statement
            ps = connection.prepareStatement("SELECT * FROM User");
            // execute query and get the result set
            ResultSet rs = ps.executeQuery();
            // loop trough result set,
            // map each row to a new user object and add it to user ArrayList
            while (rs.next()) {
                usersList.add(mapUser(rs));
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


    public UserModel getById(UserModel user) throws Exception {
        return new UserModel(1, "Alex", "Vuta", "verso.930@gmail.com", "VerSo930",
                "hbsc", new Date().getTime(), new Date().getTime(), true, 1, 1);
    }

    public UserModel add(UserModel user) {
        return new UserModel(1, "Alex", "Vuta", "verso.930@gmail.com", "VerSo930",
                "hbsc", new Date().getTime(), new Date().getTime(), true, 1, 1);
    }

    public void delete(int id) throws Exception {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        } finally {
            // put back connection in tomcat pool
            Database.close(connection);
        }
    }

    public void update(UserModel user) throws Exception {

    }

    private UserModel mapUser(ResultSet rs) throws Exception {

        UserModel user = new UserModel();
        // Map all query's columns to UserModel
        while (rs.next()) {
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setUserName(rs.getString("userName"));
            user.setCreatedAt(rs.getTimestamp("createdAt").getTime());
            user.setCreatedAt(rs.getTimestamp("lastActivity").getTime());
            user.setEnabled(rs.getBoolean("isEnabled"));
            user.setAvatar(rs.getInt("avatar"));
            user.setRole(rs.getInt("avatar"));
        }
        return user;
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
