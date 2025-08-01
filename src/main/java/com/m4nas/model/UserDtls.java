package com.m4nas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class UserDtls implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(updatable = false, nullable = false, length = 16)
    private String id;

    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = true)  // Made nullable for OAuth users
    private String password;

    private String role;

    private boolean enable;

    @Column(nullable = true)
    private String provider;  // For storing "google", "facebook" etc.

    @Column(nullable = true)  // Made nullable for OAuth users
    private String verificationCode;
}
