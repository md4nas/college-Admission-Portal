package com.m4nas.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
public class UserDtls implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private int id;

    private String fullName;

    private String email;

    private String mobileNumber;

    private String password;

    private String address;

    private String role;

    private boolean enable;

    private String verificationCode;
}
