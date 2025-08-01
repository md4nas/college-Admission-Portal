# 📖 UserAuth Help & User Guide

## 🚀 Getting Started

### First Time Setup
1. **Access the Application:** Navigate to `http://localhost:8080`
2. **Create Account:** Click "Register" to create a new account
3. **Verify Email:** Check your email for verification link
4. **Login:** Use your credentials to access the system

### System Requirements
- **Browser:** Chrome 90+, Firefox 88+, Safari 14+, Edge 90+
- **JavaScript:** Must be enabled
- **Cookies:** Must be enabled for session management
- **Email:** Valid email address required for registration

## 👤 User Registration & Login

### Creating an Account
1. **Navigate to Registration:** Click "Register" on the home page
2. **Fill Form:**
   - **Full Name:** Your complete name
   - **Email:** Valid email address (will be used for login)
   - **Password:** Minimum 6 characters
   - **Confirm Password:** Must match the password
3. **Submit:** Click "Register" button
4. **Email Verification:** Check your email and click verification link
5. **Account Activated:** You can now login

### Login Process
1. **Access Login:** Click "Sign In" on the home page
2. **Enter Credentials:**
   - **Email:** Your registered email address
   - **Password:** Your account password
3. **Login Options:**
   - **Local Login:** Use email and password
   - **Google OAuth:** Click "Login with Google"
   - **GitHub OAuth:** Click "Login with GitHub"
4. **Dashboard Access:** Redirected based on your role

### Forgot Password
1. **Access Recovery:** Click "Forgot Password" on login page
2. **Enter Email:** Provide your registered email address
3. **Check Email:** You'll receive a 6-digit OTP
4. **Enter OTP:** Input the OTP within 10 minutes
5. **New Password:** Set your new password
6. **Login:** Use new password to access account

## 🏠 Dashboard Overview

### User Dashboard (ROLE_USER)
- **Personal Profile:** View and edit your information
- **Course Progress:** Track your learning progress
- **Settings:** Change password and preferences
- **Navigation:** Blue navbar with user-specific options

### Teacher Dashboard (ROLE_TEACHER)
- **Student Management:** View and manage student data
- **Course Oversight:** Manage courses and assignments
- **Student Progress:** Monitor student performance
- **Navigation:** Green navbar with teacher-specific options

### Admin Dashboard (ROLE_ADMIN)
- **User Management:** Complete user administration
- **System Reports:** View system statistics and reports
- **Role Management:** Assign and modify user roles
- **System Settings:** Configure system parameters
- **Navigation:** Red navbar with admin-specific options

## ⚙️ Account Settings

### Changing Password
1. **Access Settings:** Click your name in the navbar
2. **Select Change Password:** From the dropdown menu
3. **Enter Details:**
   - **Current Password:** Your existing password
   - **New Password:** Your desired new password
4. **Submit:** Click "Update Password"
5. **Confirmation:** Success message will appear

### Profile Management
- **View Profile:** Access from user dashboard
- **Update Information:** Modify personal details
- **Account Status:** Check verification status
- **Login History:** View recent login activities

## 🔐 Security Features

### Two-Factor Authentication
- **OAuth2 Integration:** Login with Google or GitHub
- **Email Verification:** Required for account activation
- **Session Security:** Automatic logout after inactivity
- **Password Security:** Encrypted password storage

### Account Security
- **Strong Passwords:** Use complex passwords
- **Regular Updates:** Change passwords periodically
- **Secure Logout:** Always logout when finished
- **Monitor Activity:** Check for suspicious activities

## 🎯 Role-Specific Features

### For Students (ROLE_USER)
- **Course Access:** View enrolled courses
- **Progress Tracking:** Monitor learning progress
- **Profile Management:** Update personal information
- **Support Access:** Contact support for help

### For Teachers (ROLE_TEACHER)
- **Student Data:** Access student information
- **Course Management:** Create and manage courses
- **Grade Management:** Assign and track grades
- **Reports:** Generate student progress reports

### For Administrators (ROLE_ADMIN)
- **User Management:** Create, edit, delete users
- **Role Assignment:** Assign roles to users
- **System Monitoring:** Monitor system health
- **Configuration:** System-wide settings management

## 🔧 Troubleshooting

### Common Issues

#### Login Problems
**Issue:** Cannot login with correct credentials
**Solutions:**
1. Check if account is verified (check email)
2. Ensure password is correct (case-sensitive)
3. Try password reset if needed
4. Clear browser cache and cookies
5. Contact administrator if issue persists

