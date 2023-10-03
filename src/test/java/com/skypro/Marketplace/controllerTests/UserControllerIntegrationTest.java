package com.skypro.Marketplace.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.Marketplace.dto.user.*;
import com.skypro.Marketplace.entity.Role;
import com.skypro.Marketplace.entity.User;
import com.skypro.Marketplace.repository.UserRepository;
import com.skypro.Marketplace.service.impl.AuthServiceImpl;
import com.skypro.Marketplace.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthServiceImpl authServiceImpl;
    @Autowired
    private UserRepository userRepository;

    private User createdUser;



    @BeforeEach
    public void setUp() {
        Register register = new Register("testUsername", "testPassword", "testFirstName", "testLastName", "testPhone", Role.USER);
        authServiceImpl.register(register);
        createdUser = userRepository.findByEmail(register.getUsername()).orElse(null);
    }

    @Test
    public void testSetPassword() throws Exception {
        UserDetails userDetails = userService.loadUserByUsername("testUsername");

        NewPassword newPassword = new NewPassword();
        newPassword.setCurrentPassword("testPassword");
        newPassword.setNewPassword("newPassword");

        SecurityUser securityUser = new SecurityUser(
                createdUser.getId(), createdUser.getEmail(), createdUser.getPassword(),
                createdUser.getFirstName(), createdUser.getLastName(),
                createdUser.getPhone(), createdUser.getRole()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(put("/users/setPassword")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPassword))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testGetUser() throws Exception {

        SecurityUser securityUser = new SecurityUser(
                createdUser.getId(), createdUser.getEmail(), createdUser.getPassword(),
                createdUser.getFirstName(), createdUser.getLastName(),
                createdUser.getPhone(), createdUser.getRole()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockMvc.perform(get("/users/me")
                )
                .andExpect(status().isOk());
    }
    @Test
    public void testUpdateUser() throws Exception {

        UpdateUser updatedUser = new UpdateUser("UpdatedFirstName", "UpdatedLastName", "UpdatedPhone");

        SecurityUser securityUser = new SecurityUser(
                createdUser.getId(), createdUser.getEmail(), createdUser.getPassword(),
                createdUser.getFirstName(), createdUser.getLastName(),
                createdUser.getPhone(), createdUser.getRole()
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Perform the updateUser request
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser))
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("UpdatedFirstName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("UpdatedLastName"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value("UpdatedPhone"));
    }

//    @Test
//    public void testUpdateUserImage() throws Exception {
//        File imageFile = ResourceUtils.getFile("classpath:sample.jpg");
//        FileInputStream input = new FileInputStream(imageFile);
//        MockMultipartFile multipartFile = new MockMultipartFile("image", imageFile.getName(), "image/jpeg", input);
//
//        SecurityUser securityUser = new SecurityUser(
//                createdUser.getId(), createdUser.getEmail(), createdUser.getPassword(),
//                createdUser.getFirstName(), createdUser.getLastName(),
//                createdUser.getPhone(), createdUser.getRole()
//        );
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/users/me/image")
//                        .file(multipartFile)
//
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.image").exists());
//    }
}
