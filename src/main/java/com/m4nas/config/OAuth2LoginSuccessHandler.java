package com.m4nas.config;

import com.m4nas.model.UserDtls;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        UserDtls user = oauthUser.getUser();

        // Redirect based on role
        String redirectUrl = determineTargetUrl(user.getRole());

        if (response.isCommitted()) {
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String determineTargetUrl(String role) {
        if (role.equals("ROLE_ADMIN")) {
            return "/admin/dashboard";
        } else if (role.equals("ROLE_TEACHER")) {
            return "/teacher/dashboard";
        }
        return "/user/dashboard";
    }
}