# 🚀 College Admission Portal - Complete Setup Guide

This comprehensive guide will help you set up the College Admission Portal from scratch. Follow these steps carefully to ensure everything works properly.

## 📋 Prerequisites

Before starting, ensure you have:

- ☕ **Java 17+** installed
- 🐘 **PostgreSQL 13+** installed and running
- 📦 **Maven 3.8+** installed
- 🔧 **IDE** (IntelliJ IDEA recommended)

## 🛠️ Step-by-Step Setup

### 1️⃣ Clone and Setup Project

```bash
git clone https://github.com/your-username/college-admission-portal.git
cd college-admission-portal
```

### 2️⃣ Database Setup

#### Create PostgreSQL Database:
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database and user
CREATE DATABASE college_portal_db;
CREATE USER college_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE college_portal_db TO college_user;

-- Exit PostgreSQL
\q
```

#### Test Database Connection:
```bash
psql -h localhost -U college_user -d college_portal_db
```

### 3️⃣ Environment Configuration

#### Copy and Configure Environment File:
```bash
cp .env.template .env
```

#### Edit `.env` file with your credentials:
```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/college_portal_db
DATABASE_USERNAME=college_user
DATABASE_PASSWORD=your_secure_password

# Email Configuration (REQUIRED)
MAIL_USERNAME=your_brevo_email@domain.com
BREVO_SMTP_PASSWORD=your_brevo_smtp_key

# OAuth2 Configuration (Optional)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
GOOGLE_AUTHORIZATION_URI=https://accounts.google.com/o/oauth2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_USER_INFO_URI=https://www.googleapis.com/oauth2/v2/userinfo

GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
GITHUB_REDIRECT_URI=http://localhost:8080/login/oauth2/code/github

# Admin Configuration
ADMIN_EMAIL=admin@collegeportal.com
ADMIN_PASSWORD=YourSecureAdminPassword
ADMIN_NAME=System Administrator

# Server Configuration
SERVER_PORT=8080
```

### 4️⃣ Email Setup (CRITICAL)

#### Brevo SMTP Setup (Recommended):
1. Go to [Brevo.com](https://www.brevo.com/)
2. Create free account (300 emails/day)
3. Go to **SMTP & API** → **SMTP**
4. Generate SMTP key
5. Update `.env` with your credentials

#### Test Email Configuration:
- Start the application
- Register a new user
- Check email for verification link

### 5️⃣ OAuth2 Setup (Optional)

#### Google OAuth2:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create project → Enable Google+ API
3. Create OAuth2 credentials
4. Add redirect URI: `http://localhost:8080/login/oauth2/code/google`
5. Update `.env` with client ID and secret

#### GitHub OAuth2:
1. Go to GitHub Settings → Developer settings → OAuth Apps
2. Create new OAuth App
3. Set callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Update `.env` with client ID and secret

### 6️⃣ Build and Run

#### Build the Application:
```bash
mvn clean install
```

#### Run the Application:
```bash
mvn spring-boot:run
```

#### Or run JAR file:
```bash
java -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar
```

#### For Production:
```bash
# Build for production
mvn clean package -Pprod

# Run with production profile
java -jar -Dspring.profiles.active=prod target/UserManagemetPortal-0.0.1-SNAPSHOT.jar
```

### 7️⃣ Access Application

- 🌐 **URL**: http://localhost:8080
- 👤 **Admin Login**: Use credentials from `.env` file
- 📧 **Test Registration**: Create a new user account

## ✅ Verification Checklist

### Database Connection:
- [ ] PostgreSQL is running
- [ ] Database `college_portal_db` exists
- [ ] User can connect to database
- [ ] Application starts without database errors
- [ ] Tables are created automatically (user_dtls, user_applications, payments, announcements)

### Email System:
- [ ] SMTP credentials configured in `.env`
- [ ] Registration sends verification email
- [ ] Password recovery sends OTP email
- [ ] Emails not going to spam folder

### Authentication:
- [ ] Local login works with email/password
- [ ] Google OAuth2 login works (if configured)
- [ ] GitHub OAuth2 login works (if configured)
- [ ] Admin account created automatically

### Role-Based Access:
- [ ] Admin can access `/admin/**` endpoints (user management, system reports)
- [ ] Teacher can access `/teacher/**` endpoints (student management, announcements)
- [ ] User can access `/user/**` endpoints (application, payment, status)
- [ ] Unauthorized access is blocked with proper redirects

### UI/UX:
- [ ] Landing page loads correctly with hero section
- [ ] All 39 screenshots match your interface (9 index + 9 admin + 12 teacher + 18 user)
- [ ] Responsive design works on mobile and tablet
- [ ] Navigation works properly across all roles
- [ ] Forms have proper validation and feedback
- [ ] Payment calculator works correctly
- [ ] File upload functionality works for receipts

## 🚨 Common Issues & Solutions

### Issue: Application won't start
**Solution**: Check database connection and `.env` configuration

### Issue: Emails not sending
**Solution**: Verify SMTP credentials and check spam folder

### Issue: OAuth2 not working
**Solution**: Check redirect URIs match exactly in OAuth provider settings

### Issue: Database connection failed
**Solution**: Ensure PostgreSQL is running and credentials are correct

## 🔒 Security Recommendations

### For Development:
- Use strong passwords in `.env`
- Don't commit `.env` to version control
- Change default admin password after first login

### For Production:
- Use environment variables instead of `.env` file
- Enable HTTPS
- Use secure database passwords
- Regular security updates

## 📞 Support

If you encounter issues:
1. Check this setup guide
2. Review application logs
3. Verify all prerequisites are met
4. Check database and email configurations

## 🎯 Next Steps

After successful setup:
1. **Change default admin password** - Login and update admin credentials
2. **Configure OAuth2 providers** - Set up Google and GitHub OAuth2 if needed
3. **Customize email templates** - Update email templates in `src/main/resources/templates/`
4. **Test all features** - Create test applications, payments, and announcements
5. **Set up production deployment** - Configure production environment variables
6. **Configure monitoring** - Set up application monitoring and logging
7. **Backup strategy** - Implement database backup procedures
8. **SSL certificate** - Configure HTTPS for production

## 🔧 Advanced Configuration

### Custom Email Templates
Email templates are located in `src/main/resources/templates/`:
- `verification-mail-template.html` - Account verification email
- `forget_otp_mail.html` - Password reset OTP email

### Database Migrations
The application uses Hibernate DDL auto-update. For production, consider:
```properties
spring.jpa.hibernate.ddl-auto=validate
```

### File Upload Configuration
```properties
# Maximum file size for receipt uploads
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Production Security
```properties
# Enable HTTPS in production
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_keystore_password
server.ssl.key-store-type=PKCS12
```

---

**🎉 Congratulations! Your College Admission Portal is now ready to use!**

**🌐 Live Demo**: https://college-admission-portal-ax6b.onrender.com  
**📧 Support**: support@collegeportal.com  
**📚 Documentation**: See README.md for complete feature overview
