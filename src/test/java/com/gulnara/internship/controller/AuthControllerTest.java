package com.gulnara.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import com.gulnara.internship.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    //   Test: Successful registration
    @Test
    void registerUser_returns200_whenValidData() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("gulnara", "123456", "g@example.com");

        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(new User());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    //  Test: Email already exists
    @Test
    void register_returns400_whenEmailExists() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Email already exists"))
                .when(userService).registerUser(any(UserRegistrationDto.class));

        UserRegistrationDto dto = new UserRegistrationDto("gulnara", "123456", "existing@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Email already exists"));
    }

    //  Test: Username already exists
    @Test
    void register_returns400_whenUsernameExists() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Username already exists"))
                .when(userService).registerUser(any(UserRegistrationDto.class));

        UserRegistrationDto dto = new UserRegistrationDto("gulnara", "123456", "g@example.com");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    //  Test: Login successful
    @Test
    void login_returns200_whenCredentialsAreValid() throws Exception {
        Mockito.doReturn(true).when(userService).loginUser(any(UserLoginDto.class));

        UserLoginDto dto = new UserLoginDto("g@example.com", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    //  Test: Login failed
    @Test
    void login_returns400_whenInvalidCredentials() throws Exception {
        Mockito.doReturn(false).when(userService).loginUser(any(UserLoginDto.class));

        UserLoginDto dto = new UserLoginDto("wrong@example.com", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email or password"));
    }
}