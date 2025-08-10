# 📡 College Admission Portal - API Documentation

<div align="center">
  <h2>🔌 Complete REST API Reference</h2>
  <p>Comprehensive documentation for all API endpoints, authentication, and integration</p>
</div>

---

## 📋 **API Overview**

### **Base Information**
- **Base URL**: `https://your-domain.com` (Replace with your actual domain)
- **API Version**: v1.0
- **Protocol**: HTTPS
- **Authentication**: Session-based + OAuth2
- **Content Type**: `application/json`, `multipart/form-data`
- **Response Format**: JSON, HTML (for web endpoints)

### **API Categories**
1. **Authentication APIs** - Login, registration, OAuth2
2. **User Management APIs** - Profile, user operations
3. **Application APIs** - Admission applications
4. **Payment APIs** - Fee management
5. **Announcement APIs** - Communication system
6. **Admin APIs** - System administration
7. **Teacher APIs** - Academic management
8. **Health Check APIs** - System monitoring

---

## 🔐 **Authentication**

### **Authentication Methods**

#### **1. Session-Based Authentication**
```http
POST /login
Content-Type: application/x-www-form-urlencoded

username=user@example.com&password=userpassword
```

**Response:**
```http
HTTP/1.1 302 Found
Location: /user/
Set-Cookie: JSESSIONID=ABC123; Path=/; HttpOnly; Secure
```

#### **2. OAuth2 Authentication**
```http
GET /oauth2/authorization/google
GET /oauth2/authorization/github
```

**OAuth2 Flow:**
1. Redirect to provider authorization URL
2. User authorizes application
3. Provider redirects back with authorization code
4. Application exchanges code for access token
5. User profile created/updated automatically

### **Session Management**
- **Session Timeout**: 30 minutes of inactivity
- **Concurrent Sessions**: 1 per user
- **Session Security**: HttpOnly, Secure, SameSite=Strict

---

## 👤 **Authentication APIs**

### **User Registration**
```http
POST /createUser
Content-Type: application/x-www-form-urlencoded

fullName=John Doe&email=john@example.com&password=password123&confirmPassword=password123
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| fullName | string | Yes | User's full name |
| email | string | Yes | Valid email address |
| password | string | Yes | Minimum 6 characters |
| confirmPassword | string | Yes | Must match password |

**Response:**
```http
HTTP/1.1 302 Found
Location: /register?success=true
```

**Error Response:**
```http
HTTP/1.1 302 Found
Location: /register?error=email_exists
```

### **Email Verification**
```http
GET /verify?code={verification_code}
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | string | Yes | Email verification token |

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- Verification success page -->
```

### **Login**
```http
POST /login
Content-Type: application/x-www-form-urlencoded

username=user@example.com&password=userpassword
```

**Response (Success):**
```http
HTTP/1.1 302 Found
Location: /user/  # or /admin/ or /teacher/ based on role
Set-Cookie: JSESSIONID=ABC123; Path=/; HttpOnly; Secure
```

**Response (Failure):**
```http
HTTP/1.1 302 Found
Location: /signin?error=invalid_credentials
```

### **Logout**
```http
POST /logout
```

**Response:**
```http
HTTP/1.1 302 Found
Location: /signin?logout=true
```

---

## 🔑 **Password Recovery APIs**

### **Forgot Password**
```http
POST /send-otp
Content-Type: application/x-www-form-urlencoded

email=user@example.com
```

**Response:**
```http
HTTP/1.1 302 Found
Location: /verify-otp-page
```

### **Verify OTP**
```http
POST /verify-otp
Content-Type: application/x-www-form-urlencoded

otp=123456
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- Password reset form -->
```

### **Reset Password**
```http
POST /reset-password
Content-Type: application/x-www-form-urlencoded

password=newpassword123&confirmPassword=newpassword123
```

**Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- Success message -->
```

---

## 👥 **User Management APIs**

### **Get User Profile**
```http
GET /user/profile/{userId}
Authorization: Session-based
```

**Response:**
```json
{
  "id": "USER_123",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "ROLE_USER",
  "provider": "local",
  "enabled": true,
  "createdAt": "2025-01-15T10:30:00Z"
}
```

