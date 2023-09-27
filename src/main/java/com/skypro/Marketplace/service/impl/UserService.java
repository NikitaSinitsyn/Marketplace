package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.NewPassword;
import com.skypro.Marketplace.dto.user.UpdateUser;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.ForbiddenException;
import com.skypro.Marketplace.exception.UnauthorizedException;
import com.skypro.Marketplace.exception.UserNotFoundException;
import com.skypro.Marketplace.mapper.UserMapper;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

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

    @Transactional
    public boolean changePassword(Integer userId, NewPassword newPassword) {



                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    if (passwordEncoder.matches(newPassword.getCurrentPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
                        userRepository.save(user);
                        return true;
                    } else {
                        throw new IllegalArgumentException("Current password is incorrect");
                    }
                } else {
                    throw new UsernameNotFoundException("User not found with id: " + userId);
                }


    }

    public UserDTO getUserByUsername(String username) {

        return userRepository.findByEmail(username);
    }


    @Transactional
    public UpdateUser updateUserProfile(Integer userId, UpdateUser updateUser) {


            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            user.setFirstName(updateUser.getFirstName());
            user.setLastName(updateUser.getLastName());
            user.setPhone(updateUser.getPhone());

            user = userRepository.save(user);

            UpdateUser updatedUserProfile = new UpdateUser();
            updatedUserProfile.setFirstName(user.getFirstName());
            updatedUserProfile.setLastName(user.getLastName());
            updatedUserProfile.setPhone(user.getPhone());

            return updatedUserProfile;


    }

    @Transactional
    public UserDTO updateProfileImage(Integer userId, MultipartFile image) {


            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));


            String imagePath = "путь/к/папке/с/изображениями";

            String fileName = userId + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(imagePath, fileName);

            try  {
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setImage(fileName);
                userRepository.save(user);
                return userMapper.userToUserDTO(user);
            } catch (IOException e) {
                logger.error("Error writing image file for user with id {}: {}", userId, e.getMessage());
                throw new RuntimeException("Failed to write image file.", e);
            }


    }


}
