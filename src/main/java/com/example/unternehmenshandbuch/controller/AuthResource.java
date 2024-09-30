package com.example.unternehmenshandbuch.controller;


import com.example.unternehmenshandbuch.controller.dto.ArticleResponseDto;
import com.example.unternehmenshandbuch.controller.dto.LoginForm;
import com.example.unternehmenshandbuch.model.AppUser;
import com.example.unternehmenshandbuch.service.dto.ArticleRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Validated
public interface AuthResource {

    @PostMapping("/register")
    ResponseEntity<AppUser> createUser(@RequestBody AppUser appUser);

    @PostMapping("/authenticate")
    ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginForm loginForm);

}