### **Update User Profile**
```http
POST /user/update-profile
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based

fullName=John Smith&email=johnsmith@example.com
```

**Response:**
```http
HTTP/1.1 302 Found
Location: /user/profile?success=true
```

---

## 📝 **Application APIs**

### **Submit Application**
```http
POST /user/submit-application
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_USER)

dob=2003-05-15&gender=Male&phoneNo=9876543210&address=123 Main St&city=Mumbai&state=Maharashtra&pincode=400001&parentsName=Robert Doe&parentsPhoneNo=9876543211&class10PassingYear=2019&class10SchoolName=ABC School&class10Board=CBSE&class10Percentage=95.5&class12PassingYear=2021&class12SchoolName=XYZ School&class12Board=CBSE&class12Percentage=92.75&entranceExamName=JEE Main&entranceExamRollNo=JEE2021123456&entranceExamYear=2021&entranceExamRank=1250&course=B.Tech&branch1=Computer Science&branch2=Electronics
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| dob | date | Yes | Date of birth (YYYY-MM-DD) |
| gender | string | Yes | Male/Female/Other |
| phoneNo | string | Yes | 10-digit phone number |
| address | string | Yes | Full address |
| city | string | Yes | City name |
| state | string | Yes | State name |
| pincode | string | Yes | 6-digit pincode |
| parentsName | string | Yes | Parent/Guardian name |
| parentsPhoneNo | string | Yes | Parent contact number |
| class10PassingYear | integer | Yes | Class 10 passing year |
| class10SchoolName | string | Yes | School name |
| class10Board | string | Yes | Education board |
| class10Percentage | decimal | Yes | Percentage (0-100) |
| class12PassingYear | integer | Yes | Class 12 passing year |
| class12SchoolName | string | Yes | School name |
| class12Board | string | Yes | Education board |
| class12Percentage | decimal | Yes | Percentage (0-100) |
| entranceExamName | string | No | Exam name (JEE, NEET, etc.) |
| entranceExamRollNo | string | No | Roll number |
| entranceExamYear | integer | No | Exam year |
| entranceExamRank | integer | No | Rank obtained |
| course | string | Yes | Course (B.Tech, M.Tech, MBA) |
| branch1 | string | Yes | First preference |
| branch2 | string | No | Second preference |

**⚠️ SECURITY WARNING**: Application submissions are rate-limited to prevent spam. Only 1 application per user allowed.

**Response:**
```http
HTTP/1.1 302 Found
Location: /user/application-status?success=true
```

### **Get Application Status**
```http
GET /user/application-status/{userId}
Authorization: Session-based (ROLE_USER)
```

**Response:**
```json
{
  "applicationId": "APP_123",
  "status": "UNDER_REVIEW",
  "course": "B.Tech",
  "branch1": "Computer Science",
  "branch2": "Electronics",
  "allocatedBranch": null,
  "seatAccepted": false,
  "applicationDate": "2025-01-15T10:30:00Z",
  "lastUpdated": "2025-01-20T14:45:00Z"
}
```

**Status Values:**
- `PENDING` - Application submitted, awaiting review
- `UNDER_REVIEW` - Being reviewed by admission committee
- `ADMITTED` - Application accepted
- `REJECTED` - Application rejected

### **Accept/Reject Seat**
```http
POST /user/seat-response
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_USER)

applicationId=APP_123&response=accept
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| applicationId | string | Yes | Application ID |
| response | string | Yes | "accept" or "reject" |

**Response:**
```http
HTTP/1.1 302 Found
Location: /user/application-status?seat_response=success
```

---

## 💰 **Payment APIs**

