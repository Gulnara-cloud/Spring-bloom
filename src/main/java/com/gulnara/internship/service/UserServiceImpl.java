package com.gulnara.internship.service;

import com.gulnara.internship.dto.UserLoginDto;
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
    public User registerUser(UserRegistrationDto userData) throws IllegalArgumentException {

        // Step 1: check if username exists
        if (userRepository.findByUsername(userData.getUsername()) !=null) {
            throw new IllegalArgumentException("Username already exists");
        }
        // Step 2: Encode password before saving
        String hashedPassword = passwordEncoder.encode(userData.getPassword());

        // Step 3: Create new user and save
        User user = new User(userData.getUsername(), hashedPassword, userData.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean loginUser(UserLoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername());
        if (user == null) {
            return false;
        }
        return passwordEncoder.matches(dto.getPassword(), user.getPasswordHash());
    }
}
