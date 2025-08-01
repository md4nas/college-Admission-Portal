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

import java.util.Random;

@Controller
public class ForgotPassController {

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String openEmailForm(){
        return "forget_password";
    }

    @PostMapping("/send-otp")
    public String sendEmailOTP(@RequestParam("email") String email, HttpSession session, Model model){
        
        UserDtls user = userService.getUserByEmail(email);
        if(user == null) {
            model.addAttribute("error", "Email not found!");
            return "forget_password";
        }

        // Generate 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        
        // Store OTP, email and timestamp in session
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        session.setAttribute("otpTimestamp", System.currentTimeMillis());
        session.setAttribute("otpSent", true);
        
        // Send OTP via email
        boolean emailSent = userService.sendForgotPasswordOTP(email, otp);
        if(emailSent) {
            return "redirect:/verify-otp-page";
        } else {
            model.addAttribute("error", "Failed to send OTP. Please try again.");
            return "forget_password";
        }
    }

    @GetMapping("/verify-otp-page")
    public String showVerifyOtpPage(HttpSession session, Model model){
        Boolean otpSent = (Boolean) session.getAttribute("otpSent");
        if(otpSent == null || !otpSent) {
            return "redirect:/forgot-password";
        }
        return "verify_otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam("otp") int enteredOtp, HttpSession session, Model model){
        
        Integer sessionOtp = (Integer) session.getAttribute("otp");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");
        Boolean otpSent = (Boolean) session.getAttribute("otpSent");
        
        // Check if OTP session exists
        if(sessionOtp == null || otpTimestamp == null || otpSent == null || !otpSent) {
            model.addAttribute("error", "Session expired. Please request a new OTP.");
            return "redirect:/forgot-password";
        }
        
        // Check if OTP is expired (10 minutes = 600000 milliseconds)
        long currentTime = System.currentTimeMillis();
        if(currentTime - otpTimestamp > 600000) {
            session.removeAttribute("otp");
            session.removeAttribute("otpTimestamp");
            session.removeAttribute("otpSent");
            model.addAttribute("error", "OTP has expired. Please request a new one.");
            return "redirect:/forgot-password";
        }
        
        // Verify OTP
        if(sessionOtp != enteredOtp) {
            model.addAttribute("error", "Invalid OTP!");
            return "verify_otp";
        }
        
        // OTP verified successfully, mark as verified
        session.setAttribute("otpVerified", true);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("password") String password, 
                               @RequestParam("confirmPassword") String confirmPassword,
                               HttpSession session, Model model){
        
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("email");
        
        // Check if OTP was verified
        if(otpVerified == null || !otpVerified || email == null) {
            model.addAttribute("error", "Unauthorized access. Please verify OTP first.");
            return "redirect:/forgot-password";
        }
        
        if(!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "reset_password";
        }
        
        boolean updated = userService.updatePassword(email, password);
        if(updated) {
            // Clear all session attributes
            session.removeAttribute("otp");
            session.removeAttribute("email");
            session.removeAttribute("otpTimestamp");
            session.removeAttribute("otpSent");
            session.removeAttribute("otpVerified");
            model.addAttribute("success", "Password updated successfully!");
            return "signin";
        } else {
            model.addAttribute("error", "Failed to update password. Please try again.");
            return "reset_password";
        }
    }
}
