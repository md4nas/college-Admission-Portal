# 🚀 Deploy Spring Boot App with PostgreSQL on Render (FREE)

## 📋 **Prerequisites**
- GitHub account
- Render account (free)
- SMTP email service (Brevo recommended)
- OAuth2 credentials (optional)

## 🎯 **Step-by-Step Deployment Guide**

### 1️⃣ **Prepare Your Repository**

```bash
# Add deployment files to git
git add .
git commit -m "Add Render deployment configuration"
git push origin main
```

### 2️⃣ **Create PostgreSQL Database on Render**

1. **Login to Render Dashboard**
   - Go to [render.com](https://render.com)
   - Sign up/Login with GitHub

2. **Create PostgreSQL Database**
   - Click "New +" → "PostgreSQL"
   - **Name**: `college-portal-db`
   - **Database**: `college_portal_db`
   - **User**: `college_user`
   - **Region**: Choose closest to your users
   - **Plan**: Free (1GB storage, 90 days retention)
   - Click "Create Database"

3. **Note Database Details**
   - Copy the **External Database URL**
   - Format: `postgresql://user:password@host:port/database`

### 3️⃣ **Deploy Spring Boot Application**

1. **Create Web Service**
   - Click "New +" → "Web Service"
   - Connect your GitHub repository
   - Select your repository

2. **Configure Build Settings**
   - **Name**: `college-admission-portal`
   - **Environment**: `Java`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -Dspring.profiles.active=prod -Dserver.port=$PORT -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar`

3. **Set Environment Variables**
   ```env
   # Database Configuration
   DATABASE_URL=postgresql://college_user:password@host:5432/college_portal_db
   DATABASE_USERNAME=college_user
   DATABASE_PASSWORD=your_db_password
   
   # Server Configuration
   PORT=10000
   SPRING_PROFILES_ACTIVE=prod
   
   # Admin Configuration
   ADMIN_EMAIL=admin@collegeportal.com
   ADMIN_PASSWORD=Admin@123
   ADMIN_NAME=System Administrator
   
   # Email Configuration (REQUIRED)
   MAIL_USERNAME=your_brevo_email@domain.com
   BREVO_SMTP_PASSWORD=your_brevo_smtp_key
   
   # OAuth2 Configuration (Optional)
   GOOGLE_CLIENT_ID=your_google_client_id
   GOOGLE_CLIENT_SECRET=your_google_client_secret
   GOOGLE_REDIRECT_URI=https://your-app-name.onrender.com/login/oauth2/code/google
   GOOGLE_AUTHORIZATION_URI=https://accounts.google.com/o/oauth2/auth?prompt=consent
   GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
   GOOGLE_USER_INFO_URI=https://www.googleapis.com/oauth2/v3/userinfo
   
   GITHUB_CLIENT_ID=your_github_client_id
   GITHUB_CLIENT_SECRET=your_github_client_secret
   GITHUB_REDIRECT_URI=https://your-app-name.onrender.com/login/oauth2/code/github
   ```

4. **Deploy**
   - Click "Create Web Service"
   - Wait for deployment (5-10 minutes)

### 4️⃣ **Configure Email Service (CRITICAL)**

#### **Brevo SMTP Setup (Recommended)**
1. **Create Brevo Account**
   - Go to [brevo.com](https://www.brevo.com)
   - Sign up for free (300 emails/day)

2. **Get SMTP Credentials**
   - Dashboard → SMTP & API → SMTP
   - **Server**: `smtp-relay.brevo.com`
   - **Port**: `587`
   - **Login**: Your Brevo email
   - **Password**: Generate SMTP key

3. **Update Environment Variables**
   ```env
   MAIL_USERNAME=your_brevo_email@domain.com
   BREVO_SMTP_PASSWORD=your_generated_smtp_key
   ```

### 5️⃣ **Configure OAuth2 (Optional)**

#### **Google OAuth2**
1. **Google Cloud Console**
   - Create project → Enable Google+ API
   - Credentials → OAuth 2.0 Client IDs
   - **Authorized redirect URI**: `https://your-app.onrender.com/login/oauth2/code/google`

#### **GitHub OAuth2**
1. **GitHub Settings**
   - Developer settings → OAuth Apps → New OAuth App
   - **Authorization callback URL**: `https://your-app.onrender.com/login/oauth2/code/github`

### 6️⃣ **Verify Deployment**

1. **Check Application Status**
   - Render Dashboard → Your service
   - Status should be "Live"
   - Check logs for any errors

2. **Test Application**
   - Visit your app URL: `https://your-app-name.onrender.com`
   - Register a new account
   - Verify email functionality
   - Test login/logout

3. **Admin Access**
   - Login with admin credentials
   - Email: `admin@collegeportal.com`
   - Password: `Admin@123`

## 🔧 **Troubleshooting**

### **Common Issues**

#### **Build Failures**
```bash
# Check build logs in Render dashboard
# Common fixes:
- Ensure Java 17 is specified in pom.xml
- Check Maven wrapper permissions
- Verify all dependencies are available
```

#### **Database Connection Issues**
```bash
# Verify environment variables
- DATABASE_URL format is correct
- Database user has proper permissions
- Database is running and accessible
```

#### **Email Not Working**
```bash
# Check SMTP configuration
- Verify Brevo SMTP credentials
- Check email sending limits
- Ensure port 587 is accessible
```

#### **OAuth2 Issues**
```bash
# Verify OAuth2 setup
- Check redirect URIs match exactly
- Ensure client IDs and secrets are correct
- Verify OAuth2 apps are enabled
```

### **Performance Optimization**

#### **Free Tier Limitations**
- **Memory**: 512MB RAM
- **CPU**: Shared
- **Sleep**: Apps sleep after 15 minutes of inactivity
- **Build Time**: 10 minutes max

#### **Optimization Tips**
```bash
# Reduce build time
./mvnw clean package -DskipTests -Dmaven.javadoc.skip=true

# Optimize JVM for low memory
java -Xmx400m -Dserver.port=$PORT -jar target/app.jar

# Enable compression
server.compression.enabled=true
```

## 📊 **Monitoring & Maintenance**

### **Health Checks**
- Render automatically monitors your app
- Check `/actuator/health` endpoint
- Monitor logs in Render dashboard

### **Database Maintenance**
- Free PostgreSQL: 1GB storage, 90 days retention
- Monitor database size in Render dashboard
- Consider upgrading for production use

### **Security Considerations**
- Change default admin password immediately
- Use strong passwords for all accounts
- Enable HTTPS (automatic on Render)
- Regularly update dependencies

## 🎉 **Success Checklist**

- ✅ Database created and connected
- ✅ Application deployed and running
- ✅ Email verification working
- ✅ Admin login successful
- ✅ User registration working
- ✅ OAuth2 login functional (if configured)
- ✅ All features tested

## 🔗 **Useful Links**

- **Render Documentation**: https://render.com/docs
- **PostgreSQL on Render**: https://render.com/docs/databases
- **Spring Boot on Render**: https://render.com/docs/deploy-spring-boot
- **Brevo SMTP**: https://www.brevo.com/products/transactional-email/

---

**🎯 Your application will be live at**: `https://your-app-name.onrender.com`

**⚠️ Important**: Free tier apps sleep after 15 minutes of inactivity. First request after sleep may take 30-60 seconds to respond.