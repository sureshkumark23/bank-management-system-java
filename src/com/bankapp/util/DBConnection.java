package com.bankapp.util;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    private static final String URL      = "jdbc:mysql://localhost:3306/bankapp";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ DB Connected Successfully!");
        } catch (Exception e) {
            System.out.println("❌ DB Connection Failed: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}