package com.example.unternehmenshandbuch.controller.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginForm {

    private String username;
    private String password;
}
