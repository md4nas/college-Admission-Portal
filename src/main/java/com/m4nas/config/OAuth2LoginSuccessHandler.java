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
        



        
        if (fullName == null) {
            fullName = oauth2User.getAttribute("login");
            if (fullName == null) fullName = "Unknown User";
        }
        
        // Find user that should have been created by CustomOAuth2UserService
        UserDtls user = userRepository.findByEmail(email);

        

        
        if (user == null) {

            response.sendRedirect("/signin?error");
            return;
        }
        

        
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