### **Submit Payment**
```http
POST /user/submit-payment
Content-Type: multipart/form-data
Authorization: Session-based (ROLE_USER)

studentName=John Doe&course=B.Tech&branch=Computer Science&amount=150000&paymentMethod=bank_transfer&transactionId=TXN123456789&receiptFile=@receipt.pdf
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| studentName | string | Yes | Student's full name |
| course | string | Yes | Course name |
| branch | string | Yes | Branch name |
| amount | decimal | Yes | Payment amount |
| paymentMethod | string | Yes | bank_transfer/upi/card/cash |
| transactionId | string | Yes | Transaction reference |
| receiptFile | file | Yes | Payment receipt (PDF/Image, **MAX: 5MB**) |

**⚠️ SECURITY WARNING**: File uploads are scanned for malware and limited to 5MB. Only PDF and image formats allowed.

**Response:**
```http
HTTP/1.1 302 Found
Location: /user/payment-history?success=true
```

### **Get Payment History**
```http
GET /user/payment-history/{userId}
Authorization: Session-based (ROLE_USER)
```

**Response:**
```json
{
  "payments": [
    {
      "id": 1,
      "studentName": "John Doe",
      "course": "B.Tech",
      "branch": "Computer Science",
      "amount": 150000.00,
      "paymentMethod": "bank_transfer",
      "transactionId": "TXN123456789",
      "status": "VERIFIED",
      "submissionDate": "2025-01-15T10:30:00Z",
      "verificationDate": "2025-01-16T09:15:00Z",
      "verifiedBy": "teacher@collegeportal.com"
    }
  ],
  "totalAmount": 150000.00,
  "pendingAmount": 0.00
}
```

**Payment Status Values:**
- `PENDING` - Payment submitted, awaiting verification
- `VERIFIED` - Payment verified and approved
- `REJECTED` - Payment rejected (invalid receipt/details)

---

## 📢 **Announcement APIs**

### **Get Announcements**
```http
GET /api/announcements?audience=STUDENTS&active=true
Authorization: Session-based
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| audience | string | No | ALL/STUDENTS/TEACHERS/PROSPECTIVE/ADMITTED |
| active | boolean | No | Filter by active status |
| type | string | No | GENERAL/ADMISSION/EXAM/PAYMENT/EVENT |
| limit | integer | No | Number of results (default: 10) |

**Response:**
```json
{
  "announcements": [
    {
      "id": 1,
      "title": "Application Deadline Extended",
      "content": "The application deadline has been extended to March 31, 2025.",
      "createdBy": "admin@collegeportal.com",
      "createdAt": "2025-01-15T10:30:00Z",
      "creatorRole": "ROLE_ADMIN",
      "eventDate": "2025-03-31",
      "eventTime": "23:59",
      "active": true,
      "targetAudience": "PROSPECTIVE",
      "announcementType": "ADMISSION"
    }
  ],
  "totalCount": 5,
  "hasMore": false
}
```

---

## 👑 **Admin APIs**

### **Get All Users**
```http
GET /admin/users?role=ROLE_USER&page=0&size=10
Authorization: Session-based (ROLE_ADMIN)
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| role | string | No | Filter by role |
| enabled | boolean | No | Filter by account status |
| provider | string | No | Filter by auth provider |
| page | integer | No | Page number (0-based) |
| size | integer | No | Page size (default: 10, **MAX: 50**) |

**⚠️ SECURITY WARNING**: This endpoint is rate-limited to prevent server overload. Maximum 50 records per request.

**Response:**
```json
{
  "users": [
    {
      "id": "USER_123",
      "fullName": "John Doe",
      "email": "john@example.com",
      "role": "ROLE_USER",
      "enabled": true,
      "provider": "local",
      "createdAt": "2025-01-15T10:30:00Z",
      "lastLogin": "2025-01-20T14:30:00Z"
    }
  ],
  "totalElements": 150,
  "totalPages": 15,
  "currentPage": 0,
  "hasNext": true
}
```

### **Update User Role**
```http
POST /admin/update-user-role
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_ADMIN)

userId=USER_123&newRole=ROLE_TEACHER
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| userId | string | Yes | User ID to update |
| newRole | string | Yes | ROLE_USER/ROLE_TEACHER/ROLE_ADMIN |

**Response:**
```http
HTTP/1.1 302 Found
Location: /admin/users?success=role_updated
```

### **Get System Statistics**
```http
GET /admin/statistics
Authorization: Session-based (ROLE_ADMIN)
```

**⚠️ PERFORMANCE WARNING**: This endpoint performs expensive database calculations. Results are cached for 5 minutes. Rate limited to 2 requests per minute per admin.

