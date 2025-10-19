package com.gulnara.internship.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {

    @NotBlank(message = "Email cannot ne empty")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    // Default constructor (required by Spring)
    public UserLoginDto() {}

    // Constructor for manual creation (e.g. in tests)
    public UserLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
