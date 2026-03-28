package com.bankapp.dao.impl;

import com.bankapp.dao.UserDAO;
import com.bankapp.model.User;
import com.bankapp.util.DBConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {

    private Connection con;

    public UserDAOImpl() {
        this.con = DBConnection.getInstance().getConnection();
    }

    @Override
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ login failed: " + e.getMessage());
        }
        return null; // wrong credentials
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ createUser failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // true if username found
        } catch (SQLException e) {
            System.out.println("❌ usernameExists failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ getUserById failed: " + e.getMessage());
        }
        return null;
    }
}