**Response:**
```json
{
  "userStats": {
    "totalUsers": 1250,
    "activeUsers": 1180,
    "newUsersThisMonth": 85,
    "usersByProvider": {
      "local": 800,
      "google": 300,
      "github": 150
    }
  },
  "applicationStats": {
    "totalApplications": 950,
    "pendingApplications": 120,
    "admittedApplications": 680,
    "rejectedApplications": 150,
    "applicationsByStatus": {
      "PENDING": 120,
      "UNDER_REVIEW": 45,
      "ADMITTED": 680,
      "REJECTED": 150
    }
  },
  "paymentStats": {
    "totalPayments": 720,
    "verifiedPayments": 680,
    "pendingPayments": 25,
    "rejectedPayments": 15,
    "totalRevenue": 102000000.00,
    "revenueThisMonth": 8500000.00
  },
  "announcementStats": {
    "totalAnnouncements": 45,
    "activeAnnouncements": 12,
    "announcementsByType": {
      "GENERAL": 15,
      "ADMISSION": 12,
      "EXAM": 8,
      "PAYMENT": 6,
      "EVENT": 4
    }
  }
}
```

### **Create Announcement**
```http
POST /admin/create-announcement
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_ADMIN)

title=Important Notice&content=This is an important announcement&targetAudience=ALL&announcementType=GENERAL&eventDate=2025-02-15&eventTime=10:00
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| title | string | Yes | Announcement title |
| content | text | Yes | Announcement content |
| targetAudience | string | Yes | ALL/STUDENTS/TEACHERS/PROSPECTIVE/ADMITTED |
| announcementType | string | Yes | GENERAL/ADMISSION/EXAM/PAYMENT/EVENT |
| eventDate | date | No | Event date (YYYY-MM-DD) |
| eventTime | string | No | Event time (HH:MM) |

**Response:**
```http
HTTP/1.1 302 Found
Location: /admin/announcements?success=created
```

---

## 🎓 **Teacher APIs**

### **Get Students**
```http
GET /teacher/students?course=B.Tech&status=ADMITTED&page=0&size=20
Authorization: Session-based (ROLE_TEACHER)
```

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| course | string | No | Filter by course |
| branch | string | No | Filter by branch |
| status | string | No | Filter by application status |
| page | integer | No | Page number (0-based) |
| size | integer | No | Page size (default: 20, **MAX: 100**) |

**⚠️ SECURITY WARNING**: Large queries are cached and rate-limited to prevent database overload.

**Response:**
```json
{
  "students": [
    {
      "applicationId": "APP_123",
      "studentName": "John Doe",
      "email": "john@example.com",
      "course": "B.Tech",
      "branch": "Computer Science",
      "status": "ADMITTED",
      "class12Percentage": 92.75,
      "entranceExamRank": 1250,
      "applicationDate": "2025-01-15T10:30:00Z",
      "allocatedBranch": "Computer Science",
      "seatAccepted": true
    }
  ],
  "totalElements": 85,
  "totalPages": 5,
  "currentPage": 0
}
```

### **Update Application Status**
```http
POST /teacher/update-application-status
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_TEACHER)

applicationId=APP_123&status=ADMITTED&allocatedBranch=Computer Science
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| applicationId | string | Yes | Application ID |
| status | string | Yes | PENDING/UNDER_REVIEW/ADMITTED/REJECTED |
| allocatedBranch | string | No | Branch allocated (for ADMITTED status) |

**Response:**
```http
HTTP/1.1 302 Found
Location: /teacher/applications?success=status_updated
```

