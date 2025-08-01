# 🚀 UserAuth Deployment Guide

## 📋 Prerequisites

### System Requirements
- **Java:** 17 or higher
- **Maven:** 3.8+
- **PostgreSQL:** 13+
- **Memory:** Minimum 2GB RAM
- **Storage:** 1GB free space

### Required Accounts
- **Brevo Account:** For SMTP email service
- **Google Cloud Console:** For Google OAuth2 (optional)
- **GitHub Developer:** For GitHub OAuth2 (optional)

## 🔧 Environment Configuration

### 1. Database Setup

#### PostgreSQL Installation
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# CentOS/RHEL
sudo yum install postgresql-server postgresql-contrib
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### Database Creation
```sql
-- Connect to PostgreSQL
sudo -u postgres psql

-- Create database and user
CREATE DATABASE userauth_db;
CREATE USER userauth_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE userauth_db TO userauth_user;
ALTER USER userauth_user CREATEDB;

-- Exit PostgreSQL
\q
```

### 2. Environment Variables

Create `.env` file in project root:
```env
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/userauth_db
DATABASE_USERNAME=userauth_user
DATABASE_PASSWORD=your_secure_password

# Email Configuration (Brevo SMTP)
MAIL_USERNAME=your_brevo_email@domain.com
BREVO_SMTP_PASSWORD=your_brevo_api_key

# OAuth2 Configuration (Optional)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# OAuth2 Redirect URIs
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
GITHUB_REDIRECT_URI=http://localhost:8080/login/oauth2/code/github

# Google OAuth2 URLs
GOOGLE_AUTHORIZATION_URI=https://accounts.google.com/o/oauth2/v2/auth
GOOGLE_TOKEN_URI=https://www.googleapis.com/oauth2/v4/token
GOOGLE_USER_INFO_URI=https://www.googleapis.com/oauth2/v3/userinfo

# Admin Configuration
ADMIN_EMAIL=admin@yourcompany.com
ADMIN_PASSWORD=SecureAdminPass123!
ADMIN_NAME=System Administrator

# Server Configuration
SERVER_PORT=8080
```

## 🏗️ Local Development

### 1. Clone Repository
```bash
git clone <repository-url>
cd UserAuth-System
```

### 2. Build Application
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package -DskipTests
```

### 3. Run Application
```bash
# Using Maven
mvn spring-boot:run

# Using JAR file
java -jar target/userauth-system-1.0.0.jar

# With custom profile
java -jar target/userauth-system-1.0.0.jar --spring.profiles.active=prod
```

## 🐳 Docker Deployment

### 1. Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/userauth-system-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Docker Compose
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=jdbc:postgresql://db:5432/userauth_db
      - DATABASE_USERNAME=userauth_user
      - DATABASE_PASSWORD=secure_password
    depends_on:
      - db

  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=userauth_db
      - POSTGRES_USER=userauth_user
      - POSTGRES_PASSWORD=secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

volumes:
  postgres_data:
```

### 3. Deploy with Docker
```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## ☁️ Cloud Deployment

### AWS Deployment

#### 1. Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize EB application
eb init

# Create environment
eb create production

# Deploy application
eb deploy
```

#### 2. EC2 Deployment
```bash
# Connect to EC2 instance
ssh -i your-key.pem ec2-user@your-instance-ip

# Install Java 17
sudo yum update
sudo yum install java-17-openjdk

# Install PostgreSQL
sudo yum install postgresql-server postgresql-contrib

# Transfer and run application
scp -i your-key.pem target/userauth-system-1.0.0.jar ec2-user@your-instance-ip:~/
java -jar userauth-system-1.0.0.jar
```

### Heroku Deployment

#### 1. Prepare Application
```bash
# Install Heroku CLI
# Create Procfile
echo "web: java -jar target/userauth-system-1.0.0.jar --server.port=\$PORT" > Procfile

# Create system.properties
echo "java.runtime.version=17" > system.properties
```

#### 2. Deploy to Heroku
```bash
# Login to Heroku
heroku login

# Create application
heroku create your-app-name

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set MAIL_USERNAME=your_email
heroku config:set BREVO_SMTP_PASSWORD=your_api_key

# Deploy application
git add .
git commit -m "Deploy to Heroku"
git push heroku main
```

## 🔒 Production Security

### 1. SSL/TLS Configuration
```properties
# application-prod.properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your_keystore_password
server.ssl.key-store-type=PKCS12
```

### 2. Database Security
```properties
# Use connection pooling
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Enable SSL for database
spring.datasource.url=jdbc:postgresql://localhost:5432/userauth_db?sslmode=require
```

### 3. Application Security
```properties
# Security headers
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# Logging
logging.level.org.springframework.security=INFO
logging.level.com.m4nas=INFO
```

## 📊 Monitoring & Maintenance

### 1. Health Checks
```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connection
curl http://localhost:8080/actuator/health/db
```

### 2. Log Management
```properties
# Logging configuration
logging.file.name=logs/userauth.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.level.root=INFO
```

### 3. Backup Strategy
```bash
# Database backup
pg_dump -h localhost -U userauth_user userauth_db > backup_$(date +%Y%m%d).sql

# Restore database
psql -h localhost -U userauth_user userauth_db < backup_20250101.sql
```

## 🚨 Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check database connectivity
psql -h localhost -U userauth_user -d userauth_db
```

#### 2. Email Not Sending
- Verify Brevo SMTP credentials
- Check firewall settings for port 587
- Validate email templates

#### 3. OAuth2 Issues
- Verify client IDs and secrets
- Check redirect URIs in OAuth providers
- Ensure proper SSL configuration

#### 4. Memory Issues
```bash
# Increase JVM memory
java -Xmx2g -Xms1g -jar userauth-system-1.0.0.jar
```

## 📈 Performance Optimization

### 1. JVM Tuning
```bash
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xmx2g -jar app.jar
```

### 2. Database Optimization
```sql
-- Create indexes for better performance
CREATE INDEX idx_user_email ON user_dtls(email);
CREATE INDEX idx_user_role ON user_dtls(role);
CREATE INDEX idx_user_enabled ON user_dtls(enable);
```

### 3. Caching Configuration
```properties
# Enable caching
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m
```