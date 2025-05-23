# 📚 Student Management Application

This project handles student operations including email verification, OTP login, promotional emailing with images, and bulk emailing.

Port: `8081`

---

## 🛠️ Technologies Used

- Java
- Spring Boot
- Thymeleaf
- JavaMailSender (for emails)
- SLF4J (logging)

---

## 🏗️ Features and API Endpoints

| Feature | API Endpoint | Method | Description |
|:--------|:-------------|:-------|:------------|
| Show registration form | `/student/form` | GET | Display student registration form |
| Create new student | `/student/create` | POST | Save student data |
| Show student details | `/student/id/{id}` | GET | Fetch student by ID |
| Send MIME promotional email | `/student/mime-email/{id}` | GET | Send email with HTML and image |
| Send email with PDF attachment | `/student/pdf-email/{id}` | GET | Send email with a PDF attached |
| Show bulk email form | `/student/bulk-email-form` | GET | Form to input multiple student IDs |
| Send bulk emails | `/student/bulk-email` | POST | Send emails to multiple students |
| Send verification email | `/student/send-verification/{id}` | GET | Send email with verification link |
| Verify token from email | `/student/verify?token=xxxx` | GET | Verify token and activate account |
| Send OTP email | `/student/otp-id/{id}` | GET | Send OTP to student's email |
| Show OTP input form | `/student/verify-otp/{id}` | GET | Enter OTP form |
| Verify OTP | `/student/verify` | POST | Validate OTP entered by student |

---

## 🗄️ Thymeleaf Templates

- `student-form.html` — Registration form
- `student-show.html` — Student details
- `bulk-email-form.html` — Bulk email entry
- `bulk-email-result.html` — Result after bulk email
- `promotional-email-template.html` — Promotional email template
- `student-pdf-email-template.html` — PDF email template
- `email-verification-sent.html` — After sending verification email
- `verification-result.html` — Result after verifying token
- `otp-form.html` — OTP input form
- `verification-success.html` — Success page after OTP verification

---

## 🔄 Application Flow

### 1. Student Registration

- User visits `http://localhost:8081/student/form`
- Fills the form and submits ➔ `POST /student/create`
- Student data is saved and redirected back to the form page.

---

### 2. View Student Details

- Visit `http://localhost:8081/student/id/{id}`
- Student details are shown.

---

### 3. Send Promotional Email (MIME)

- Admin/user triggers `GET /student/mime-email/{id}`.
- System sends an HTML-based email with images to the student's email address.

---

### 4. Send PDF Attachment Email

- Admin/user triggers `GET /student/pdf-email/{id}`.
- Email with PDF attachment is sent.

---

### 5. Bulk Email Sending

- Admin visits `http://localhost:8081/student/bulk-email-form`.
- Enters comma-separated student IDs (e.g., `1,2,3`).
- Form submits to `POST /student/bulk-email`, emails are sent in bulk.

---

### 6. Email Verification (Link-Based)

- Admin sends verification email via `GET /student/send-verification/{id}`.
- Student receives an email with a verification link: `http://localhost:8081/student/verify?token=xxxx`
- Student clicks the link ➔ token is validated ➔ account is verified.

---

### 7. OTP Verification Flow

- Admin sends OTP via `GET /student/otp-id/{id}`.
- Student receives OTP on email.
- Student visits `http://localhost:8081/student/verify-otp/{id}` to input OTP.
- Student submits OTP ➔ verified via `POST /student/verify`.

---

## 📧 How to Set up Gmail SMTP Access Token (For Sending Emails)

Since Gmail **no longer allows** less secure apps with username/password, you must use an **App Password** if you have 2FA enabled, or create an **OAuth2 access token**.

Here’s how to easily generate an SMTP access token for Gmail:

### Step 1: Enable 2-Step Verification

- Go to [Google Account Security Settings](https://myaccount.google.com/security)
- Under **"Signing in to Google"**, turn **2-Step Verification** **ON**.

### Step 2: Create an App Password

- After enabling 2FA, in the same Security settings page:
  - Click **"App passwords"** (it appears after 2FA is ON).
  - Select **"Mail"** as the app.
  - Select **"Other (Custom Name)"** → Name it, e.g., `StudentAppSMTP`.
  - Click **Generate**.

- **Copy the 16-digit App Password** generated by Google.

⚡ **Important:**  
This app password is **your SMTP password** for Spring Boot.  
Use it instead of your Gmail password!

---

### Example SMTP Configuration (`application.properties`)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-digit-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
```

✅ Now your Spring Boot app will be able to send emails using Gmail SMTP securely!

---

## 📦 Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

App will start at:  
**http://localhost:8081**

---

