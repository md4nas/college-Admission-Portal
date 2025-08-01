# API Documentation - User Management System

## 📋 Base Information
- **Base URL**: `http://localhost:8080`
- **Content-Type**: `application/x-www-form-urlencoded` (for forms)
- **Authentication**: Session-based with CSRF tokens

---

## 🔐 Authentication Endpoints

### 1. User Registration
**POST** `/createUser`

**Description**: Register a new user account with email verification

**Parameters**:
```
fullName: string (required) - User's full name
email: string (required) - Valid email address
password: string (required) - Minimum 6 characters
confirmPassword: string (required) - Must match password
g-recaptcha-response: string (required) - reCAPTCHA token
```

**Response**: Redirect to `/register` with success/error message

**Example**:
```bash
curl -X POST http://localhost:8080/createUser \
  -d "fullName=John Doe" \
  -d "email=john@example.com" \
  -d "password=password123" \
  -d "confirmPassword=password123" \
  -d "g-recaptcha-response=03AGdBq25..."
```

---

### 2. User Login
**POST** `/login`

**Description**: Authenticate user with email and password

**Parameters**:
```
email: string (required) - User's email
password: string (required) - User's password
g-recaptcha-response: string (required) - reCAPTCHA token
```

**Response**: Redirect based on user role:
- Admin: `/admin/`
- Teacher: `/teacher/`
- User: `/user/`

**Example**:
```bash
curl -X POST http://localhost:8080/login \
  -d "email=john@example.com" \
  -d "password=password123" \
  -d "g-recaptcha-response=03AGdBq25..."
```

---

### 3. User Logout
**POST** `/logout`

**Description**: Logout current user and invalidate session

**Response**: Redirect to `/signin?logout`

**Example**:
```bash
curl -X POST http://localhost:8080/logout
```

---

## 📧 Email Verification Endpoints

### 4. Verify Account
**GET** `/verify?code={verificationCode}`

**Description**: Verify user account via email link

**Parameters**:
```
code: string (required) - Verification code from email
```

**Response**: 
- Success: Redirect to verification success page
- Failure: Redirect to verification failed page

**Example**:
```bash
curl -X GET "http://localhost:8080/verify?code=abc123def456..."
```

---

## 🔑 Password Reset Endpoints

### 5. Forgot Password Form
**GET** `/forgot-password`

**Description**: Display forgot password email input form

**Response**: Returns forgot password HTML page

**Example**:
```bash
curl -X GET http://localhost:8080/forgot-password
```

---

### 6. Send OTP
**POST** `/send-otp`

**Description**: Send OTP to user's email for password reset

**Parameters**:
```
email: string (required) - User's registered email
```

**Response**: Redirect to OTP verification page

**Example**:
```bash
curl -X POST http://localhost:8080/send-otp \
  -d "email=john@example.com"
```

---

### 7. Verify OTP
**POST** `/verify-otp`

**Description**: Verify OTP entered by user (10-minute expiration)

**Parameters**:
```
otp: integer (required) - 6-digit OTP from email
```

**Response**: Redirect to password reset form

**Example**:
```bash
curl -X POST http://localhost:8080/verify-otp \
  -d "otp=123456"
```

---

### 8. Reset Password
**POST** `/reset-password`

**Description**: Update user password after OTP verification

**Parameters**:
```
password: string (required) - New password (min 6 chars)
confirmPassword: string (required) - Password confirmation
```

**Response**: Redirect to login page with success message

**Example**:
```bash
curl -X POST http://localhost:8080/reset-password \
  -d "password=newpassword123" \
  -d "confirmPassword=newpassword123"
```

---

## 🌐 OAuth2 Endpoints

### 9. Google OAuth2 Login
**GET** `/oauth2/authorization/google`

**Description**: Initiate Google OAuth2 login flow

**Response**: Redirect to Google authentication

**Example**:
```bash
curl -X GET http://localhost:8080/oauth2/authorization/google
```

---

### 10. GitHub OAuth2 Login
**GET** `/oauth2/authorization/github`

**Description**: Initiate GitHub OAuth2 login flow

**Response**: Redirect to GitHub authentication

**Example**:
```bash
curl -X GET http://localhost:8080/oauth2/authorization/github
```

---

## 📄 Page Endpoints

### 11. Home Page
**GET** `/`

**Description**: Public landing page

**Example**:
```bash
curl -X GET http://localhost:8080/
```

---

### 12. Registration Page
**GET** `/register`

**Description**: User registration form

**Example**:
```bash
curl -X GET http://localhost:8080/register
```

---

### 13. Login Page
**GET** `/signin`

**Description**: User login form

**Example**:
```bash
curl -X GET http://localhost:8080/signin
```

---

### 14. User Dashboard
**GET** `/user/`

**Description**: User dashboard (requires authentication)

**Authentication**: Required (USER, ADMIN, or TEACHER role)

**Example**:
```bash
curl -X GET http://localhost:8080/user/ \
  --cookie "JSESSIONID=your-session-id"
```

---

### 15. Admin Dashboard
**GET** `/admin/`

**Description**: Admin dashboard (requires admin role)

**Authentication**: Required (ADMIN role only)

**Example**:
```bash
curl -X GET http://localhost:8080/admin/ \
  --cookie "JSESSIONID=your-session-id"
```

---

### 16. Teacher Dashboard
**GET** `/teacher/`

**Description**: Teacher dashboard (requires teacher role)

**Authentication**: Required (TEACHER role only)

**Example**:
```bash
curl -X GET http://localhost:8080/teacher/ \
  --cookie "JSESSIONID=your-session-id"
```

---

## 🔒 Security Notes

### CSRF Protection
All POST requests require CSRF tokens. Include in forms:
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
```

### reCAPTCHA
Login and registration require valid reCAPTCHA response:
- Get token from Google reCAPTCHA widget
- Include as `g-recaptcha-response` parameter

### Session Management
- Sessions expire after inactivity
- Maximum 1 session per user
- Secure session cookies in production

---

## 📊 Response Codes

| Code | Description |
|------|-------------|
| 200 | Success |
| 302 | Redirect (normal flow) |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (login required) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 500 | Internal Server Error |

---

## 🧪 Testing with Postman

### Collection Setup
1. Create new Postman collection
2. Set base URL variable: `{{baseUrl}} = http://localhost:8080`
3. Enable cookie jar for session management
4. Add CSRF token extraction scripts

### Pre-request Script (for CSRF)
```javascript
pm.sendRequest({
    url: pm.variables.get("baseUrl") + "/signin",
    method: 'GET'
}, function (err, response) {
    if (response) {
        const $ = cheerio.load(response.text());
        const csrfToken = $('input[name="_csrf"]').val();
        pm.environment.set("csrf_token", csrfToken);
    }
});
```

### Environment Variables
```
baseUrl: http://localhost:8080
csrf_token: (auto-extracted)
session_id: (auto-extracted)
```

---

## 🚀 Quick Start Guide

1. **Start Application**: Run Spring Boot app on port 8080
2. **Register User**: POST to `/createUser` with required fields
3. **Verify Email**: Click link in verification email
4. **Login**: POST to `/login` with credentials
5. **Access Dashboard**: GET user-specific dashboard
6. **Test Password Reset**: Use forgot password flow
7. **Test OAuth2**: Try Google/GitHub login

---

## 📞 Support

For API issues or questions:
- Check application logs
- Verify environment variables
- Ensure database connectivity
- Validate reCAPTCHA configuration