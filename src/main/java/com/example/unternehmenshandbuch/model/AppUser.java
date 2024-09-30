package com.example.unternehmenshandbuch.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "app_user")
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column()
    private String role;
}
