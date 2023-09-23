package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.Register;

import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.repository.UserRepository;
import com.skypro.Marketplace.service.AuthService;
import com.skypro.Marketplace.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(UserDetailsManager manager,
                           PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.manager = manager;
        this.encoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    @Override
    public boolean register(Register register) {
        if (manager.userExists(register.getUsername())) {
            return false;
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

        manager.createUser(new SecurityUser(user.getId(), user.getEmail(),user.getPassword(),user.getFirstName(),user.getLastName(),user.getPhone(),user.getRole()));

        return true;
    }

}