#### Email Not Received
**Issue:** Verification or reset email not received
**Solutions:**
1. Check spam/junk folder
2. Verify email address is correct
3. Wait 5-10 minutes for delivery
4. Request new verification email
5. Contact support if still not received

#### Access Denied (403 Error)
**Issue:** Cannot access certain pages
**Solutions:**
1. Verify you have correct role permissions
2. Login again to refresh session
3. Contact administrator for role verification
4. Check if account is active and verified

#### Page Not Loading
**Issue:** Application pages not loading properly
**Solutions:**
1. Refresh the page (F5 or Ctrl+R)
2. Clear browser cache and cookies
3. Try different browser
4. Check internet connection
5. Contact technical support

### Browser Compatibility
- **Chrome:** Fully supported (recommended)
- **Firefox:** Fully supported
- **Safari:** Fully supported
- **Edge:** Fully supported
- **Internet Explorer:** Not supported

### Performance Tips
1. **Use Latest Browser:** Keep browser updated
2. **Clear Cache:** Regularly clear browser cache
3. **Stable Connection:** Ensure stable internet connection
4. **Close Unused Tabs:** Reduce browser memory usage

## 📧 Email Features

### Email Verification
- **Purpose:** Confirms email address ownership
- **Process:** Click link in verification email
- **Expiry:** Verification links expire after 24 hours
- **Resend:** Request new verification if expired

### Password Reset Emails
- **OTP Delivery:** 6-digit code sent to email
- **Validity:** OTP valid for 10 minutes only
- **Security:** Never share OTP with others
- **Multiple Attempts:** Limited attempts for security

### Email Templates
- **Professional Design:** HTML formatted emails
- **Clear Instructions:** Step-by-step guidance
- **Security Information:** Important security notices
- **Contact Information:** Support contact details

## 🆘 Getting Help

### Self-Service Options
1. **FAQ Section:** Check frequently asked questions
2. **User Guide:** This comprehensive help document
3. **Video Tutorials:** Step-by-step video guides
4. **Knowledge Base:** Searchable help articles

### Contact Support
- **Email Support:** support@userauth.com
- **Response Time:** 24-48 hours
- **Business Hours:** Monday-Friday, 9 AM - 5 PM
- **Emergency Issues:** Critical security issues prioritized

### Reporting Issues
When reporting issues, please include:
1. **Error Message:** Exact error text
2. **Steps to Reproduce:** What you were doing
3. **Browser Information:** Browser type and version
4. **Account Details:** Your email (never share password)
5. **Screenshots:** Visual evidence if applicable

## 📚 Additional Resources

### Documentation
- **API Documentation:** For developers
- **Security Guide:** Security best practices
- **Deployment Guide:** Installation instructions
- **Admin Manual:** Administrator guidelines

### Training Materials
- **User Training:** Basic system usage
- **Admin Training:** Administrative functions
- **Security Training:** Security awareness
- **Best Practices:** Recommended usage patterns

### Community
- **User Forum:** Community discussions
- **Feature Requests:** Suggest improvements
- **Bug Reports:** Report system issues
- **Knowledge Sharing:** Share tips and tricks

## 🔄 System Updates

### Update Notifications
- **Email Alerts:** Important updates via email
- **System Messages:** In-app notifications
- **Maintenance Windows:** Scheduled downtime notices
- **Feature Announcements:** New feature releases

### Version Information
- **Current Version:** Check in footer
- **Release Notes:** Available in documentation
- **Update Schedule:** Regular monthly updates
- **Security Patches:** Applied as needed

## 📞 Emergency Procedures

### Account Compromise
If you suspect your account is compromised:
1. **Change Password:** Immediately change password
2. **Check Activity:** Review recent login history
3. **Contact Support:** Report the incident
4. **Monitor Account:** Watch for suspicious activity

### System Outage
During system outages:
1. **Check Status:** Visit status page
2. **Wait for Resolution:** Most issues resolve quickly
3. **Contact Support:** If outage persists
4. **Follow Updates:** Monitor communication channels

### Data Loss
If you experience data loss:
1. **Don't Panic:** Data is regularly backed up
2. **Contact Support:** Report the issue immediately
3. **Provide Details:** Explain what data is missing
4. **Recovery Process:** Support will initiate recovery