package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.user.NewPassword;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.dto.user.UpdateUser;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for managing users.
 */

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Set a new password for the current user.
     *
     * @param newPassword     Data for setting a new password.
     * @param authentication  Information about the current user's authentication.
     * @return Response about the password change result.
     */
    @PutMapping("/setPassword")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean passwordChanged = userService.changePassword(securityUser.getId(), newPassword);
        return ResponseEntity.status(HttpStatus.OK).body(passwordChanged);

    }

    /**
     * Get information about the current user.
     *
     * @param authentication  Information about the current user's authentication.
     * @return Information about the current user in JSON format.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UserDTO user = userService.getUserByUsername(securityUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(user);

    }

    /**
     * Update information about the current user.
     *
     * @param updateUser      New user data.
     * @param authentication  Information about the current user's authentication.
     * @return Updated user data in JSON format.
     */
    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser, Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UpdateUser updatedUserProfile = userService.updateUserProfile(securityUser.getId(), updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserProfile);

    }

    /**
     * Update the profile image of the current user.
     *
     * @param image           New profile image.
     * @param authentication  Information about the current user's authentication.
     * @return Information about the user with the updated image in JSON format.
     */
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateUserImage(@RequestParam("image") MultipartFile image, Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UserDTO currentUser = userService.updateProfileImage(securityUser.getId(), image);
        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
    }

    /**
     * Retrieves a user's image based on the provided user ID and image name.
     *
     * @param imageName The name of the image.
     * @return ResponseEntity containing the image as a byte array and appropriate HTTP headers.
     */
    @GetMapping(value = "/me/images", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> getUserImage( @PathVariable String imageName, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return userService.getUserImage(securityUser.getId(), imageName);
    }
}
