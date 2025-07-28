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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncode;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public UserDtls createUser(UserDtls user, String url) {
        user.setPassword(passwordEncode.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnable(false);

        RandomString rn = new RandomString();
        user.setVerificationCode(rn.make(64));

        UserDtls savedUser = userRepo.save(user);

        try {
            sendVerificationMail(savedUser, url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedUser;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public void sendVerificationMail(UserDtls user, String url) throws MessagingException, UnsupportedEncodingException {
        String fromAddress = "kabiranas7890@gmail.com";
        String toAddress = user.getEmail();
        String senderName = "User Management Team";
        String subject = "Account Verification";

        try {
            // Load HTML template from resources
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

            // Prepare mail
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromAddress, senderName);
            helper.setTo(toAddress);
            helper.setSubject(subject);
            helper.setText(content, true); // true enables HTML

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new MessagingException("Failed to send verification email", e);
        }
    }

    @Override
    public boolean verifyAccount(String code) {
        UserDtls user = userRepo.findByVerificationCode(code);
        if (user != null) {
            user.setEnable(true);
            user.setVerificationCode(null);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}