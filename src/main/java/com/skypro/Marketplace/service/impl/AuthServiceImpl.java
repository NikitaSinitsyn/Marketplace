package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.Register;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.UserAlreadyExistsException;
import com.skypro.Marketplace.repository.UserRepository;
import com.skypro.Marketplace.service.AuthService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the AuthService interface for user authentication and registration.
 */
@Service
public class AuthServiceImpl implements AuthService {


    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(
                           PasswordEncoder passwordEncoder, UserRepository userRepository) {

        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    /**
     * Authenticate a user by verifying the provided username and password.
     *
     * @param userName Username provided during login.
     * @param password Password provided during login.
     * @return True if the authentication is successful, false otherwise.
     */
    @Override
    public boolean login(String userName, String password) {
        UserDetails userDetails = userRepository.findByEmail(userName)
                .map(SecurityUser::from)
                .orElse(null);

        return userDetails != null && encoder.matches(password, userDetails.getPassword());
    }

    /**
     * Register a new user.
     *
     * @param register Registration request containing user details.
     * @return True if the registration is successful, false otherwise.
     * @throws UserAlreadyExistsException If a user with the same username already exists.
     */
    @Override
    public boolean register(Register register) {
        Optional<User> existingUser = userRepository.findByEmail(register.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .email(register.getUsername())
                .password(encoder.encode(register.getPassword()))
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .phone(register.getPhone())
                .role(register.getRole())
                .build();
        userRepository.save(user);

        return true;
    }

}