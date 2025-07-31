package com.m4nas.config;

import org.springframework.security.authentication.DisabledException;

public class CustomDisabledException extends DisabledException {
    public CustomDisabledException(String msg) {
        super(msg);
    }
}