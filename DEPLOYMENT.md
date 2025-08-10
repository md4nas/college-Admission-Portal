# 🚀 College Admission Portal - Deployment Guide

<div align="center">
  <h2>☁️ Complete Production Deployment Guide</h2>
  <p>Comprehensive guide for deploying the College Admission Portal to various cloud platforms</p>
</div>

---

## 🌐 **Live Production Environment**

### **Current Deployment**
- **Platform**: Render.com
- **Live URL**: https://college-admission-portal-ax6b.onrender.com
- **Database**: PostgreSQL on Render
- **Environment**: Production
- **Status**: ✅ Active

### **Demo Credentials**
```
👑 Admin Access:
Email: admin@collegeportal.com
Password: [Contact support for demo credentials]

🎓 Teacher Access:
Email: testTdMail@clgportal.com
Password: [Contact support for demo credentials]

👤 Student Access:
Email: testStdMail@clgportal.com
Password: [Contact support for demo credentials]
```

---

## 📋 **Pre-Deployment Checklist**

### **Code Preparation**
- [ ] All features tested locally
- [ ] Database migrations ready
- [ ] Environment variables configured
- [ ] Security configurations verified
- [ ] Performance optimizations applied
- [ ] Error handling implemented
- [ ] Logging configured properly

### **Environment Setup**
- [ ] Production database created
- [ ] SMTP service configured
- [ ] OAuth2 applications registered
- [ ] SSL certificates obtained
- [ ] Domain name configured
- [ ] CDN setup (if required)
- [ ] Monitoring tools configured

### **Security Checklist**
- [ ] Sensitive data encrypted
- [ ] Environment variables secured
- [ ] HTTPS enforced
- [ ] CSRF protection enabled
- [ ] SQL injection prevention verified
- [ ] XSS protection implemented
- [ ] Rate limiting configured

---

## ☁️ **Render.com Deployment (Current)**

### **Step 1: Repository Setup**
```bash
# Ensure your code is in a Git repository
git init
git add .
git commit -m "Initial commit for deployment"
git remote add origin https://github.com/your-username/college-admission-portal.git
git push -u origin main
```

### **Step 2: Render Configuration**
Create `render.yaml` in your project root:
```yaml
services:
  - type: web
    name: college-admission-portal
    env: java
    plan: free  # or starter/standard for production
    buildCommand: mvn clean package -DskipTests
    startCommand: java -Dserver.port=$PORT -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar
    healthCheckPath: /actuator/health
    envVars:
      - key: DATABASE_URL
        fromDatabase:
          name: college-portal-db
          property: connectionString
      - key: DATABASE_USERNAME
        fromDatabase:
          name: college-portal-db
          property: user
      - key: DATABASE_PASSWORD
        fromDatabase:
          name: college-portal-db
          property: password
      - key: JAVA_OPTS
        value: -Xmx512m -Xms256m
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: SERVER_PORT
        value: 8080

databases:
  - name: college-portal-db
    databaseName: college_portal_db
    user: college_user
    plan: free  # or starter for production
```

### **Step 3: Environment Variables**
Configure these in Render Dashboard:
```bash
# Application Settings
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Database (Auto-configured by Render)
DATABASE_URL=postgresql://user:password@host:port/database
DATABASE_USERNAME=college_user
DATABASE_PASSWORD=auto_generated_password

# Email Configuration
MAIL_USERNAME=your_brevo_email@domain.com
BREVO_SMTP_PASSWORD=your_brevo_smtp_key

# OAuth2 Production Settings
GOOGLE_CLIENT_ID=your_production_google_client_id
GOOGLE_CLIENT_SECRET=your_production_google_client_secret
GOOGLE_REDIRECT_URI=https://your-app-name.onrender.com/login/oauth2/code/google
GOOGLE_AUTHORIZATION_URI=https://accounts.google.com/o/oauth2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_USER_INFO_URI=https://www.googleapis.com/oauth2/v2/userinfo

GITHUB_CLIENT_ID=your_production_github_client_id
GITHUB_CLIENT_SECRET=your_production_github_client_secret
GITHUB_REDIRECT_URI=https://your-app-name.onrender.com/login/oauth2/code/github

# Admin Configuration
ADMIN_EMAIL=admin@collegeportal.com
ADMIN_PASSWORD=YourSecureProductionPassword
ADMIN_NAME=System Administrator
```

