package com.skypro.Marketplace.service;

import com.skypro.Marketplace.dto.user.Register;

/**
 * This interface defines the contract for user authentication and registration services.
 * Implementing classes should provide methods for user login and registration.
 */
public interface AuthService {

    /**
     * Attempts to authenticate a user with the provided username and password.
     *
     * @param userName The username of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return true if authentication is successful, false otherwise.
     */
    boolean login(String userName, String password);

    /**
     * Registers a new user with the provided registration details.
     *
     * @param register A Register object containing user registration details.
     * @return true if registration is successful, false otherwise.
     */
    boolean register(Register register);
}
