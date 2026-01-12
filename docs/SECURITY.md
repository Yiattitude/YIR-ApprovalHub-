# Security Guide

## Important Security Notes

### ⚠️ Development vs Production

This system includes default configurations suitable for **development and testing only**. Before deploying to production, you **MUST** address the following security considerations:

---

## Critical Security Checklist

### 1. Database Credentials

**Development (Current)**:
```properties
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

**Production (Required)**:
```bash
# NEVER use default values in production
# Set environment variables explicitly
export DB_USERNAME=secure_username
export DB_PASSWORD=strong_random_password_here

# Start application
java -jar app.jar --spring.profiles.active=prod
```

⚠️ **Action Required**: Remove default fallback values in production profile.

---

### 2. JWT Secret Key

**Development (Current)**:
- Uses a long but still hardcoded default secret
- Marked as "DevelopmentOnlyNotForProduction"

**Production (Required)**:
```bash
# Generate a secure random secret (minimum 64 characters)
export JWT_SECRET=$(openssl rand -base64 64)

# Or manually create a strong random string
export JWT_SECRET="your-very-long-random-secret-string-minimum-64-characters-recommended-128-or-more"
```

⚠️ **Action Required**: 
- In production-prod.properties, remove the default value
- Require JWT_SECRET to be set via environment variable
- Application should fail to start if JWT_SECRET is not provided

---

### 3. Test Accounts

**Development (Current)**:
The system auto-creates test accounts with weak passwords:
- admin / admin123
- manager1 / manager123
- hrmanager / hr123
- employee1 / emp123
- employee2 / emp123

**Production (Required)**:
```bash
# Option 1: Disable auto-initialization in production
spring.profiles.active=prod
# In prod profile: Do not run DataInitializer

# Option 2: Change all passwords immediately after deployment
# Option 3: Delete test accounts and create real accounts
```

⚠️ **Action Required**: 
- Disable DataInitializer in production
- Create real user accounts with strong passwords
- Delete or disable all test accounts

---

### 4. Frontend API URL

**Development (Current)**:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

**Production (Required)**:
```javascript
// Use environment variable
const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || '/api';
```

Create `.env.production` file:
```
VUE_APP_API_BASE_URL=https://your-domain.com/api
```

⚠️ **Action Required**: Configure API URL via environment variables

---

### 5. SQL Logging

**Development (Current)**:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Production (Required)**:
```properties
# In application-prod.properties
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

⚠️ **Action Required**: SQL logging is already disabled in production profile

---

### 6. HTTPS/SSL

**Production (Required)**:
```properties
# Force HTTPS
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
```

⚠️ **Action Required**: 
- Obtain SSL certificate
- Configure HTTPS
- Redirect HTTP to HTTPS

---

### 7. CORS Configuration

**Development (Current)**:
```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000", 
    "http://localhost:8081"
));
```

**Production (Required)**:
```java
// Only allow your production domain
configuration.setAllowedOrigins(Arrays.asList(
    "https://your-domain.com"
));
```

⚠️ **Action Required**: Update CORS to allow only production domains

---

## Production Deployment Checklist

Before deploying to production, ensure:

- [ ] Database credentials set via environment variables (no defaults)
- [ ] JWT secret generated and set (minimum 64 characters)
- [ ] Test accounts disabled or deleted
- [ ] All default passwords changed
- [ ] HTTPS/SSL configured and enforced
- [ ] CORS restricted to production domains
- [ ] SQL logging disabled
- [ ] Environment variables documented
- [ ] Frontend API URL configured
- [ ] Firewall rules configured
- [ ] Database backups configured
- [ ] Log rotation configured
- [ ] Monitoring and alerting set up

---

## Secure Configuration Example

### Production Environment Variables

Create a secure configuration file (e.g., `/etc/approval-hub/config.env`):

