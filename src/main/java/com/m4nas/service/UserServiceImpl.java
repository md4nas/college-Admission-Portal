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
import java.util.List;
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
            // Update existing user with provider info but preserve role
            existingUser.setProvider(provider);
            existingUser.setVerificationCode("OAUTH_VERIFIED");
            existingUser.setEnable(true);
            // Don't change the role - preserve existing role
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

            Resource resource = resourceLoader.getResource("classpath:templates/forget_otp_mail.html");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            String content = sb.toString();
            UserDtls user = userRepo.findByEmail(email);
            String userName = (user != null && user.getFullName() != null) ? user.getFullName() : "User";
            content = content.replace("[[name]]", userName);
            content = content.replace("[[otp]]", String.valueOf(otp));


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
    
    @Override
    public List<UserDtls> getAllUsers() {
        return userRepo.findAll();
    }
    
    @Override
    public List<UserDtls> getTeachers() {
        return userRepo.findByRole("ROLE_TEACHER");
    }
    
    @Override
    public List<UserDtls> getUsersByRole(String role) {
        return userRepo.findByRole(role);
    }
}