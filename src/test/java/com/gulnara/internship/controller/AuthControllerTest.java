package com.gulnara.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.exception.GlobalExceptionHandler;
import com.gulnara.internship.model.User;
import com.gulnara.internship.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)

class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    //  REGISTER: success (201)
    @Test
    void register_returns201_andMessage_onSuccess() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("newuser", "plain123", "newuser@example.com");
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(new User("newuser", "ENC_plain123","newuser@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"));
    }

    //  REGISTER: username already exists (400)
    @Test
    void register_returns400_whenUsernameExists() throws Exception {
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        UserRegistrationDto dto = new UserRegistrationDto("existingUser", "123456","existingUser@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    //  LOGIN: success (200)
    @Test
    void login_returns200_onValidCredentials() throws Exception {
        User u = new User("john", "ENC_1234","john@example.com");
        when(userService.findByUsername("john")).thenReturn(u);
        when(passwordEncoder.matches("1234", "ENC_1234")).thenReturn(true);

        UserLoginDto dto = new UserLoginDto("john", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    //  LOGIN: invalid password (401)
    @Test
    void login_returns401_onInvalidPassword() throws Exception {
        User u = new User("john", "ENC_1234","john@example.com");
        when(userService.findByUsername("john")).thenReturn(u);
        when(passwordEncoder.matches("wrongpass", "ENC_1234")).thenReturn(false);

        UserLoginDto dto = new UserLoginDto("john", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid password"));
    }

    //  LOGIN: user not found (401)
    @Test
    void login_returns401_onUnknownUser() throws Exception {
        when(userService.findByUsername("unknown")).thenReturn(null);

        UserLoginDto dto = new UserLoginDto("unknown", "123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }
}

