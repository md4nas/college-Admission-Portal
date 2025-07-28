
# Spring Boot User Management System

A complete User Management System built with Spring Boot, Thymeleaf, Spring Security, and PostgreSQL. This system includes features like user registration, login, role-based access control, email verification using Brevo (formerly SendinBlue), and secure password encryption.

---

## 🧰 Tech Stack

- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, HTML, CSS (can be extended with Bootstrap or Tailwind)
- **Database:** PostgreSQL
- **Mail API:** Brevo (SendinBlue SMTP)
- **Build Tool:** Maven

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com.example.usermanagement/
│   │       ├── controller/        # Controllers for handling requests
│   │       ├── entity/            # JPA Entities (User, Role, etc.)
│   │       ├── repository/        # Spring Data JPA Repositories
│   │       ├── service/           # Business Logic and Mail Service
│   │       ├── config/            # Spring Security Configuration
│   │       └── UserManagementApplication.java
│   └── resources/
│       ├── templates/             # Thymeleaf HTML pages
│       ├── static/                # CSS/JS files (if used)
│       └── application.properties # DB & Mail Configs
└── test/                          # JUnit tests
```

---

## 🚀 Features

- ✅ User Registration with Email Verification (via Brevo)
- ✅ Secure Login with Role-Based Access
- ✅ Password Encryption (BCrypt)
- ✅ Admin/User Role Mapping
- ✅ Session and Error Handling

---

## 🔐 application.properties Setup

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/userdb
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Brevo SMTP Configuration
spring.mail.host=smtp-relay.brevo.com
spring.mail.port=587
spring.mail.username=your_brevo_email
spring.mail.password=your_brevo_smtp_key
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> ⚠️ Make sure to add `application.properties` to `.gitignore`

---

## 🔁 Route Flow & Redirection

| Path                   | Method | Description                              |
|------------------------|--------|------------------------------------------|
| `/register`            | GET    | Show user registration form              |
| `/register`            | POST   | Process registration & send email        |
| `/verify?code=XYZ`     | GET    | Email verification route                 |
| `/login`               | GET    | Show login form                          |
| `/dashboard`           | GET    | Redirected page after successful login   |
| `/admin`               | GET    | Admin-only area (role-based access)      |
| `/user`                | GET    | User-only area                           |

---

## 📦 How to Run on Your Machine

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/user-management-system.git
cd user-management-system
```

### 2. Configure Database
- Create a PostgreSQL database: `userdb`
- Update your credentials in `src/main/resources/application.properties`

### 3. Configure Brevo (SendinBlue)
- Create an account on [Brevo](https://www.brevo.com/)
- Generate SMTP key and paste it in `application.properties`

### 4. Build & Run the App
```bash
./mvnw spring-boot:run
```

### 5. Open in Browser
```
http://localhost:8080/register
```

---

## 👤 Default Roles & Accounts

You can define default roles (`ADMIN`, `USER`) and create initial users through a data.sql script or manually insert via database.

---

## 📌 Notes

- Email verification must be completed before login.
- Admin pages are protected by Spring Security role-based authorization.
- Designed with extensibility in mind (e.g., add forgot password, JWT, etc.)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

> Created with by Anas
