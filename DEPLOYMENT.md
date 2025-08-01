# Deployment Guide

## 🚀 Production Deployment Checklist

### Pre-Deployment Setup

#### 1. Environment Configuration
```bash
# Copy and configure production environment
cp .env .env.production
# Update all values for production environment
```

#### 2. Database Setup
```sql
-- Create production database
CREATE DATABASE user_management_prod;
CREATE USER app_user WITH PASSWORD 'strong_password_here';
GRANT ALL PRIVILEGES ON DATABASE user_management_prod TO app_user;
```

#### 3. Application Configuration
```bash
# Copy production config template
cp src/main/resources/application-prod.properties.template src/main/resources/application-prod.properties
# Update all production values
```

### Security Configuration

#### 4. SSL/TLS Setup
- Obtain SSL certificate (Let's Encrypt recommended)
- Configure reverse proxy (Nginx/Apache)
- Update OAuth2 redirect URLs to HTTPS

#### 5. reCAPTCHA Configuration
- Create new reCAPTCHA site for production domain
- Update site key and secret key in environment variables

#### 6. OAuth2 Configuration
- Update Google OAuth2 redirect URLs
- Update GitHub OAuth2 redirect URLs
- Test OAuth2 flows in production

### Build & Deploy

#### 7. Maven Build
```bash
# Clean and build for production
mvn clean package -Pprod
```

#### 8. Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/user-management-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
```

#### 9. Systemd Service (Linux)
```ini
[Unit]
Description=User Management System
After=network.target

[Service]
Type=simple
User=appuser
ExecStart=/usr/bin/java -jar /opt/user-management/app.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### Post-Deployment

#### 10. Health Checks
- [ ] Application starts successfully
- [ ] Database connection works
- [ ] Email sending works
- [ ] OAuth2 login works
- [ ] reCAPTCHA works
- [ ] SSL certificate valid

#### 11. Monitoring Setup
- [ ] Application logs configured
- [ ] Error monitoring (Sentry/similar)
- [ ] Performance monitoring
- [ ] Database monitoring

#### 12. Backup Strategy
- [ ] Database backup schedule
- [ ] Application backup
- [ ] Environment variables backup (encrypted)

### Environment Variables for Production

```bash
# Database
DATABASE_URL=jdbc:postgresql://prod-db:5432/user_management_prod
DATABASE_USERNAME=app_user
DATABASE_PASSWORD=strong_production_password

# Email
MAIL_USERNAME=production@yourdomain.com
BREVO_SMTP_PASSWORD=production_smtp_key

# OAuth2
GOOGLE_CLIENT_ID=production_google_client_id
GOOGLE_CLIENT_SECRET=production_google_secret
GITHUB_CLIENT_ID=production_github_client_id
GITHUB_CLIENT_SECRET=production_github_secret

# reCAPTCHA
RECAPTCHA_SITE_KEY=production_site_key
RECAPTCHA_SECRET_KEY=production_secret_key

# URLs (update domain)
GOOGLE_REDIRECT_URI=https://yourdomain.com/login/oauth2/code/google
GITHUB_REDIRECT_URI=https://yourdomain.com/login/oauth2/code/github
```

### Nginx Configuration Example

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/certificate.crt;
    ssl_certificate_key /path/to/private.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 80;
    server_name yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

### Troubleshooting

#### Common Issues
1. **OAuth2 redirect mismatch**: Update redirect URLs in OAuth2 providers
2. **reCAPTCHA domain error**: Update reCAPTCHA site configuration
3. **Database connection**: Check firewall and connection strings
4. **Email not sending**: Verify SMTP credentials and settings

#### Logs Location
```bash
# Application logs
tail -f /var/log/user-management/application.log

# System logs
journalctl -u user-management -f
```

### Security Hardening

#### Additional Security Measures
- [ ] Firewall configuration
- [ ] Rate limiting at proxy level
- [ ] Security headers configuration
- [ ] Regular security updates
- [ ] Vulnerability scanning

#### Monitoring & Alerts
- [ ] Failed login attempts monitoring
- [ ] Unusual activity alerts
- [ ] System resource monitoring
- [ ] Database performance monitoring