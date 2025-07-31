package com.m4nas.config;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            user.setEnable(true);
            user.setRole("ROLE_USER");

            // Generate secure random password for OAuth users
            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(passwordEncoder.encode(randomPassword));

            // Mark OAuth users as verified
            user.setVerificationCode("OAUTH_VERIFIED");

            // Leave optional fields null
            user.setMobileNumber(null);
            user.setAddress(null);

            user = userRepository.save(user);
        }

        return new CustomOAuth2User(oAuth2User, user);
    }
}