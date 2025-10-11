package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import jakarta.validation.constraints.NotBlank;

public interface UserService {
    User registerUser(UserRegistrationDto userData) throws IllegalArgumentException;

    User findByUsername(String username);
}
