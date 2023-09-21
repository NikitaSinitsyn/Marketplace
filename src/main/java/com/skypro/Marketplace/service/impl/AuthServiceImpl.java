package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.Register;
import com.skypro.Marketplace.exception.UserAlreadyExistsException;
import com.skypro.Marketplace.repository.UserRepository;
import com.skypro.Marketplace.service.AuthService;
import com.skypro.Marketplace.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;

    private final UserDetailsManager manager;
    private final UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserDetailsManager manager, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.manager = manager;
        this.userRepository = userRepository;
    }


    @Override
    public boolean login(String userName, String password) {
        User user = userRepository.findByEmail(userName);

        if (user == null) {
            return false;
        }

        if (passwordEncoder.matches(password, user.getPassword())) {
            Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        }

        return false;
    }


    @Override
    public boolean register(Register register) {

        Optional<User> existingUser = Optional.ofNullable(userRepository.findByEmail(register.getUsername()));
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }


        String hashedPassword = passwordEncoder.encode(register.getPassword());

        User user = new User();
        user.setEmail(register.getUsername());
        user.setPassword(hashedPassword);
        user.setRole(register.getRole());
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());
        user.setPhone(register.getPhone());

        userRepository.save(user);

        return true;
    }



}