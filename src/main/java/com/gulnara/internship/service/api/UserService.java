package com.gulnara.internship.service.api;

import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User registerUser(UserRegistrationDto userData);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean checkPassword(String rawPassword, String encodedPassword);

    boolean loginUser(UserLoginDto dto);

    User loadUserByUsername(String username);

    UUID getUserIdByUsername(String username);
}