package com.bankapp.dao;

import com.bankapp.model.User;

public interface UserDAO {

    // Validate login credentials
    User login(String username, String password);

    // Create new user
    boolean createUser(User user);

    // Check if username already exists
    boolean usernameExists(String username);

    // Get user by ID
    User getUserById(int userId);
}