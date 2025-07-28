package com.m4nas.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Configuration
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Set<String> roles =  AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if(roles.contains("ROLE_ADMIN")){
            response.sendRedirect("/admin/");
        }
        // IF WANT 3 ROLE FOR TEACHER PANEL, other wise comment or remove this
        else if(roles.contains("ROLE_TEACHER")){
            response.sendRedirect("/teacher/");
        }
        else{
            response.sendRedirect("/user/");
        }
    }
}