### **Step 4: Production Configuration**
Create `application-prod.properties`:
```properties
# Database Configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.leak-detection-threshold=60000

# JPA Settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Security Settings
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict
server.servlet.session.timeout=30m

# Compression
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
server.compression.min-response-size=1024

# Logging
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=WARN
logging.level.com.m4nas=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Actuator for health checks
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

### **Step 5: Deploy to Render**
1. **Connect Repository**: Link your GitHub repository to Render
2. **Configure Service**: Set up web service with Java environment
3. **Add Database**: Create PostgreSQL database
4. **Set Environment Variables**: Configure all required variables
5. **Deploy**: Trigger deployment from Render dashboard

---

## 🐳 **Docker Deployment**

### **Dockerfile**
```dockerfile
# Multi-stage build for optimization
FROM maven:3.8.6-openjdk-17-slim AS build

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/UserManagemetPortal-0.0.1-SNAPSHOT.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### **Docker Compose for Local Development**
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=jdbc:postgresql://db:5432/college_portal_db
      - DATABASE_USERNAME=college_user
      - DATABASE_PASSWORD=college_password
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      - db
    restart: unless-stopped
    networks:
      - college-network

  db:
    image: postgres:13-alpine
    environment:
      - POSTGRES_DB=college_portal_db
      - POSTGRES_USER=college_user
      - POSTGRES_PASSWORD=college_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: unless-stopped
    networks:
      - college-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - college-network

volumes:
  postgres_data:

networks:
  college-network:
    driver: bridge
```

### **Production Docker Compose**
```yaml
version: '3.8'

services:
  app:
    image: your-registry/college-admission-portal:latest
    ports:
      - "8080:8080"
    environment:
      - DATABASE_URL=${DATABASE_URL}
      - DATABASE_USERNAME=${DATABASE_USERNAME}
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
      - SPRING_PROFILES_ACTIVE=prod
      - JAVA_OPTS=-Xmx1g -Xms512m
    volumes:
      - app_logs:/app/logs
      - app_uploads:/app/uploads
    restart: unless-stopped
    networks:
      - college-network
    deploy:
      resources:
        limits:
          memory: 1.5G
        reservations:
          memory: 512M

  db:
    image: postgres:13-alpine
    environment:
      - POSTGRES_DB=${POSTGRES_DB}
      - POSTGRES_USER=${POSTGRES_USER}
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - postgres_backups:/backups
    restart: unless-stopped
    networks:
      - college-network
    deploy:
      resources:
        limits:
          memory: 512M

  redis:
    image: redis:7-alpine
    command: redis-server --appendonly yes
    volumes:
      - redis_data:/data
    restart: unless-stopped
    networks:
      - college-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
      - nginx_logs:/var/log/nginx
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - college-network

volumes:
  postgres_data:
  postgres_backups:
  redis_data:
  app_logs:
  app_uploads:
  nginx_logs:

networks:
  college-network:
    driver: bridge
```

---

## ☁️ **AWS Deployment**

### **AWS Elastic Beanstalk**
```bash
# Install EB CLI
pip install awsebcli

# Initialize Elastic Beanstalk
eb init college-admission-portal --platform java-17 --region us-east-1

# Create environment
eb create production --database.engine postgres --database.username college_user

# Deploy application
eb deploy

