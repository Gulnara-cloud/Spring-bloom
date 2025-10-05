package com.gulnara.internship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    private String username;
    private String password;

    // Default constructor(required by Spring and for testing)
    public UserRegistrationDto() {
    }

    // Parametrized constructor (optional, useful for manual creation)
    public UserRegistrationDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
