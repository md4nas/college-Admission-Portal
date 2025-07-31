package com.m4nas.controller;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class VerificationController {

    private final UserRepository userRepository;

    public VerificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/verify")
    public String verifyAccount(@RequestParam String code, Model model) {
        UserDtls user = userRepository.findByVerificationCode(code);

        // Check for OAuth-verified users
        if (user != null && user.getVerificationCode() != null && user.getVerificationCode().endsWith("_OAUTH_VERIFIED")) {
            return "redirect:/login?verified";
        }

        if (user == null || user.isEnable()) {
            model.addAttribute("error", "Invalid verification code or account already verified");
            return "verify_failed";
        } else {
            user.setEnable(true);
            user.setVerificationCode("LOCAL_OAUTH_VERIFIED"); // Set proper verification status
            userRepository.save(user);
            return "verify_success";
        }
    }
}