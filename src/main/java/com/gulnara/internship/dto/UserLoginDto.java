package com.gulnara.internship.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDto {
    private String username;
    private String password;

    public UserLoginDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public UserLoginDto() {}
}
