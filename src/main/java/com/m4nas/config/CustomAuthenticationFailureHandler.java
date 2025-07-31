package com.m4nas.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage;
        
        // Check if the cause is CustomDisabledException
        if (exception instanceof CustomDisabledException) {
            errorMessage = exception.getMessage();
        } else if (exception.getCause() instanceof CustomDisabledException) {
            errorMessage = exception.getCause().getMessage();
        } else if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid email or password. Please try again.";
        } else {
            errorMessage = "Login failed. Please try again.";
        }
        
        // Create a new exception with custom message
        AuthenticationException customException = new AuthenticationException(errorMessage) {};
        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", customException);
        
        response.sendRedirect("/signin?error");
    }
}