
# User Management System - Spring Boot Application

A complete User Management System built using Spring Boot, Thymeleaf, and MySQL. This application supports user registration, login, logout, email verification, password encryption, and role-based access control.

---

# 🚀 Project Features

## 🔐 **Authentication & Authorization**
- **User Registration** with email verification (using Brevo SMTP)
- **Google OAuth2 Integration** for seamless social login
- **Login/Logout Functionality** with session management
- **Role-based Access Control** (USER / ADMIN / TEACHER)
- **Spring Security Integration** with custom configurations

## 🔑 **Password Management**
- **Password Encryption** with BCrypt hashing
- **Forgot Password** functionality with email OTP
- **Password Reset** with secure token validation
- **Change Password** with old password verification
- **User-friendly Messages** for all password operations

## 📧 **Email System**
- **Email Verification** for new user accounts
- **Verification Email Templates** with HTML formatting
- **Email API Integration** using Brevo (formerly Sendinblue)
- **Secure API Key Storage** in application properties
- **Automated Email Sending** for account verification

## 🛡️ **Security Features**
- **Custom Authentication Handlers** for success/failure scenarios
- **Custom Error Handling** with descriptive messages
- **Account Verification** required before login
- **Provider Tracking** (local registration vs OAuth2)
- **Session Security** with proper timeout handling

## 🗄️ **Database Integration**
- **PostgreSQL Database** support with JPA/Hibernate
- **User Entity Management** with proper relationships
- **Repository Pattern** for data access
- **Transaction Management** for data consistency
- **Database Schema** auto-generation and updates

## 🎨 **User Experience**
- **Responsive Web Design** with Bootstrap integration
- **User Dashboard** with personalized content
- **Admin Panel** for user management
- **Teacher Portal** for educator-specific features
- **Smooth Redirects** with loading states and feedback
- **Form Validation** with client and server-side checks

## 🔧 **Technical Features**
- **Spring Boot 3.x** framework
- **Thymeleaf Template Engine** for server-side rendering
- **RESTful API endpoints** for various operations
- **Custom Exception Handling** for better error management
- **Logging Integration** for debugging and monitoring
- **Environment Configuration** for different deployment stages

## 📱 **Multi-Platform Support**
- **Cross-browser Compatibility** (Chrome, Firefox, Safari, Edge)
- **Mobile-responsive Design** for all screen sizes
- **Progressive Web App** features for better mobile experience

---

## 📁 Project Structure

```bash
src/
├── main/
│   ├── java/
│   │   └── com/m4nas/
│   │       ├── config/
│   │       │   ├── CustomAuthenticationFailureHandler.java    # Custom login failure handler
│   │       │   ├── CustomDisabledException.java               # Custom exception for disabled users
│   │       │   ├── CustomOAuth2User.java                      # OAuth2 user wrapper
│   │       │   ├── CustomOAuth2UserService.java               # OAuth2 user service
│   │       │   ├── CustomSuccessHandler.java                  # Form login success handler
│   │       │   ├── CustomUserDetails.java                     # UserDetails implementation
│   │       │   ├── OAuth2LoginSuccessHandler.java             # OAuth2 login success handler
│   │       │   ├── PasswordConfig.java                        # Password encoder configuration
│   │       │   ├── SecurityConfig.java                        # Spring Security configuration
│   │       │   └── UserDetailsServiceImpl.java                # UserDetailsService implementation
│   │       │
│   │       ├── controller/
│   │       │   ├── AdminController.java                       # Admin dashboard controller
│   │       │   ├── GoogleAuthController.java                  # Google OAuth controller
│   │       │   ├── HomeController.java                        # Public pages controller
│   │       │   ├── TeacherController.java                     # Teacher dashboard controller
│   │       │   ├── UserController.java                        # User dashboard controller
│   │       │   └── VerificationController.java                # Email verification controller
│   │       │
│   │       ├── model/
│   │       │   └── UserDtls.java                              # User entity model
│   │       │
│   │       ├── repository/
│   │       │   └── UserRepository.java                        # User data repository
│   │       │
│   │       ├── service/
│   │       │   ├── UserService.java                           # User service interface
│   │       │   └── UserServiceImpl.java                       # User service implementation
│   │       │
│   │       ├── util/
│   │       │   └── RandomString.java                          # Random string generator
│   │       │
│   │       └── UserManagemetApplication.java                  # Main Spring Boot application
│   │
│   └── resources/
│       ├── static/
│       │   ├── css/
│       │   │   └── user-portal-style.css                      # Application styles
│       │   └── js/
│       │       └── user-portal-script.js                      # Application scripts
│       │
│       ├── templates/
│       │   ├── base.html                                      # Common layout template
│       │   ├── forget_password.html                           # Forgot password page
│       │   ├── index.html                                     # Public landing page
│       │   ├── register.html                                  # User registration form
│       │   ├── reset_password.html                            # Password reset form
│       │   ├── signin.html                                    # User login page
│       │   ├── verification-mail-template.html                # Email verification template
│       │   ├── verify_failed.html                             # Verification failure page
│       │   ├── verify_success.html                            # Verification success page
│       │   └── user/
│       │       ├── home.html                                  # User dashboard
│       │       └── settings/
│       │           └── change_password.html                   # Change password form
│       │
│       └── application.properties                             # Application configuration
│
└── test/
    └── java/
        └── com/m4nas/
            └── UserManagemetApplicationTests.java              # Application tests

---

## 🛠️ Setup Instructions

### Prerequisites

- Java 17+
- Spring Boot
- Themeleaf
- Bootstrap
- Maven
- PostgreSQL
- IntelliJ IDEA

### Clone & Run

```bash
git clone https://github.com/your-username/user-management-system.git
cd user-management-system
```

1. Open the project in IntelliJ IDEA.
2. Create a `user_db` schema in MySQL.
3. Create a `application.properties` file in `src/main/resources/`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_brevo_username
spring.mail.password=your_brevo_api_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

4. Run the application using the main method in `UserManagementApplication.java`.
5. Visit `http://localhost:8080` to get started.

---

## 🔐 Redirection Flow

| Action | URL | Description |
|-------|-----|-------------|
| Register | `/register` | User Registration Page |
| Verify Email | `/verify?code=` | Email verification link |
| Login | `/login` | User Login Page |
| Dashboard | `/home` | User Dashboard after login |
| Forgot Password | `/forgot-password` | Sends OTP |
| Reset Password | `/reset-password` | Enter new password |

---

## 📸 Screenshots

### 🔧 Project Structure
![Project Structure](screenshots/project-structure.png)

### 🏠 Home Page
![Home](screenshots/home-page.png)

### 🔐 Login Page
![Login](screenshots/login-page.png)

### ✍️ User Registration
![Register](screenshots/user-registration.png)

### ✅ Email Verification
![Verify](screenshots/email-verification.png)

### ❌ Verification Failed
![Failed](screenshots/verification-failed.png)

### 🔓 Logout
![Logout](screenshots/logout.png)

### 🔁 Change Password
![Change Password](screenshots/change-password.png)

---

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🤝 Contribution

Feel free to fork this repo and submit PRs to improve it.

---

## 🙋‍♂️ Author

Developed by **Anas**  
GitHub: [md4nas](https://github.com/md4nas)

---

