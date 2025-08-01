# 🚀 UserAuth - Complete Setup Guide

This guide will help you set up the UserAuth system from scratch. Follow these steps carefully to ensure everything works properly.

## 📋 Prerequisites

Before starting, ensure you have:

- ☕ **Java 17+** installed
- 🐘 **PostgreSQL 13+** installed and running
- 📦 **Maven 3.8+** installed
- 🔧 **IDE** (IntelliJ IDEA recommended)

## 🛠️ Step-by-Step Setup

### 1️⃣ Clone and Setup Project

```bash
git clone <your-repository-url>
cd UserAuth-System
```

### 2️⃣ Database Setup

#### Create PostgreSQL Database:
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database and user
CREATE DATABASE user_management;
CREATE USER userauth_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE user_management TO userauth_user;

-- Exit PostgreSQL
\q
```

#### Test Database Connection:
```bash
psql -h localhost -U userauth_user -d user_management
```

### 3️⃣ Environment Configuration

#### Copy and Configure Environment File:
```bash
cp .env.template .env
```

#### Edit `.env` file with your credentials:
```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/user_management
DATABASE_USERNAME=userauth_user
DATABASE_PASSWORD=your_secure_password

# Email Configuration (REQUIRED)
MAIL_USERNAME=your_brevo_email@domain.com
BREVO_SMTP_PASSWORD=your_brevo_smtp_key

# OAuth2 Configuration (Optional)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# Admin Configuration
ADMIN_EMAIL=admin@yourcompany.com
ADMIN_PASSWORD=SecureAdminPass123!
ADMIN_NAME=System Administrator
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

### 7️⃣ Access Application

- 🌐 **URL**: http://localhost:8080
- 👤 **Admin Login**: Use credentials from `.env` file
- 📧 **Test Registration**: Create a new user account

## ✅ Verification Checklist

### Database Connection:
- [ ] PostgreSQL is running
- [ ] Database `user_management` exists
- [ ] User can connect to database
- [ ] Application starts without database errors

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
- [ ] Admin can access `/admin/**` endpoints
- [ ] Teacher can access `/teacher/**` endpoints
- [ ] User can access `/user/**` endpoints
- [ ] Unauthorized access is blocked

### UI/UX:
- [ ] Landing page loads correctly
- [ ] All 22 screenshots match your interface
- [ ] Responsive design works on mobile
- [ ] Navigation works properly

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
1. Change default admin password
2. Configure additional OAuth2 providers if needed
3. Customize email templates
4. Set up production deployment
5. Configure monitoring and logging

---

**🎉 Congratulations! Your UserAuth system is now ready to use!**