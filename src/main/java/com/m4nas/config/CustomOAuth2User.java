package com.m4nas.config;

import com.m4nas.model.UserDtls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final UserDtls user;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String email;

    public CustomOAuth2User(OAuth2User oauth2User, UserDtls user) {
        this.oauth2User = oauth2User;
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
        this.email = user.getEmail();
    }
    
    public CustomOAuth2User(OAuth2User oauth2User, String email) {
        this.oauth2User = oauth2User;
        this.user = null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        this.email = email;
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
        return email != null ? email : oauth2User.getAttribute("name");
    }

    public UserDtls getUser() {
        return user;
    }
}