```bash
#!/bin/bash
# Production Configuration
# Store this file securely with restricted permissions (chmod 600)

# Database
export DB_URL="jdbc:mysql://db-host:3306/approval_hub?useSSL=true&requireSSL=true"
export DB_USERNAME="appuser"
export DB_PASSWORD="$(cat /secure/db-password.txt)"

# JWT
export JWT_SECRET="$(cat /secure/jwt-secret.txt)"
export JWT_EXPIRATION="86400000"

# Upload
export UPLOAD_DIR="/var/appdata/approval-hub/uploads"

# Logging
export LOG_FILE="/var/log/approval-hub/application.log"

# SSL
export SSL_KEYSTORE_PASSWORD="$(cat /secure/ssl-password.txt)"
```

### Starting the Application

```bash
# Load environment variables
source /etc/approval-hub/config.env

# Start application with production profile
java -jar /opt/approval-hub/approval-hub.jar \
  --spring.profiles.active=prod \
  --server.port=8443
```

---

## Password Security

### User Passwords

All user passwords are hashed using BCrypt with a work factor of 10. This is secure, but for enhanced security:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    // Increase work factor for stronger hashing (slower but more secure)
    return new BCryptPasswordEncoder(12);
}
```

### Strong Password Policy

Implement password validation:

```java
public boolean isStrongPassword(String password) {
    // At least 12 characters
    // Contains uppercase, lowercase, digit, special char
    return password.length() >= 12 &&
           password.matches(".*[A-Z].*") &&
           password.matches(".*[a-z].*") &&
           password.matches(".*\\d.*") &&
           password.matches(".*[!@#$%^&*].*");
}
```

---

## Database Security

### Connection Pooling

Configure HikariCP for production:

```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### Database User Privileges

Create a dedicated database user with minimal privileges:

```sql
CREATE USER 'appuser'@'app-server-ip' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON approval_hub.* TO 'appuser'@'app-server-ip';
FLUSH PRIVILEGES;
```

---

## Monitoring and Alerts

### Security Monitoring

Monitor for:
- Failed login attempts
- Suspicious API access patterns
- Unusual database queries
- Large file uploads
- Privilege escalation attempts

### Log Analysis

Implement log analysis:
- Parse authentication logs
- Track approval patterns
- Monitor error rates
- Detect anomalies

---

## Incident Response

### In Case of Security Breach

1. **Immediately**:
   - Rotate all secrets (JWT, database passwords)
   - Invalidate all active sessions
   - Lock affected accounts

2. **Within 1 hour**:
   - Analyze logs to determine breach extent
   - Identify compromised data
   - Patch vulnerabilities

3. **Within 24 hours**:
   - Notify affected users
   - Document incident
   - Implement additional security measures

---

## Regular Security Maintenance

### Dependency Updates

**Critical**: Always keep dependencies up to date with security patches.

**MySQL Connector**:
- ✅ Using `mysql-connector-j` version 8.2.0 (patched)
- ⚠️ Previous `mysql-connector-java` 8.0.33 had takeover vulnerability
- 🔄 Check for updates regularly: https://dev.mysql.com/downloads/connector/j/

**How to check for vulnerabilities**:
```bash
# Maven dependency check
mvn dependency-check:check

# Or use GitHub Dependabot
# Or OWASP Dependency-Check
```

### Monthly Tasks
- [ ] **Update dependencies for security patches**
- [ ] Review user accounts and permissions
- [ ] Review access logs for anomalies
- [ ] Test backup restoration
- [ ] Review and update firewall rules

### Quarterly Tasks
- [ ] Security audit
- [ ] Penetration testing
- [ ] Update security documentation
- [ ] Review and update incident response plan

---

## Contact

For security issues, please contact:
- Security Team: security@yir.com
- Emergency: Create a private security advisory on GitHub

**Do not disclose security vulnerabilities publicly.**

---

## Compliance Notes

This system stores:
- User personal information (names, emails, phone numbers)
- Employment information (departments, positions)
- Leave and reimbursement data

Ensure compliance with:
- Data protection regulations (GDPR, CCPA, etc.)
- Company data retention policies
- Audit requirements

---

**Last Updated**: 2024-01-12  
**Review Required**: Before production deployment