# Set environment variables
eb setenv SPRING_PROFILES_ACTIVE=prod \
         ADMIN_EMAIL=admin@collegeportal.com \
         ADMIN_PASSWORD=YourSecurePassword \
         MAIL_USERNAME=your_email@domain.com \
         BREVO_SMTP_PASSWORD=your_smtp_key
```

### **AWS ECS with Fargate**
```yaml
# task-definition.json
{
  "family": "college-admission-portal",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "executionRoleArn": "arn:aws:iam::account:role/ecsTaskExecutionRole",
  "taskRoleArn": "arn:aws:iam::account:role/ecsTaskRole",
  "containerDefinitions": [
    {
      "name": "college-portal",
      "image": "your-account.dkr.ecr.region.amazonaws.com/college-admission-portal:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "DATABASE_URL",
          "valueFrom": "arn:aws:secretsmanager:region:account:secret:college-portal/database-url"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/college-admission-portal",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck": {
        "command": ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"],
        "interval": 30,
        "timeout": 5,
        "retries": 3,
        "startPeriod": 60
      }
    }
  ]
}
```

### **AWS RDS Configuration**
```bash
# Create RDS PostgreSQL instance
aws rds create-db-instance \
    --db-instance-identifier college-portal-db \
    --db-instance-class db.t3.micro \
    --engine postgres \
    --engine-version 13.7 \
    --master-username college_user \
    --master-user-password YourSecurePassword \
    --allocated-storage 20 \
    --storage-type gp2 \
    --vpc-security-group-ids sg-xxxxxxxxx \
    --db-subnet-group-name college-portal-subnet-group \
    --backup-retention-period 7 \
    --storage-encrypted
```

---

## 🌐 **Heroku Deployment**

### **Heroku Setup**
```bash
# Install Heroku CLI
# Create Heroku app
heroku create college-admission-portal

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set ADMIN_EMAIL=admin@collegeportal.com
heroku config:set ADMIN_PASSWORD=YourSecurePassword
heroku config:set MAIL_USERNAME=your_email@domain.com
heroku config:set BREVO_SMTP_PASSWORD=your_smtp_key

# Deploy
git push heroku main

# Scale dynos
heroku ps:scale web=1

# View logs
heroku logs --tail
```

### **Procfile**
```
web: java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/UserManagemetPortal-0.0.1-SNAPSHOT.jar
```

### **system.properties**
```
java.runtime.version=17
maven.version=3.8.6
```

---

## 🔧 **Google Cloud Platform (GCP)**

### **App Engine Deployment**
```yaml
# app.yaml
runtime: java17
service: default
instance_class: F2

env_variables:
  SPRING_PROFILES_ACTIVE: prod
  DATABASE_URL: jdbc:postgresql://google/college_portal_db?cloudSqlInstance=project:region:instance&socketFactory=com.google.cloud.sql.postgres.SocketFactory
  DATABASE_USERNAME: college_user
  DATABASE_PASSWORD: your_password

automatic_scaling:
  min_instances: 1
  max_instances: 10
  target_cpu_utilization: 0.6

health_check:
  enable_health_check: true
  check_interval_sec: 30
  timeout_sec: 4
  unhealthy_threshold: 2
  healthy_threshold: 2
  restart_threshold: 60
```

### **Cloud SQL Setup**
```bash
# Create Cloud SQL instance
gcloud sql instances create college-portal-db \
    --database-version=POSTGRES_13 \
    --tier=db-f1-micro \
    --region=us-central1

# Create database
gcloud sql databases create college_portal_db --instance=college-portal-db

# Create user
gcloud sql users create college_user --instance=college-portal-db --password=YourSecurePassword