### **Verify Payment**
```http
POST /teacher/verify-payment
Content-Type: application/x-www-form-urlencoded
Authorization: Session-based (ROLE_TEACHER)

paymentId=1&status=VERIFIED&remarks=Payment verified successfully
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| paymentId | integer | Yes | Payment ID |
| status | string | Yes | VERIFIED/REJECTED |
| remarks | string | No | Verification remarks |

**Response:**
```http
HTTP/1.1 302 Found
Location: /teacher/payments?success=payment_verified
```

---

## 🏥 **Health Check APIs**

### **Application Health**
```http
GET /actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 10737418240,
        "free": 8589934592,
        "threshold": 10485760,
        "exists": true
      }
    },
    "mail": {
      "status": "UP",
      "details": {
        "location": "smtp-relay.brevo.com:587"
      }
    }
  }
}
```

### **Application Info**
```http
GET /actuator/info
```

**Response:**
```json
{
  "app": {
    "name": "College Admission Portal",
    "version": "1.0.0",
    "description": "Complete college admission management system"
  },
  "build": {
    "version": "1.0.0",
    "artifact": "UserManagemetPortal",
    "name": "College Admission Portal",
    "time": "2025-02-04T10:30:00.000Z"
  },
  "java": {
    "version": "17.0.2",
    "vendor": {
      "name": "Eclipse Adoptium"
    }
  }
}
```

### **Metrics**
```http
GET /actuator/metrics
Authorization: Session-based (ROLE_ADMIN)
```

**⚠️ CRITICAL WARNING**: This endpoint exposes sensitive system metrics and is restricted to ADMIN users only. Rate limited to 5 requests per minute.

**Response:**
```json
{
  "names": [
    "jvm.memory.used",
    "jvm.memory.max",
    "http.server.requests",
    "hikaricp.connections.active",
    "hikaricp.connections.max",
    "system.cpu.usage",
    "process.uptime"
  ]
}
```

### **Specific Metric**
```http
GET /actuator/metrics/http.server.requests
Authorization: Session-based (ROLE_ADMIN)
```

**Response:**
```json
{
  "name": "http.server.requests",
  "description": "Duration of HTTP server request handling",
  "baseUnit": "seconds",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 1250.0
    },
    {
      "statistic": "TOTAL_TIME",
      "value": 45.5
    },
    {
      "statistic": "MAX",
      "value": 2.1
    }
  ],
  "availableTags": [
    {
      "tag": "exception",
      "values": ["None", "HttpMessageNotReadableException"]
    },
    {
      "tag": "method",
      "values": ["GET", "POST"]
    },
    {
      "tag": "status",
      "values": ["200", "302", "404", "500"]
    },
    {
      "tag": "uri",
      "values": ["/", "/login", "/user/**", "/admin/**"]
    }
  ]
}
```

---

## 🔒 **Security & Authorization**

### **Role-Based Access Control**

| Endpoint Pattern | ADMIN | TEACHER | USER | Anonymous |
|------------------|-------|---------|------|-----------|
| `/` | ✅ | ✅ | ✅ | ✅ |
| `/register` | ✅ | ✅ | ✅ | ✅ |
| `/signin` | ✅ | ✅ | ✅ | ✅ |
| `/forgot-password` | ✅ | ✅ | ✅ | ✅ |
| `/oauth2/**` | ✅ | ✅ | ✅ | ✅ |
| `/user/**` | ✅ | ❌ | ✅ | ❌ |
| `/teacher/**` | ✅ | ✅ | ❌ | ❌ |
| `/admin/**` | ✅ | ❌ | ❌ | ❌ |
| `/actuator/health` | ✅ | ✅ | ✅ | ✅ |
| `/actuator/**` | ✅ | ❌ | ❌ | ❌ |

### **Security Headers**
All API responses include security headers:
```http
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Content-Security-Policy: default-src 'self'
```

### **Rate Limiting & Security Controls**
- **Login Attempts**: 5 attempts per 15 minutes per IP
- **Registration**: 3 registrations per hour per IP
- **Password Reset**: 3 OTP requests per hour per email
- **API Calls**: 100 requests per minute per session
- **Admin Endpoints**: 10 requests per minute (STRICT)
- **Data Export**: 1 request per 5 minutes per user
- **File Uploads**: 5MB max size, 3 files per hour
- **Bulk Operations**: Admin only, 1 per minute

---

## 📊 **Error Handling**

### **HTTP Status Codes**
| Status Code | Description | Usage |
|-------------|-------------|-------|
| 200 | OK | Successful GET requests |
| 201 | Created | Successful resource creation |
| 302 | Found | Successful form submissions (redirect) |
| 400 | Bad Request | Invalid request parameters |
| 401 | Unauthorized | Authentication required |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 422 | Unprocessable Entity | Validation errors |
| 500 | Internal Server Error | Server-side errors |

### **Error Response Format**
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input parameters",
    "details": [
      {
        "field": "email",
        "message": "Email address is required"
      },
      {
        "field": "password",
        "message": "Password must be at least 6 characters"
      }
    ],
    "timestamp": "2025-02-04T10:30:00Z",
    "path": "/createUser"
  }
}
```

### **Common Error Codes**
| Error Code | Description |
|------------|-------------|
| `VALIDATION_ERROR` | Input validation failed |
| `AUTHENTICATION_FAILED` | Invalid credentials |
| `AUTHORIZATION_DENIED` | Insufficient permissions |
| `RESOURCE_NOT_FOUND` | Requested resource not found |
| `DUPLICATE_RESOURCE` | Resource already exists |
| `EXTERNAL_SERVICE_ERROR` | Third-party service error |
| `DATABASE_ERROR` | Database operation failed |
| `FILE_UPLOAD_ERROR` | File upload failed |

---

## 🧪 **API Testing**

### **Postman Collection**
```json
{
  "info": {
    "name": "College Admission Portal API",
    "description": "Complete API collection for testing",
    "version": "1.0.0"
  },
  "auth": {
    "type": "noauth"
  },
  "event": [
    {
      "listen": "prerequest",
      "script": {
        "exec": [
          "// Set base URL",
          "pm.globals.set('baseUrl', 'https://your-domain.com');"
        ]
      }
    }
  ],
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Register User",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/x-www-form-urlencoded"
              }
            ],
            "body": {
              "mode": "urlencoded",
              "urlencoded": [
                {
                  "key": "fullName",
                  "value": "Test User"
                },
                {
                  "key": "email",
                  "value": "test@example.com"
                },
                {
                  "key": "password",
                  "value": "password123"
                },
                {
                  "key": "confirmPassword",
                  "value": "password123"
                }
              ]
            },
            "url": {
              "raw": "{{baseUrl}}/createUser",
              "host": ["{{baseUrl}}"],
              "path": ["createUser"]
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/x-www-form-urlencoded"
              }
            ],
            "body": {
              "mode": "urlencoded",
              "urlencoded": [
                {
                  "key": "username",
                  "value": "test@example.com"
                },
                {
                  "key": "password",
                  "value": "password123"
                }
              ]
            },
            "url": {
              "raw": "{{baseUrl}}/login",
              "host": ["{{baseUrl}}"],
              "path": ["login"]
            }
          }
        }
      ]
    }
  ]
}
```

### **cURL Examples**

#### **Register User**
```bash
curl -X POST https://your-domain.com/createUser \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "fullName=Test User&email=test@example.com&password=password123&confirmPassword=password123"
```

#### **Login**
```bash
curl -X POST https://your-domain.com/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=test@example.com&password=password123" \
  -c cookies.txt
```

#### **Submit Application**
```bash
curl -X POST https://your-domain.com/user/submit-application \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -b cookies.txt \
  -d "dob=2003-05-15&gender=Male&phoneNo=9876543210&address=123 Main St&city=Mumbai&state=Maharashtra&pincode=400001&parentsName=Robert Doe&parentsPhoneNo=9876543211&class10PassingYear=2019&class10SchoolName=ABC School&class10Board=CBSE&class10Percentage=95.5&class12PassingYear=2021&class12SchoolName=XYZ School&class12Board=CBSE&class12Percentage=92.75&entranceExamName=JEE Main&entranceExamRollNo=JEE2021123456&entranceExamYear=2021&entranceExamRank=1250&course=B.Tech&branch1=Computer Science&branch2=Electronics"
```

#### **Health Check**
```bash
curl -X GET https://your-domain.com/actuator/health
```

---

## 📚 **SDK & Integration**

### **JavaScript SDK Example**
```javascript
class CollegePortalAPI {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
    this.sessionId = null;
  }

  async login(email, password) {
    const response = await fetch(`${this.baseUrl}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams({
        username: email,
        password: password
      }),
      credentials: 'include'
    });
    
    if (response.redirected) {
      return { success: true, redirectUrl: response.url };
    }
    return { success: false };
  }

  async submitApplication(applicationData) {
    const response = await fetch(`${this.baseUrl}/user/submit-application`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: new URLSearchParams(applicationData),
      credentials: 'include'
    });
    
    return response.ok;
  }

  async getAnnouncements(audience = 'ALL') {
    const response = await fetch(`${this.baseUrl}/api/announcements?audience=${audience}`, {
      credentials: 'include'
    });
    
    if (response.ok) {
      return await response.json();
    }
    throw new Error('Failed to fetch announcements');
  }
}

// Usage
const api = new CollegePortalAPI('https://your-domain.com');

// Login
await api.login('user@example.com', 'password123');

// Submit application
await api.submitApplication({
  dob: '2003-05-15',
  gender: 'Male',
  phoneNo: '9876543210',
  // ... other fields
});

// Get announcements
const announcements = await api.getAnnouncements('STUDENTS');
```

### **Python SDK Example**
```python
import requests
from typing import Dict, Any, Optional

class CollegePortalAPI:
    def __init__(self, base_url: str):
        self.base_url = base_url
        self.session = requests.Session()
    
    def login(self, email: str, password: str) -> bool:
        """Login to the portal"""
        response = self.session.post(
            f"{self.base_url}/login",
            data={
                'username': email,
                'password': password
            },
            allow_redirects=False
        )
        return response.status_code == 302
    
    def submit_application(self, application_data: Dict[str, Any]) -> bool:
        """Submit admission application"""
        response = self.session.post(
            f"{self.base_url}/user/submit-application",
            data=application_data
        )
        return response.status_code == 302
    
    def get_announcements(self, audience: str = 'ALL') -> Optional[Dict]:
        """Get announcements"""
        response = self.session.get(
            f"{self.base_url}/api/announcements",
            params={'audience': audience}
        )
        if response.status_code == 200:
            return response.json()
        return None
    
    def health_check(self) -> Dict:
        """Check application health"""
        response = self.session.get(f"{self.base_url}/actuator/health")
        return response.json()

# Usage
api = CollegePortalAPI('https://your-domain.com')

# Login
if api.login('user@example.com', 'password123'):
    print("Login successful")
    
    # Submit application
    application_data = {
        'dob': '2003-05-15',
        'gender': 'Male',
        'phoneNo': '9876543210',
        # ... other fields
    }
    
    if api.submit_application(application_data):
        print("Application submitted successfully")
    
    # Get announcements
    announcements = api.get_announcements('STUDENTS')
    print(f"Found {len(announcements['announcements'])} announcements")
```

---

## 📋 **API Changelog**

### **Version 1.0.0 (Current)**
- **Release Date**: February 4, 2025
- **Features**:
  - Complete authentication system (local + OAuth2)
  - User management APIs
  - Application submission and tracking
  - Payment management
  - Announcement system
  - Admin and teacher management APIs
  - Health check and monitoring endpoints

### **Planned Version 1.1.0**
- **Expected Release**: March 2025
- **Planned Features**:
  - REST API for mobile applications
  - Webhook support for real-time notifications
  - Bulk operations APIs
  - Advanced search and filtering
  - File upload APIs with cloud storage
  - SMS notification APIs
  - Advanced analytics APIs

---

## 📞 **API Support**

### **Support Channels**
- **Technical Support**: api-support@collegeportal.com
- **Documentation Issues**: docs@collegeportal.com
- **Bug Reports**: GitHub Issues
- **Feature Requests**: GitHub Discussions

### **SLA & Support Hours**
- **Response Time**: 24 hours for technical issues
- **Support Hours**: Monday-Friday, 9 AM - 6 PM IST
- **Emergency Support**: Available for production issues
- **Documentation Updates**: Weekly

### **Rate Limits & Quotas**
- **Free Tier**: 1000 requests/hour
- **Standard Tier**: 10,000 requests/hour
- **Premium Tier**: 100,000 requests/hour
- **Burst Limit**: 2x normal rate for 5 minutes

---

**📡 API Version**: 1.0.0  
**📅 Last Updated**: February 4, 2025  
**👨💻 API Team**: College Technical Team  
**📧 Support**: api-support@collegeportal.com  
**🌐 Base URL**: https://your-domain.com