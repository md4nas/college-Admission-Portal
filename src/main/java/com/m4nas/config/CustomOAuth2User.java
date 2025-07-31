package com.m4nas.config;

import com.m4nas.model.UserDtls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final UserDtls user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(OAuth2User oauth2User, UserDtls user) {
        this.oauth2User = oauth2User;
        this.user = user;
        this.authorities = Collections.singletonList(() -> user.getRole()); // Convert role to authority
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    public UserDtls getUser() {
        return user;
    }
}