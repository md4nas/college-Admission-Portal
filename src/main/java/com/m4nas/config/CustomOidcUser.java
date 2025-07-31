package com.m4nas.config;

import com.m4nas.model.UserDtls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOidcUser implements OidcUser {

    private final OidcUser oidcUser;
    private final UserDtls user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOidcUser(OidcUser oidcUser, UserDtls user) {
        this.oidcUser = oidcUser;
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public UserDtls getUser() {
        return user;
    }
}