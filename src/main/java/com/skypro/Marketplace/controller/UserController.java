package com.skypro.Marketplace.controller;

import com.skypro.Marketplace.dto.user.NewPassword;
import com.skypro.Marketplace.dto.user.SecurityUser;
import com.skypro.Marketplace.dto.user.UpdateUser;
import com.skypro.Marketplace.dto.user.UserDTO;
import com.skypro.Marketplace.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PutMapping("/setPassword")
    @PreAuthorize("(#authentication.principal.id == #securityUser.id) or hasRole('ADMIN')")
    public ResponseEntity<?> setPassword(@RequestBody NewPassword newPassword, Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        boolean passwordChanged = userService.changePassword(securityUser.getId(), newPassword);
        return ResponseEntity.status(HttpStatus.OK).body(passwordChanged);

    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UserDTO user = userService.getUserByUsername(securityUser.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(user);


    }


    @PatchMapping("/me")
    public ResponseEntity<UpdateUser> updateUser(@RequestBody UpdateUser updateUser, Authentication authentication) {

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UpdateUser updatedUserProfile = userService.updateUserProfile(securityUser.getId(), updateUser);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUserProfile);


    }


    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateUserImage(@RequestParam("image") MultipartFile image, Authentication authentication) {


        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        UserDTO currentUser = userService.updateProfileImage(securityUser.getId(), image);
        return ResponseEntity.status(HttpStatus.OK).body(currentUser);
    }
}
