package com.vuta.helpers;

/**
 * Created by Vuta Alexandru on 6/7/2017.
 */

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


public class Database {

    public static Connection getConnection() throws Exception
    {
        Connection conn;
        try {
               Context initContext = new InitialContext();
               Context envContext  = (Context)initContext.lookup("java:/comp/env");
               DataSource datasource = (DataSource)envContext.lookup("jdbc/galleo");
               conn = datasource.getConnection();

           } catch (Exception e) {
            Logger logger = new Logger();
            e.printStackTrace(logger.printStream());
             throw new Exception(e.getMessage());
           }

           return conn;

    }

    public static void close(Connection connection)
    {
        try {
            // If connection exists, close
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger logger = new Logger();
            e.printStackTrace(logger.printStream());
        }
    }



}