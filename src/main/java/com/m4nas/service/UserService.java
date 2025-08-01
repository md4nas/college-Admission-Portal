package com.m4nas.service;

import com.m4nas.model.UserDtls;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Service interface for user management operations including registration,
 * verification, and OAuth2 integration.
 */
public interface UserService {

    /**
     * Creates a new local user account and initiates email verification.
     *
     * @param user the user details to create (must not be null)
     * @param url the base URL for constructing verification links (must not be empty)
     * @return the persisted user entity
     * @throws IllegalArgumentException if user details are invalid
     */
    UserDtls createUser(UserDtls user, String url);

    /**
     * Checks for existing user by email address.
     *
     * @param email the email address to check (must not be empty)
     * @return true if email exists in system, false otherwise
     */
    boolean checkEmail(String email);

    /**
     * Verifies a user account using the verification token.
     *
     * @param code the verification token (must not be empty)
     * @return true if verification succeeded, false if token is invalid
     */
    boolean verifyAccount(String code);

    /**
     * Sends account verification email to the specified user.
     *
     * @param user the user to verify (must not be null and must have valid email)
     * @param url the base URL for verification link construction
     * @throws MessagingException if email transport fails
     * @throws UnsupportedEncodingException if email encoding fails
     * @throws IllegalArgumentException if user or URL is invalid
     */
    void sendVerificationMail(UserDtls user, String url)
            throws MessagingException, UnsupportedEncodingException;

    /**
     * Creates or updates a user account from OAuth2 provider data.
     *
     * @param email the user's verified email from provider (must not be empty)
     * @param name the user's full name from provider (must not be empty)
     * @param provider the authentication provider (e.g., "google", "facebook")
     * @return the created/updated user entity
     * @throws IllegalArgumentException if any parameter is invalid
     */
    UserDtls createOAuthUser(String email, String name, String provider);

    /**
     * Retrieves user by email address.
     *
     * @param email the email address to search for
     * @return the user entity if found, null otherwise
     */
    UserDtls getUserByEmail(String email);

    /**
     * Sends OTP for forgot password functionality.
     *
     * @param email the user's email address
     * @param otp the generated OTP
     * @return true if email sent successfully, false otherwise
     */
    boolean sendForgotPasswordOTP(String email, int otp);

    /**
     * Updates user password without current password verification.
     *
     * @param email the user's email
     * @param newPassword the new password to set
     * @return true if update succeeded, false otherwise
     */
    boolean updatePassword(String email, String newPassword);
    
    /**
     * Retrieves all users in the system.
     *
     * @return list of all users
     */
    List<UserDtls> getAllUsers();
    
    /**
     * Retrieves all users with TEACHER role.
     *
     * @return list of teachers
     */
    List<UserDtls> getTeachers();
    List<UserDtls> getUsersByRole(String role);
}