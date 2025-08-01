package com.m4nas.config;

import com.m4nas.model.UserDtls;
import com.m4nas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.name}")
    private String adminName;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (!userRepository.existsByEmail(adminEmail)) {
            UserDtls admin = new UserDtls();
            admin.setId(java.util.UUID.randomUUID().toString().substring(0, 16));
            admin.setFullName(adminName);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ROLE_ADMIN");
            admin.setEnable(true);
            admin.setProvider("local");
            
            userRepository.save(admin);
            System.out.println("✅ Default admin created successfully!");
            System.out.println("📧 Email: " + adminEmail);
            System.out.println("🔑 Password: " + adminPassword);
            System.out.println("⚠️  Please change the password after first login!");
        }
        // Silent if admin already exists - no log message
    }
}