package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import java.util.Optional;

public interface UserService {
    User registerUser(UserRegistrationDto userData);
    Optional<User> findByUsername(String username);
}
