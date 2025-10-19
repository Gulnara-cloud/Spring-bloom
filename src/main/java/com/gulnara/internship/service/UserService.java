package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserLoginDto;
import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;

import java.util.Optional;

public interface UserService {

    User registerUser(UserRegistrationDto userData);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean checkPassword(String rawPassword, String encodedPassword);

    boolean loginUser(UserLoginDto dto);
}
