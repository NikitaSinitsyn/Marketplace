package com.skypro.Marketplace.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.Marketplace.controller.AuthController;
import com.skypro.Marketplace.dto.user.Login;
import com.skypro.Marketplace.dto.user.Register;
import com.skypro.Marketplace.entity.Role;
import com.skypro.Marketplace.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    public void testLoginSuccess() throws Exception {
        Login login = new Login("username", "password");

        when(authService.login("username", "password")).thenReturn(true);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(login)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login("username", "password");
        verifyNoMoreInteractions(authService);
    }

    @Test
    public void testLoginUnauthorized() throws Exception {
        Login login = new Login("username", "password");

        when(authService.login("username", "password")).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(login)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login("username", "password");
        verifyNoMoreInteractions(authService);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        Register register = new Register("username", "password", "Nikita", "Sinitsyn", "+7 (123) 456-78-90", Role.USER);

        when(authService.register(register)).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(register)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).register(register);
        verifyNoMoreInteractions(authService);
    }

    @Test
    public void testRegisterBadRequest() throws Exception {
        Register register = new Register("username", "password", "Nikita", "Sinitsyn", "+7 (123) 456-78-90", Role.USER);

        when(authService.register(register)).thenReturn(false);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(register)))
                .andExpect(status().isBadRequest());

        verify(authService, times(1)).register(register);
        verifyNoMoreInteractions(authService);
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}