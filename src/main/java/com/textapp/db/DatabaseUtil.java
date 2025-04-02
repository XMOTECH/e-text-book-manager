package com.textapp.db;

import java.sql.*;


public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/cahier_de_texte";
    private static final String USER = "root";
    private static final String PASS = "<mody";

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Connection established");
            return connection;

        } catch (SQLException e) {
            System.out.println("Error connecting to database");
            throw e;
        }
    }
//
//    public static void test() {
//        try (Connection conn = getConnection()) {
//        System.out.println("Connection established");
//        } catch (SQLException e) {
//        System.out.println("Error connecting to database");
//        e.printStackTrace();
//        }
//    }
//    public static void main(String[] args) {
//        DatabaseUtil.test();
//    }
    }