# Deploy to App Engine
gcloud app deploy
```

---

## 🚀 **Kubernetes Deployment**

### **Deployment Configuration**
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: college-admission-portal
  labels:
    app: college-portal
spec:
  replicas: 3
  selector:
    matchLabels:
      app: college-portal
  template:
    metadata:
      labels:
        app: college-portal
    spec:
      containers:
      - name: college-portal
        image: your-registry/college-admission-portal:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: college-portal-secrets
              key: database-url
        - name: DATABASE_USERNAME
          valueFrom:
            secretKeyRef:
              name: college-portal-secrets
              key: database-username
        - name: DATABASE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: college-portal-secrets
              key: database-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: college-portal-service
spec:
  selector:
    app: college-portal
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: college-portal-ingress
  annotations:
    kubernetes.io/ingress.class: nginx
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
  - hosts:
    - collegeportal.com
    secretName: college-portal-tls
  rules:
  - host: collegeportal.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: college-portal-service
            port:
              number: 80
```

### **Secrets Configuration**
```yaml
# secrets.yaml
apiVersion: v1
kind: Secret
metadata:
  name: college-portal-secrets
type: Opaque
data:
  database-url: <base64-encoded-database-url>
  database-username: <base64-encoded-username>
  database-password: <base64-encoded-password>
  admin-email: <base64-encoded-admin-email>
  admin-password: <base64-encoded-admin-password>
  mail-username: <base64-encoded-mail-username>
  brevo-smtp-password: <base64-encoded-smtp-password>
```

---

## 🔒 **SSL/TLS Configuration**

### **Let's Encrypt with Certbot**
```bash
# Install Certbot
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx

# Obtain SSL certificate
sudo certbot --nginx -d collegeportal.com -d www.collegeportal.com

# Auto-renewal
sudo crontab -e
# Add: 0 12 * * * /usr/bin/certbot renew --quiet
```

### **Nginx SSL Configuration**
```nginx
# nginx.conf
server {
    listen 80;
    server_name collegeportal.com www.collegeportal.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name collegeportal.com www.collegeportal.com;

    ssl_certificate /etc/letsencrypt/live/collegeportal.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/collegeportal.com/privkey.pem;
    
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff;
    add_header X-Frame-Options DENY;
    add_header X-XSS-Protection "1; mode=block";

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Static files caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        proxy_pass http://localhost:8080;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

---

## 📊 **Monitoring & Logging**

### **Application Monitoring**
```properties
# application-prod.properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true

# Custom metrics
management.metrics.tags.application=college-admission-portal
management.metrics.tags.environment=production
```

### **Prometheus Configuration**
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'college-portal'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
```

### **Grafana Dashboard**
```json
{
  "dashboard": {
    "title": "College Admission Portal",
    "panels": [
      {
        "title": "HTTP Requests",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_seconds_count[5m])",
            "legendFormat": "{{method}} {{uri}}"
          }
        ]
      },
      {
        "title": "Database Connections",
        "type": "singlestat",
        "targets": [
          {
            "expr": "hikaricp_connections_active",
            "legendFormat": "Active Connections"
          }
        ]
      }
    ]
  }
}
```

### **Log Aggregation with ELK Stack**
```yaml
# docker-compose-elk.yml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.15.0
    environment:
      - discovery.type=single-node
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    ports:
      - "9200:9200"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data

  logstash:
    image: docker.elastic.co/logstash/logstash:7.15.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    ports:
      - "5044:5044"
    depends_on:
      - elasticsearch

  kibana:
    image: docker.elastic.co/kibana/kibana:7.15.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch

volumes:
  elasticsearch_data:
```

---

## 🔄 **CI/CD Pipeline**

### **GitHub Actions**
```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:13
        env:
          POSTGRES_PASSWORD: postgres
          POSTGRES_DB: test_db
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
        
    - name: Run tests
      run: mvn clean test
      env:
        DATABASE_URL: jdbc:postgresql://localhost:5432/test_db
        DATABASE_USERNAME: postgres
        DATABASE_PASSWORD: postgres

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        
    - name: Build with Maven
      run: mvn clean package -DskipTests
      
    - name: Build Docker image
      run: |
        docker build -t college-admission-portal:${{ github.sha }} .
        docker tag college-admission-portal:${{ github.sha }} college-admission-portal:latest
        
    - name: Deploy to Render
      run: |
        curl -X POST "${{ secrets.RENDER_DEPLOY_HOOK_URL }}"
```

