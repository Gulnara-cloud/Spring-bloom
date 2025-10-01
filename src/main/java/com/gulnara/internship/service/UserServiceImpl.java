package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserRegistrationDto;
import com.gulnara.internship.model.User;
import com.gulnara.internship.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserRegistrationDto userData) {
        if (userRepository.findByUsername(userData.getUsername()) !=null) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(userData.getUsername());
        user.setPasswordHash(passwordEncoder.encode(userData.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}