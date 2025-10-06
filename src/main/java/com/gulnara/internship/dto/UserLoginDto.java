package com.gulnara.internship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
    private String username;
    private String password;

    // Default constructor (required by Spring)
    public UserLoginDto() {}

    // Constructor for manual creation (e.g. in tests)
    public UserLoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