### **Jenkins Pipeline**
```groovy
// Jenkinsfile
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.8.6'
        jdk 'JDK-17'
    }
    
    environment {
        DOCKER_REGISTRY = 'your-registry.com'
        IMAGE_NAME = 'college-admission-portal'
        KUBECONFIG = credentials('kubeconfig')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'mvn clean test'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    def image = docker.build("${DOCKER_REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}")
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                        image.push()
                        image.push('latest')
                    }
                }
            }
        }
        
        stage('Deploy to Kubernetes') {
            steps {
                sh """
                    sed -i 's|IMAGE_TAG|${BUILD_NUMBER}|g' k8s/deployment.yaml
                    kubectl apply -f k8s/
                """
            }
        }
    }
    
    post {
        success {
            slackSend channel: '#deployments',
                     color: 'good',
                     message: "✅ College Portal deployed successfully - Build #${BUILD_NUMBER}"
        }
        failure {
            slackSend channel: '#deployments',
                     color: 'danger',
                     message: "❌ College Portal deployment failed - Build #${BUILD_NUMBER}"
        }
    }
}
```

---

## 🔧 **Environment-Specific Configurations**

### **Development Environment**
```properties
# application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/college_portal_dev
spring.datasource.username=dev_user
spring.datasource.password=dev_password

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

logging.level.org.springframework.web=DEBUG
logging.level.com.m4nas=DEBUG

# Disable security for development
spring.security.oauth2.client.registration.google.client-id=dev_client_id
spring.security.oauth2.client.registration.github.client-id=dev_client_id
```

### **Staging Environment**
```properties
# application-staging.properties
spring.datasource.url=${STAGING_DATABASE_URL}
spring.datasource.username=${STAGING_DATABASE_USERNAME}
spring.datasource.password=${STAGING_DATABASE_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Staging-specific OAuth2 settings
spring.security.oauth2.client.registration.google.redirect-uri=https://staging.collegeportal.com/login/oauth2/code/google
spring.security.oauth2.client.registration.github.redirect-uri=https://staging.collegeportal.com/login/oauth2/code/github

# Email settings for staging
spring.mail.host=smtp-relay.brevo.com
spring.mail.username=${STAGING_MAIL_USERNAME}
spring.mail.password=${STAGING_BREVO_SMTP_PASSWORD}
```

### **Production Environment**
```properties
# application-prod.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Production security settings
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=strict

# Production OAuth2 settings
spring.security.oauth2.client.registration.google.redirect-uri=https://collegeportal.com/login/oauth2/code/google
spring.security.oauth2.client.registration.github.redirect-uri=https://collegeportal.com/login/oauth2/code/github

# Production email settings
spring.mail.host=smtp-relay.brevo.com
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${BREVO_SMTP_PASSWORD}

# Actuator security
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
```

---

## 🚨 **Troubleshooting Deployment Issues**

### **Common Issues & Solutions**

#### **Database Connection Issues**
```bash
# Check database connectivity
psql -h your-db-host -U your-username -d your-database

# Test from application
curl https://your-app.com/actuator/health

# Check environment variables
echo $DATABASE_URL
```

#### **Memory Issues**
```bash
# Check memory usage
docker stats

# Increase JVM heap size
export JAVA_OPTS="-Xmx1g -Xms512m"

# Monitor memory in Kubernetes
kubectl top pods
```

#### **SSL Certificate Issues**
```bash
# Check certificate validity
openssl s_client -connect your-domain.com:443

# Renew Let's Encrypt certificate
sudo certbot renew

# Check certificate expiration
curl -vI https://your-domain.com 2>&1 | grep -i expire
```

