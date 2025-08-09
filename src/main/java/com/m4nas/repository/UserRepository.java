package com.m4nas.repository;

import com.m4nas.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for UserDtls entity operations.
 * Provides custom query methods for user management functionality.
 * 
 * @author College Technical Team
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<UserDtls, String> {

    /**
     * Checks if a user exists with the given email address.
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return the user entity or null if not found
     */
    UserDtls findByEmail(String email);

    /**
     * Finds a user by their verification code.
     * Used for email verification process.
     * 
     * @param code the verification code
     * @return the user entity or null if not found
     */
    UserDtls findByVerificationCode(String code);
    
    /**
     * Finds all users with a specific role.
     * 
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<UserDtls> findByRole(String role);
}
