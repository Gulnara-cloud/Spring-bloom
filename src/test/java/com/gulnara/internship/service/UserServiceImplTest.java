package com.gulnara.internship.service;

import com.gulnara.internship.model.User;
import com.gulnara.internship.repository.UserRepository;
import com.gulnara.internship.dto.UserRegistrationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



// Эта аннотация включает Mockito,
// чтобы можно было использовать «mock»-объекты (подделки).
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    //@Mock создаёт поддельные версии зависимостей
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService; // will be created with mocked dependencies

    //  registerUser: happy path
    @Test
    void registerUser_savesNewUser_andEncodesPassword() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("newuser");
        dto.setPassword("plain123");

        when(userRepository.findByUsername("newuser")).thenReturn(null); //user not exists
        when(passwordEncoder.encode("plain123")).thenReturn("ENC_plain123"); // encoder stub

        // Act
        userService.registerUser(dto);

        // Assert: repo.save called with encoded password and correct username
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("newuser", saved.getUsername(),"username should match");
        assertEquals("ENC_plain123", saved.getPasswordHash(),"password must be encoded");
    }

    // This test verifies that when a username already exists in the database,
    // the registerUser() method throws an IllegalArgumentException
    // and does NOT attempt to save the user again

    //  registerUser: username already exists
    @Test
    void registerUser_throwsException_whenUsernameAlreadyExists() {
        // Arrange
        User existingUser = new User("john", "ENC_123","john@example.com");
        when(userRepository.findByUsername("john")).thenReturn(existingUser);

        UserRegistrationDto dto = new UserRegistrationDto("john", "plain123","john@example.com");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(dto)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // This test verifies that the findByUsername() method correctly
    // returns a User object when the username exists in the repository

    //   registerUser: username already exists
    @Test
    void findByUsername_returnsUser_whenExists() {
        // Arrange
        User u = new User();
        u.setUsername("john");
        when(userRepository.findByUsername("john")).thenReturn(u);

        // Act
        User result = userService.findByUsername("john");

        // Assert
        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    //   findByUsername: not found
    @Test
    void findByUsername_returnsNull_whenNotExists() {
        // Arrange
        when(userRepository.findByUsername("nobody")).thenReturn(null);

        // Act
        User result = userService.findByUsername("nobody");

        // Assert
        assertNull(result);
    }
}

/* UserServiceImpl содержит два метода:
1.registerUser() - регистрирует нового пользователя.
2.findByUsername() - ищет пользователя по имени.

Тест проверяет, что оба метода работают правильно:
-при регистрации пароль шифруется,
-при поиске имя находится,
-ошибки обрабатываются корректно.
 */

