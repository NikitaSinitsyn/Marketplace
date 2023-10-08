package com.skypro.Marketplace.service.impl;

import com.skypro.Marketplace.dto.user.NewPassword;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.dto.user.UpdateUser;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.exception.UserNotFoundException;
import com.skypro.Marketplace.mapper.UserMapper;
import com.skypro.Marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Service for managing users.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Value("${image.upload.path}")
    private String imagePath;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * Change a user's password.
     *
     * @param userId      User's identifier.
     * @param newPassword New password.
     * @return true if the password is successfully changed, otherwise false.
     */
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

    /**
     * Get user information by their username (email).
     *
     * @param username User's username (email).
     * @return User information.
     */
    public UserDTO getUserByUsername(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.userToUserDTO(user);
    }

    /**
     * Update a user's profile.
     *
     * @param userId     User's identifier.
     * @param updateUser New user data.
     * @return Updated user data.
     */
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

    /**
     * Update a user's profile image.
     *
     * @param userId User's identifier.
     * @param image  New profile image.
     * @return User information with the updated image.
     */
    @Transactional
    public UserDTO updateProfileImage(Integer userId, MultipartFile image) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (imagePath != null && !imagePath.isEmpty()) {
            String fileName = userId + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(imagePath, fileName);

            try {
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                user.setImage(fileName);
                userRepository.save(user);
                return userMapper.userToUserDTO(user);
            } catch (IOException e) {
                logger.error("Error writing image file for user with id {}: {}", userId, e.getMessage());
                throw new RuntimeException("Failed to write image file.", e);
            }
        } else {
            throw new RuntimeException("Image upload path is not configured.");
        }
    }

    /**
     * Load a user by their username (email).
     *
     * @param email User's username (email).
     * @return UserDetails for the specified user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(SecurityUser::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Retrieves a user's image as a byte array.
     *
     * @param userId The ID of the user for whom to retrieve the image.
     * @param imageName The name of the user's image.
     * @return A byte array containing the user's image data, or the default image data if the image is not found or an error occurs.
     */
    @Transactional
    public byte[] getUserImage(Integer userId, String imageName) {

        Path imageFilePath = Paths.get(imagePath, userId + "_" + imageName);

        if (!Files.exists(imageFilePath)) {
            return getDefaultImageBytes();
        }

        try (InputStream inputStream = Files.newInputStream(imageFilePath)) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            logger.error("Error reading user image: {}", e.getMessage());
            return getDefaultImageBytes();
        }
    }

    /**
     * Retrieves the default image as a byte array.
     *
     * @return The default image as a byte array.
     */
    public byte[] getDefaultImageBytes() {
        return null;
    }
}
