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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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


    public boolean changePassword(Integer userId, NewPassword newPassword, Authentication authentication) {


            User authenticatedUser = (User) authentication.getPrincipal();


            if (authenticatedUser.getId().equals(userId)) {
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
            } else {

                throw new ForbiddenException("Access forbidden to change password for another user.");
            }

    }

    public UserDTO getUserByUsername(String username, Authentication authentication) {

        return userRepository.findByEmail(username);
    }


    public UpdateUser updateUserProfile(Integer userId, UpdateUser updateUser, Authentication authentication) {


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

    public UserDTO updateProfileImage(Integer userId, MultipartFile image, Authentication authentication) {


            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));


            String imagePath = "путь/к/папке/с/изображениями";

            String fileName = userId + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(imagePath, fileName);

            try (OutputStream os = Files.newOutputStream(filePath)) {
                os.write(image.getBytes());
            } catch (IOException e) {
                logger.error("Error writing image file for user with id {}: {}", userId, e.getMessage());
                throw new RuntimeException("Failed to write image file.", e);
            }

            user.setImage(fileName);

            userRepository.save(user);
            return userMapper.userToUserDTO(user);

    }


}
