package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.NewPassword;
import com.skypro.Marketplace.dto.user.UpdateUser;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.mapper.UserMapper;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    public boolean changePassword(Integer userId, NewPassword newPassword) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

            if (passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
                userRepository.save(user);
            } else {
                throw new IllegalArgumentException("Current password is incorrect");
            }
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with id: {}", userId);
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Current password is incorrect for user with id {}: {}", userId, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("An error occurred while changing password for user with id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to change password.", e);
        }
        return true;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }
    public Integer getUserIdByUsername(String username) {


        User user = userRepository.findByEmail(username);

        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }
    public UserDTO updateUserProfile(Integer userId, UpdateUser updateUser) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));


            user.setFirstName(updateUser.getFirstName());
            user.setLastName(updateUser.getLastName());
            user.setPhone(updateUser.getPhone());

            user = userRepository.save(user);

            return userMapper.userToUserDTO(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with id: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating user profile for id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update user profile.", e);
        }
    }

    public void updateProfileImage(Integer userId, MultipartFile image) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));


            String imagePath = "путь/к/папке/с/изображениями";

            String fileName = userId + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(imagePath, fileName);

            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(image.getBytes());
            }

            user.setImage(fileName);

            userRepository.save(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with id: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while updating profile image for user with id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update profile image.", e);
        }
    }





    public List<UserDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream().map(userMapper::userToUserDTO).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("An error occurred while getting all users: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve users.", e);
        }
    }

    public UserDTO getUserById(Integer userId) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
            return userMapper.userToUserDTO(user);
        } catch (UsernameNotFoundException e) {
            logger.error("User not found with id: {}", userId);
            throw e;
        } catch (Exception e) {
            logger.error("An error occurred while getting user by id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to retrieve user.", e);
        }
    }

    public String getUsernameByUserId(Integer userId) {
        Optional<com.skypro.Marketplace.entity.User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getEmail();
        }
        return "Пользователь не найден";
    }


}