#### **OAuth2 Configuration Issues**
```bash
# Verify redirect URIs match exactly
# Check OAuth2 application settings in provider console
# Ensure environment variables are set correctly

# Test OAuth2 flow
curl -v "https://accounts.google.com/o/oauth2/auth?client_id=YOUR_CLIENT_ID&redirect_uri=YOUR_REDIRECT_URI&scope=openid%20email%20profile&response_type=code"
```

### **Health Check Endpoints**
```bash
# Application health
curl https://your-app.com/actuator/health

# Database health
curl https://your-app.com/actuator/health/db

# Disk space
curl https://your-app.com/actuator/health/diskSpace

# Application info
curl https://your-app.com/actuator/info
```

### **Log Analysis**
```bash
# Application logs
tail -f /var/log/college-portal/application.log

# Database logs
tail -f /var/log/postgresql/postgresql-13-main.log

# Nginx logs
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# Docker logs
docker logs -f container-name

# Kubernetes logs
kubectl logs -f deployment/college-admission-portal
```

---

## 📈 **Performance Optimization**

### **Application Performance**
```properties
# JVM tuning
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200

# Connection pool optimization
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000

# JPA optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### **Database Performance**
```sql
-- Create indexes for better performance
CREATE INDEX CONCURRENTLY idx_user_email_enable ON user_dtls(email, enable);
CREATE INDEX CONCURRENTLY idx_application_status_course ON user_applications(status, course);
CREATE INDEX CONCURRENTLY idx_payment_status_date ON payments(status, submission_date);

-- Analyze tables
ANALYZE user_dtls;
ANALYZE user_applications;
ANALYZE payments;
ANALYZE announcements;
```

### **Caching Strategy**
```java
// Enable caching
@EnableCaching
@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(10)));
        return cacheManager;
    }
}

// Cache frequently accessed data
@Cacheable("users")
public UserDtls getUserByEmail(String email) {
    return userRepository.findByEmail(email);
}
```

---

## 🔐 **Security Hardening**

### **Production Security Checklist**
- [ ] HTTPS enforced with valid SSL certificate
- [ ] Security headers configured (HSTS, CSP, etc.)
- [ ] Database credentials secured with secrets management
- [ ] OAuth2 applications configured with production URLs
- [ ] Rate limiting implemented
- [ ] Input validation and sanitization
- [ ] SQL injection prevention verified
- [ ] XSS protection enabled
- [ ] CSRF protection configured
- [ ] Session security hardened
- [ ] File upload restrictions implemented
- [ ] Error messages don't expose sensitive information
- [ ] Logging configured without sensitive data
- [ ] Regular security updates scheduled

### **Security Headers Configuration**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
            .frameOptions().deny()
            .contentTypeOptions().and()
            .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                .maxAgeInSeconds(31536000)
                .includeSubdomains(true))
            .and()
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false))
        );
        return http.build();
    }
}
```

---

## 📞 **Support & Maintenance**

### **Production Support Contacts**
- **Technical Lead**: tech-lead@collegeportal.com
- **DevOps Team**: devops@collegeportal.com
- **Database Admin**: dba@collegeportal.com
- **Security Team**: security@collegeportal.com

### **Maintenance Schedule**
- **Daily**: Log monitoring, health checks
- **Weekly**: Security updates, performance review
- **Monthly**: Database maintenance, backup verification
- **Quarterly**: Security audit, disaster recovery testing

### **Emergency Procedures**
1. **Incident Response**: Contact on-call engineer
2. **Rollback Process**: Revert to previous stable version
3. **Database Recovery**: Restore from latest backup
4. **Communication**: Update status page and notify users

---

**🚀 Deployment Status**: Production Ready  
**📅 Last Updated**: February 4, 2025  
**👨💻 DevOps Team**: College Technical Team  
**📧 Support**: devops@collegeportal.com  
**🌐 Live URL**: https://college-admission-portal-ax6b.onrender.com