package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;

public interface UserService {
    User registerUser(UserRegistrationDto userData);
    User findByUsername(String username);
}
