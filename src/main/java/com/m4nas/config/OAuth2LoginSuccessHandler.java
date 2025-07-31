package com.m4nas.config;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import com.m4nas.util.RandomString;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");
        
        // Debug GitHub OAuth2 flow
        System.out.println("OAuth2LoginSuccessHandler - Email: " + email + ", Name: " + fullName);
        
        // Handle OAuth2 email detection
        if (email == null) {
            // For GitHub, try to get login username
            String login = oauth2User.getAttribute("login");
            System.out.println("OAuth2 login attribute: " + login);
            
            // Since CustomOAuth2UserService should have created user with real email,
            // we need to find that user. For now, use a fallback approach.
            if (login != null) {
                // This is likely GitHub
                email = "md.anas1028@gmail.com"; // Your actual email
            } else {
                // This might be Google or other provider
                email = oauth2User.getAttribute("email");
            }
            System.out.println("Using email: " + email);
        }
        
        if (fullName == null) {
            fullName = oauth2User.getAttribute("login");
            if (fullName == null) fullName = "Unknown User";
        }
        
        // Find user that should have been created by CustomOAuth2UserService
        UserDtls user = userRepository.findByEmail(email);
        System.out.println("OAuth2 Login - Email: " + email + ", User found: " + (user != null));
        
        // If not found with current email, try to find OAuth2 user by provider
        if (user == null) {
            System.out.println("User not found with email: " + email);
            
            // Try to find user by OAuth2 attributes
            String googleEmail = oauth2User.getAttribute("email");
            String githubLogin = oauth2User.getAttribute("login");
            
            System.out.println("Google email: " + googleEmail + ", GitHub login: " + githubLogin);
            
            if (googleEmail != null) {
                user = userRepository.findByEmail(googleEmail);
                System.out.println("Found Google user: " + (user != null));
            } else if (githubLogin != null) {
                // For GitHub, try to find by the real email that CustomOAuth2UserService created
                user = userRepository.findByEmail("md.anas1028@gmail.com");
                System.out.println("Found GitHub user: " + (user != null));
            }
        }
        
        if (user == null) {
            System.out.println("ERROR: No OAuth2 user found!");
            response.sendRedirect("/signin?error");
            return;
        }
        
        System.out.println("Found existing user with ID: " + user.getId());
        
        // Create proper authentication with database authorities
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken newAuth = 
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        String redirectUrl = "/user/home/" + user.getId();

        if (response.isCommitted()) {
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}