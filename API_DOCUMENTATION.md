# 📚 UserAuth API Documentation

## 🔗 Base URL
```
http://localhost:8080
```

## 🔐 Authentication Endpoints

### Public Endpoints (No Authentication Required)

#### 🏠 Landing Page
```http
GET /
```
**Description:** Main landing page with features and registration options  
**Response:** HTML page with hero section, features, and call-to-action

#### 📝 User Registration
```http
GET /register
POST /register
```
**Description:** User registration form and processing  
**POST Parameters:**
- `fullName` (String): User's full name
- `email` (String): Valid email address
- `password` (String): Password (min 6 characters)
- `confirmPassword` (String): Password confirmation

**Response:** Redirect to registration page with success/error message

#### 🔐 User Login
```http
GET /signin
POST /signin
```
**Description:** User login form and authentication  
**POST Parameters:**
- `email` (String): User's email
- `password` (String): User's password

**Response:** Redirect based on user role (admin/teacher/user)

#### ✅ Email Verification
```http
GET /verify?code={verificationCode}
```
**Description:** Verify user email with token  
**Parameters:**
- `code` (String): Email verification token

**Response:** Success or failure verification page

#### 🔑 Password Recovery
```http
GET /forgot-password
POST /forgot-password
```
**Description:** Forgot password form and OTP generation  
**POST Parameters:**
- `email` (String): User's registered email

**Response:** OTP sent to email with verification form

#### 🔄 Password Reset
```http
GET /reset-password
POST /reset-password
```
**Description:** Reset password with OTP verification  
**POST Parameters:**
- `email` (String): User's email
- `otp` (String): 6-digit OTP from email
- `newPassword` (String): New password

## 🔒 Protected Endpoints (Authentication Required)

### 👑 Admin Endpoints (ROLE_ADMIN)

#### 📊 Admin Dashboard
```http
GET /admin/
```
**Description:** Admin dashboard with user management overview  
**Response:** Admin home page with user statistics and management tools

#### 👥 User Management
```http
GET /admin/users
POST /admin/users/{action}
```
**Description:** Complete user management functionality  
**Features:**
- View all users
- Edit user roles
- Enable/disable accounts
- Delete users

### 🎓 Teacher Endpoints (ROLE_TEACHER)

#### 📚 Teacher Dashboard
```http
GET /teacher/
```
**Description:** Teacher dashboard with student management  
**Response:** Teacher home page showing only ROLE_USER data

#### 👨‍🎓 Student Management
```http
GET /teacher/students
GET /teacher/courses
GET /teacher/grades
```
**Description:** Teacher-specific student and course management

### 👤 User Endpoints (ROLE_USER, ROLE_TEACHER, ROLE_ADMIN)

#### 🏡 User Dashboard
```http
GET /user/
```
**Description:** User dashboard with personal information  
**Response:** User home page with profile and settings

#### ⚙️ Settings
```http
GET /user/settings/changePass
POST /user/settings/updatePassword
```
**Description:** Change password functionality  
**POST Parameters:**
- `oldPass` (String): Current password
- `newPass` (String): New password

## 🔗 OAuth2 Endpoints

### Google OAuth2
```http
GET /oauth2/authorization/google
GET /login/oauth2/code/google
```

### GitHub OAuth2
```http
GET /oauth2/authorization/github
GET /login/oauth2/code/github
```

## 🚪 Logout
```http
POST /logout
```
**Description:** Logout user and invalidate session  
**Response:** Redirect to signin page with logout confirmation

## 📊 Response Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 302 | Redirect |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 500 | Internal Server Error |

## 🔒 Security Features

- **CSRF Protection:** All forms include CSRF tokens
- **Session Management:** Secure session handling with timeout
- **Password Encryption:** BCrypt with salt rounds
- **Email Verification:** Required for account activation
- **Role-Based Access:** Granular permission system
- **OAuth2 Integration:** Secure third-party authentication

## 📧 Email Integration

The system uses **Brevo SMTP** for email delivery:
- Account verification emails
- Password reset OTP emails
- Professional HTML templates
- Secure token-based validation

## 🛡️ Error Handling

All endpoints include proper error handling with:
- User-friendly error messages
- Secure error responses (no sensitive data exposure)
- Proper HTTP status codes
- Redirect-based error flow