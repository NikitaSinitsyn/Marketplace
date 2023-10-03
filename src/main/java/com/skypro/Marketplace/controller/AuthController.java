package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.user.Login;
import com.skypro.Marketplace.dto.user.Register;
import com.skypro.Marketplace.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user authentication and registration.
 */
@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint for user login.
     *
     * @param login Login request containing username and password.
     * @return ResponseEntity indicating success or failure of the login operation.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        if (authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Endpoint for user registration.
     *
     * @param register Registration request containing user details.
     * @return ResponseEntity indicating success or failure of the registration operation.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Register register) {
        if (authService.register(register)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
