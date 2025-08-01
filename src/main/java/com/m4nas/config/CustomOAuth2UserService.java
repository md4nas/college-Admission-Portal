package com.m4nas.config;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        // Handle GitHub specific attributes
        if ("github".equals(provider)) {
            if (name == null) {
                name = oAuth2User.getAttribute("login");
            }
            if (email == null) {
                email = getGitHubEmail(userRequest.getAccessToken().getTokenValue());
            }
        }
        
        // Ensure we have required values
        if (name == null) name = "Unknown User";
        
        // Skip user creation if no email found
        if (email == null) {
            throw new OAuth2AuthenticationException("Unable to get email from " + provider);
        }

        // Use the injected userService to handle user creation/update
        UserDtls user = userService.createOAuthUser(email, name, provider);
        return new CustomOAuth2User(oAuth2User, user);
    }
    
    private String getGitHubEmail(String accessToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                List.class
            );
            
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                // Find primary email or first verified email
                for (Map<String, Object> emailObj : emails) {
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (primary != null && primary && verified != null && verified) {
                        return (String) emailObj.get("email");
                    }
                }
                // If no primary, get first verified email
                for (Map<String, Object> emailObj : emails) {
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (verified != null && verified) {
                        return (String) emailObj.get("email");
                    }
                }
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return null;
    }
    
    public OidcUser loadOidcUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUserService delegate = new OidcUserService();
        OidcUser oidcUser = delegate.loadUser(userRequest);
        
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String provider = "google";
        
        if (email != null) {
            UserDtls user = userService.createOAuthUser(email, name, provider);
            // Return a custom OIDC user that implements both OidcUser and has proper authorities
            return new CustomOidcUser(oidcUser, user);
        }
        
        return oidcUser;
    }
}