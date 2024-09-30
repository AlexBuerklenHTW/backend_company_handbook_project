package com.example.unternehmenshandbuch.controller;

import com.example.unternehmenshandbuch.controller.dto.LoginForm;
import com.example.unternehmenshandbuch.model.AppUser;
import com.example.unternehmenshandbuch.persistence.AppUserRepository;
import com.example.unternehmenshandbuch.service.AppUserDetailsServiceImpl;
import com.example.unternehmenshandbuch.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthResource {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserDetailsServiceImpl appUserDetailsServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository repository;

    @Autowired
    AuthController(AuthenticationManager authenticationManager, JwtService jwtService, AppUserDetailsServiceImpl appUserDetailsServiceImpl, PasswordEncoder passwordEncoder, AppUserRepository repository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.appUserDetailsServiceImpl = appUserDetailsServiceImpl;
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(appUserDetailsServiceImpl.loadUserByUsername(loginForm.getUsername()));
                return ResponseEntity.ok(token);
            } else {
                throw new UsernameNotFoundException("Invalid login credentials");
            }
        } catch (Exception ex) {
            throw new UsernameNotFoundException("Invalid login credentials");
        }
    }


    @Override
    public ResponseEntity<AppUser> createUser(@RequestBody AppUser user) {
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        user.setRole(user.getRole());
        repository.save(user);
        return ResponseEntity.ok(user);
    }
}
