package com.gulnara.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import com.gulnara.internship.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security filters for test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    //  REGISTER: success case (201 CREATED)
    @Test
    void register_returns201_andMessage_onSuccess() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto("newuser", "plain123");
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(new User("newuser", "ENC_plain123"));


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    //  REGISTER: username already exists (400 BAD_REQUEST)
    @Test
    void register_returns400_whenUsernameExists() throws Exception {
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new IllegalArgumentException("Username already exists"));

        UserRegistrationDto dto = new UserRegistrationDto("existingUser", "12345");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    //  LOGIN: success case (200 OK)
    @Test
    void login_returns200_onValidCredentials() throws Exception {
        User u = new User("john", "ENC_1234");
        when(userService.findByUsername("john")).thenReturn(u);
        when(passwordEncoder.matches("1234", "ENC_1234")).thenReturn(true);

        UserLoginDto dto = new UserLoginDto("john", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful!"));
    }

    //  LOGIN: invalid password (401 UNAUTHORIZED)
    @Test
    void login_returns401_onInvalidPassword() throws Exception {
        User u = new User("john", "ENC_1234");
        when(userService.findByUsername("john")).thenReturn(u);
        when(passwordEncoder.matches("wrongpass", "ENC_1234")).thenReturn(false);

        UserLoginDto dto = new UserLoginDto("john", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid password"));
    }

    //  LOGIN: user not found (401 UNAUTHORIZED)
    @Test
    void login_returns401_onUnknownUser() throws Exception {
        when(userService.findByUsername("unknown")).thenReturn(null);

        UserLoginDto dto = new UserLoginDto("unknown", "123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }
}
/* Общая идея
Этот код - тестирует AuthController (тот, где /register и /login).
Он проверяет, что:
когда ты отправляешь правильный запрос (POST),
контроллер (AuthController)
возвращает правильный HTTP-ответ (201, 400, 401, 200) и правильное сообщение.
Тесты делают это автоматически, без запуска сервера.

Этот тест - это автоматическая проверка AuthController.
Он имитирует 5 реальных ситуаций:

      Сценарий	                                    Ожидаемый ответ	                            HTTP статус
Регистрация успешна	                            "User registered successfully"	              201 (Created)
Имя уже существует                          	"Username already exists"	                  400 (Bad Request)
Логин успешный	                                "Login successful"	                          200 (OK)
Неверный пароль	                                "Invalid password"	                          401 (Unauthorized)
Пользователь не найден	                        "Invalid username or password"	              401 (Unauthorized)
 */
