package com.m4nas.config;

import com.m4nas.repository.UserRepository;
import com.m4nas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Security Configuration for UserAuth System
 * 
 * This configuration class sets up comprehensive security for the application including:
 * - Role-based access control (RBAC) for Admin, Teacher, and User roles
 * - Form-based authentication with custom success/failure handlers
 * - OAuth2 integration with Google and GitHub providers
 * - CSRF protection with cookie-based token repository
 * - Session management with security controls
 * - Password encoding with BCrypt
 * 
 * Security Features:
 * - /admin/** endpoints require ROLE_ADMIN authority
 * - /teacher/** endpoints require ROLE_TEACHER authority  
 * - /user/** endpoints accessible by all authenticated users
 * - Public endpoints for registration, login, and verification
 * 
 * @author UserAuth Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationSuccessHandler customSuccessHandler;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public SecurityConfig(
            @Qualifier("customSuccessHandler") AuthenticationSuccessHandler customSuccessHandler,
            OAuth2LoginSuccessHandler oauth2LoginSuccessHandler,
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder) {
        this.customSuccessHandler = customSuccessHandler;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository, userService);
    }

    /**
     * Main security filter chain configuration.
     * 
     * Configures:
     * - CSRF protection with cookie repository
     * - Role-based URL authorization
     * - Form login with custom handlers
     * - OAuth2 login integration
     * - Logout configuration
     * - Session management
     * 
     * @param http HttpSecurity configuration object
     * @return SecurityFilterChain configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/oauth2/**", "/login/oauth2/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/teacher/**").hasAuthority("ROLE_TEACHER")
                        .requestMatchers("/user/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN", "ROLE_TEACHER")
                        .requestMatchers("/verify").permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/signin")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(customSuccessHandler)
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/signin")
                        .defaultSuccessUrl("/user/", true)
                        .userInfoEndpoint(userInfo -> {
                            CustomOAuth2UserService service = customOAuth2UserService();
                            userInfo.userService(service);
                            userInfo.oidcUserService(request -> service.loadOidcUser(request));
                        })
                        .failureUrl("/signin?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/signin?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
;

        return http.build();
    }
}