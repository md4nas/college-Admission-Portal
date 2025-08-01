package com.m4nas.service;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import com.m4nas.util.RandomString;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final ResourceLoader resourceLoader;

    @Autowired
    public UserServiceImpl(UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           JavaMailSender mailSender,
                           ResourceLoader resourceLoader) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }

    @Override
    @Transactional
    public UserDtls createUser(UserDtls user, String url) {
        user.setId(RandomString.generateUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnable(false);
        user.setProvider("local");

        RandomString rn = new RandomString();
        user.setVerificationCode(rn.make(64));

        UserDtls savedUser = userRepo.save(user);

        try {
            sendVerificationMail(savedUser, url);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email", e);
        }

        return savedUser;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    @Transactional
    public void sendVerificationMail(UserDtls user, String url) throws MessagingException, UnsupportedEncodingException {
        String fromAddress = "kabiranas7890@gmail.com";
        String toAddress = user.getEmail().trim().toLowerCase();
        String senderName = "User Management Team";
        String subject = "Account Verification";

        try {
            Resource resource = resourceLoader.getResource("classpath:templates/verification-mail-template.html");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            String content = sb.toString();
            content = content.replace("[[name]]", user.getFullName());
            String siteUrl = url + "/verify?code=" + user.getVerificationCode();
            content = content.replace("[[URL]]", siteUrl);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new MessagingException("Failed to send verification email", e);
        }
    }

    @Override
    @Transactional
    public UserDtls createOAuthUser(String email, String name, String provider) {
        if (email == null || name == null || provider == null) {
            throw new IllegalArgumentException("Email, name and provider cannot be null");
        }

        // Check if user already exists
        UserDtls existingUser = userRepo.findByEmail(email);
        if (existingUser != null) {
            // Update existing user with provider info
            existingUser.setProvider(provider);
            existingUser.setVerificationCode("OAUTH_VERIFIED");
            existingUser.setEnable(true);
            return userRepo.save(existingUser);
        }

        // Create new user
        UserDtls user = new UserDtls();
        user.setId(RandomString.generateUserId());
        user.setEmail(email);
        user.setFullName(name);
        user.setRole("ROLE_USER");
        user.setEnable(true);
        user.setProvider(provider);
        user.setVerificationCode(provider.toUpperCase() + "_OAUTH_VERIFIED");

        // Generate more secure random password
        String randomPassword = UUID.randomUUID().toString() + new RandomString().make(8);
        user.setPassword(passwordEncoder.encode(randomPassword));

        return userRepo.save(user);
    }

    @Override
    @Transactional
    public boolean verifyAccount(String code) {
        if (code != null && code.endsWith("_OAUTH_VERIFIED")) {
            return true;
        }

        UserDtls user = userRepo.findByVerificationCode(code);
        if (user != null) {
            user.setEnable(true);
            user.setVerificationCode("LOCAL_OAUTH_VERIFIED");
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public boolean sendForgotPasswordOTP(String email, int otp) {
        try {
            String fromAddress = "kabiranas7890@gmail.com";
            String senderName = "User Management Team";
            String subject = "Password Reset OTP";
            
            String content = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Password Reset OTP</title>\n" +
                    "    <style>\n" +
                    "        body { background-color: #f4f6f9; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 20px; color: #333333; line-height: 1.6; }\n" +
                    "        .email-container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); overflow: hidden; }\n" +
                    "        .email-header { background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); padding: 40px 30px; text-align: center; color: white; }\n" +
                    "        .logo { width: 60px; height: 60px; background: rgba(255, 255, 255, 0.2); border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center; font-size: 28px; font-weight: bold; line-height: 1; text-align: center; }\n" +
                    "        .email-header h1 { margin: 0; font-size: 28px; font-weight: 600; letter-spacing: -0.5px; }\n" +
                    "        .email-content { padding: 40px 30px; }\n" +
                    "        .welcome-text { font-size: 18px; color: #2c3e50; margin-bottom: 10px; font-weight: 600; }\n" +
                    "        .email-content p { font-size: 16px; color: #555555; margin-bottom: 20px; }\n" +
                    "        .otp-container { text-align: center; margin: 35px 0; }\n" +
                    "        .otp-code { display: inline-block; padding: 20px 40px; background: linear-gradient(135deg, #dc3545 0%, #c82333 100%); color: #ffffff; font-weight: 700; border-radius: 12px; font-size: 32px; letter-spacing: 8px; box-shadow: 0 4px 15px rgba(220, 53, 69, 0.4); }\n" +
                    "        .info-box { background: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0; }\n" +
                    "        .info-box p { margin: 0; font-size: 14px; color: #856404; }\n" +
                    "        .warning-box { background: #f8d7da; border-left: 4px solid #dc3545; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0; }\n" +
                    "        .warning-box p { margin: 0; font-size: 14px; color: #721c24; }\n" +
                    "        .email-footer { background: #2c3e50; color: #ecf0f1; text-align: center; padding: 30px; font-size: 13px; }\n" +
                    "        .email-footer p { margin: 5px 0; color: #bdc3c7; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<div class=\"email-container\">\n" +
                    "    <div class=\"email-header\">\n" +
                    "        <div class=\"logo\">🔐</div>\n" +
                    "        <h1>Password Reset Request</h1>\n" +
                    "    </div>\n" +
                    "    <div class=\"email-content\">\n" +
                    "        <p class=\"welcome-text\">Security Alert! 🔒</p>\n" +
                    "        <p>We received a request to reset your password. Use the OTP below to proceed:</p>\n" +
                    "        <div class=\"otp-container\">\n" +
                    "            <div class=\"otp-code\">" + otp + "</div>\n" +
                    "        </div>\n" +
                    "        <div class=\"info-box\">\n" +
                    "            <p><strong>⏰ Important:</strong> This OTP is valid for 10 minutes only.</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"warning-box\">\n" +
                    "            <p><strong>🚨 Security Notice:</strong> Never share this OTP with anyone. If you didn't request this, please ignore this email and consider changing your password.</p>\n" +
                    "        </div>\n" +
                    "        <p style=\"font-style: italic; color: #6c757d; text-align: center;\">— The User Management Team</p>\n" +
                    "    </div>\n" +
                    "    <div class=\"email-footer\">\n" +
                    "        <p><strong>User Management Portal</strong></p>\n" +
                    "        <p>&copy; 2025 All rights reserved.</p>\n" +
                    "        <p>This is an automated message, please do not reply to this email.</p>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updatePassword(String email, String newPassword) {
        try {
            UserDtls user = userRepo.findByEmail(email);
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}