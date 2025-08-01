package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

/**
 * Controller handling forgot password functionality with OTP verification.
 * Implements secure password reset flow:
 * 1. Email verification
 * 2. OTP generation and email delivery
 * 3. OTP verification with time expiration
 * 4. Password reset with validation
 * 
 * @author User Management Team
 * @version 1.0
 */
@Controller
public class ForgotPassController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPassController.class);
    
    /** OTP expiration time in milliseconds (10 minutes) */
    private static final long OTP_EXPIRATION_TIME = 600000L;

    @Autowired
    private UserService userService;

    /**
     * Displays the forgot password email input form.
     * 
     * @return forgot password template name
     */
    @GetMapping("/forgot-password")
    public String openEmailForm(){
        logger.info("Displaying forgot password form");
        return "forget_password";
    }

    /**
     * Processes forgot password request by validating email and sending OTP.
     * Implements security measures:
     * - Email validation against database
     * - Secure OTP generation
     * - Session-based OTP storage with timestamp
     * - POST-Redirect-GET pattern to prevent refresh attacks
     * 
     * @param email User's email address
     * @param session HTTP session for OTP storage
     * @param model Model for error messages
     * @return redirect to OTP verification page or error page
     */
    @PostMapping("/send-otp")
    public String sendEmailOTP(@RequestParam("email") String email, HttpSession session, Model model){
        
        logger.info("Processing forgot password request for email: {}", email);
        
        // Validate email format and existence
        if (email == null || email.trim().isEmpty()) {
            logger.warn("Forgot password attempt with empty email");
            model.addAttribute("error", "Please enter a valid email address.");
            return "forget_password";
        }
        
        // Check if user exists in database
        UserDtls user = userService.getUserByEmail(email.trim().toLowerCase());
        if(user == null) {
            logger.warn("Forgot password attempt for non-existent email: {}", email);
            model.addAttribute("error", "Email not found!");
            return "forget_password";
        }

        // Generate cryptographically secure 6-digit OTP
        SecureRandom secureRandom = new SecureRandom();
        int otp = 100000 + secureRandom.nextInt(900000);
        
        // Store OTP data in session with security attributes
        session.setAttribute("otp", otp);
        session.setAttribute("email", email.trim().toLowerCase());
        session.setAttribute("otpTimestamp", System.currentTimeMillis());
        session.setAttribute("otpSent", true);
        
        logger.info("Generated OTP for email: {}", email);
        
        // Send OTP via email service
        boolean emailSent = userService.sendForgotPasswordOTP(email, otp);
        if(emailSent) {
            logger.info("OTP email sent successfully to: {}", email);
            return "redirect:/verify-otp-page";
        } else {
            logger.error("Failed to send OTP email to: {}", email);
            model.addAttribute("error", "Failed to send OTP. Please try again.");
            return "forget_password";
        }
    }

    /**
     * Displays OTP verification page with session validation.
     * Prevents direct access without going through email verification.
     * 
     * @param session HTTP session to check OTP status
     * @param model Model for view data
     * @return OTP verification template or redirect to email form
     */
    @GetMapping("/verify-otp-page")
    public String showVerifyOtpPage(HttpSession session, Model model){
        Boolean otpSent = (Boolean) session.getAttribute("otpSent");
        
        // Validate session state - ensure OTP was actually sent
        if(otpSent == null || !otpSent) {
            logger.warn("Unauthorized access to OTP verification page");
            return "redirect:/forgot-password";
        }
        
        logger.info("Displaying OTP verification page");
        return "verify_otp";
    }

    /**
     * Verifies the OTP entered by user with comprehensive security checks.
     * Security features:
     * - Session validation
     * - Time-based expiration (10 minutes)
     * - Secure OTP comparison
     * - Session state management
     * 
     * @param enteredOtp OTP entered by user
     * @param session HTTP session containing OTP data
     * @param model Model for error messages
     * @return password reset page or error page
     */
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") int enteredOtp, HttpSession session, Model model){
        
        logger.info("Processing OTP verification");
        
        // Retrieve OTP data from session
        Integer sessionOtp = (Integer) session.getAttribute("otp");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");
        Boolean otpSent = (Boolean) session.getAttribute("otpSent");
        
        // Validate session integrity
        if(sessionOtp == null || otpTimestamp == null || otpSent == null || !otpSent) {
            logger.warn("OTP verification attempted with invalid session");
            model.addAttribute("error", "Session expired. Please request a new OTP.");
            return "redirect:/forgot-password";
        }
        
        // Check OTP expiration (10 minutes)
        long currentTime = System.currentTimeMillis();
        if(currentTime - otpTimestamp > OTP_EXPIRATION_TIME) {
            logger.warn("Expired OTP verification attempted");
            
            // Clean up expired session data
            session.removeAttribute("otp");
            session.removeAttribute("otpTimestamp");
            session.removeAttribute("otpSent");
            
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            return "redirect:/forgot-password";
        }
        
        // Verify OTP with secure comparison
        if(!sessionOtp.equals(enteredOtp)) {
            logger.warn("Invalid OTP verification attempted");
            model.addAttribute("error", "Invalid OTP!");
            return "verify_otp";
        }
        
        // OTP verified successfully - update session state
        session.setAttribute("otpVerified", true);
        logger.info("OTP verification successful");
        
        return "reset_password";
    }

    /**
     * Processes password reset with comprehensive validation.
     * Security features:
     * - OTP verification status check
     * - Password strength validation
     * - Password confirmation matching
     * - Complete session cleanup
     * 
     * @param password New password
     * @param confirmPassword Password confirmation
     * @param session HTTP session for verification status
     * @param model Model for messages
     * @return success page or error page
     */
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("password") String password, 
                               @RequestParam("confirmPassword") String confirmPassword,
                               HttpSession session, Model model){
        
        logger.info("Processing password reset request");
        
        // Validate session state - ensure OTP was verified
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("email");
        
        if(otpVerified == null || !otpVerified || email == null) {
            logger.warn("Unauthorized password reset attempt");
            model.addAttribute("error", "Unauthorized access. Please verify OTP first.");
            return "redirect:/forgot-password";
        }
        
        // Validate password inputs
        if(password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "Password cannot be empty!");
            return "reset_password";
        }
        
        if(password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long!");
            return "reset_password";
        }
        
        if(!password.equals(confirmPassword)) {
            logger.warn("Password reset failed: Password mismatch for email: {}", email);
            model.addAttribute("error", "Passwords do not match!");
            return "reset_password";
        }
        
        // Update password in database
        boolean updated = userService.updatePassword(email, password);
        if(updated) {
            logger.info("Password reset successful for email: {}", email);
            
            // Complete session cleanup for security
            session.removeAttribute("otp");
            session.removeAttribute("email");
            session.removeAttribute("otpTimestamp");
            session.removeAttribute("otpSent");
            session.removeAttribute("otpVerified");
            
            model.addAttribute("success", "Password updated successfully!");
            return "signin";
        } else {
            logger.error("Password reset failed for email: {}", email);
            model.addAttribute("error", "Failed to update password. Please try again.");
            return "reset_password";
        }
    }
}
