package com.m4nas.config;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        UserDtls user = userRepository.findByEmail(email);

        if (user == null) {
            // Create new user with default ROLE_USER
            user = new UserDtls();
            user.setEmail(email);
            user.setFullName(name);
            user.setProvider(provider);
            user.setEnable(true); // Note: Your field is 'enable' not 'enabled'
            user.setRole("ROLE_USER"); // Set default role

            // Set default values for required fields
            user.setPassword(""); // You might want to generate a random password
            user.setMobileNumber(""); // Or set to null if allowed
            user.setAddress(""); // Or set to null if allowed
            user.setVerificationCode(""); // Mark as verified

            user = userRepository.save(user);
        }

        return new CustomOAuth2User(oAuth2User, user);
    }
}