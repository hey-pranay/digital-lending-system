# Digital Lending & Loan Management System

A mock full-stack web application developed as part of the **PMIS Internship at Capgemini**.

The Digital Lending & Loan Management System aims to simplify the loan application, approval, and repayment process. Borrowers can register, manage their profiles, apply for loans, view EMI schedules, and manage repayments. Administrators can review loan applications, approve or reject requests, and monitor repayment activities.

---

## Project Overview

| Detail | Description |
|---|---|
| Project Name | Digital Lending & Loan Management System |
| Project Type | Full-Stack Web Application |
| Internship | PMIS Internship – Capgemini |
| Difficulty | Moderate |
| Team Size | 5 Members |
| Development Duration | 2 Days |
| Backend | Java, Spring Boot |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Database | MySQL |
| Project Status | In Development |

## Technology Stack

### Backend
- Java 17+
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Spring Security
- JWT Authentication
- Maven
- Bean Validation

### Frontend
- Thymeleaf
- HTML5
- CSS3
- Bootstrap
- JavaScript

### Database
- MySQL 8+

### Development Tools
- IntelliJ IDEA
- Git & GitHub
- Postman
- Swagger / OpenAPI

---

## Core Features

### 1. Borrower Module

- User registration and login.
- Borrower profile management.
- Apply for loans by specifying the loan amount and tenure.
- Automatic EMI calculation.
- View submitted loan applications.
- Track loan application status.
- View monthly repayment schedules.
- Pay pending instalments.
- View repayment history.

### 2. Admin Module

- Admin authentication.
- View all loan applications.
- Filter applications based on loan status.
- Approve or reject loan applications.
- Add remarks while rejecting applications.
- Automatically generate EMI repayment schedules after loan approval.
- Monitor loan and repayment status.

### 3. Security

- JWT-based authentication.
- BCrypt password hashing.
- Role-based authorization.
- Protected endpoints.
- Separate access for ADMIN and BORROWER roles.

### 4. Validation & Exception Handling

- Validate loan amounts and tenure.
- Check borrower income eligibility.
- Prevent duplicate active loan applications.
- Handle invalid loan operations.
- Custom exceptions.
- Centralized global exception handling.

---

## Domain Model & Database Relationships

The application consists of four primary entities.

| Entity | Description |
|---|---|
| User | Stores authentication details, email, hashed password, and role |
| BorrowerProfile | Stores borrower details such as name, monthly income, and employment type |
| Loan | Stores loan amount, interest rate, tenure, EMI, and application status |
| Repayment | Stores individual EMI instalments, due dates, amounts, and payment status |

### Entity Relationships

| Relationship | Type |
|---|---|
| User → BorrowerProfile | One-to-One |
| BorrowerProfile → Loan | One-to-Many |
| Loan → Repayment | One-to-Many |

### Loan Status

- APPLIED
- APPROVED
- REJECTED
- CLOSED

### Repayment Status

- PENDING
- PAID

### User Roles

- ADMIN
- BORROWER

---

## Project Architecture

The backend follows a layered architecture:

**Controller → Service → Repository → Database**

DTOs are used to transfer request and response data separately from JPA entities.

### Project Structure

```text
digital-lending-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/sprint/lending/
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── LoanController.java
│   │   │       │   └── AdminController.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── LoanService.java
│   │   │       │   ├── RepaymentService.java
│   │   │       │   └── EmiCalculator.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── LoanRepository.java
│   │   │       │   └── RepaymentRepository.java
│   │   │       │
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   ├── BorrowerProfile.java
│   │   │       │   ├── Loan.java
│   │   │       │   └── Repayment.java
│   │   │       │
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       │
│   │   │       ├── security/
│   │   │       │   ├── JwtUtil.java
│   │   │       │   ├── JwtFilter.java
│   │   │       │   └── SecurityConfig.java
│   │   │       │
│   │   │       ├── exception/
│   │   │       │
│   │   │       ├── config/
│   │   │       │
│   │   │       └── LendingApplication.java
│   │   │
│   │   └── resources/
│   │       │
│   │       ├── templates/
│   │       │   ├── auth/
│   │       │   ├── borrower/
│   │       │   └── admin/
│   │       │
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
│   │       │
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── docs/
│   ├── API-DOCUMENTATION.md
│   └── DATABASE-DESIGN.md
│
├── .gitignore
├── pom.xml
└── README.md
```

*Note: This is the planned project structure. Additional classes and packages may be introduced as development progresses.*

---

## REST API Endpoints

### Authentication APIs

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Borrower APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/loans` | Apply for a loan |
| GET | `/api/loans` | View my loans |
| GET | `/api/loans/{id}/repayments` | View repayment schedule |
| PUT | `/api/repayments/{id}/pay` | Pay an instalment |

### Admin APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/loans?status=` | View and filter applications |
| PUT | `/api/admin/loans/{id}/approve` | Approve loan |
| PUT | `/api/admin/loans/{id}/reject` | Reject loan |

---

## Frontend Pages

The application uses Thymeleaf for server-side HTML rendering.

### Authentication
- Login page.
- Registration page.

### Borrower Dashboard
- Borrower profile.
- Apply for a loan.
- View loan applications.
- View EMI details.
- View repayment schedule.
- Manage instalment payments.

### Admin Dashboard
- View all loan applications.
- Filter loans by status.
- Approve or reject applications.
- View loan details and repayment information.

---

## EMI Calculation

The application calculates monthly EMI using the standard reducing-balance interest formula:

**EMI Formula:**

EMI = P × r × (1 + r)^n / ((1 + r)^n − 1)

Where:

- P = Principal loan amount.
- r = Monthly interest rate (annual interest rate divided by 12 and 100).
- n = Loan tenure in months.

---

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 17 or higher.
- Maven.
- MySQL 8 or higher.
- Git.
- IntelliJ IDEA or another Java IDE.

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/digital-lending-system.git
```

### 2. Navigate to the Project

```bash
cd digital-lending-system
```

### 3. Configure MySQL

Create the project database:

```sql
CREATE DATABASE digital_lending;
```

### 4. Configure Application Properties

Set the database configuration in `application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/digital_lending}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Configure the required environment variables before running the application.

Do not commit actual database credentials or JWT secrets to GitHub.

### 5. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or run `LendingApplication.java` directly from your IDE.

The application will be available at:

```text
http://localhost:8080
```

---

## Team Collaboration & Git Workflow

This project is developed collaboratively by a team of five members.

GitHub is used for source-code management, version control, collaboration, and integration.

### Branch Strategy

The `main` branch is the default branch and contains integrated project code.

Each team member works on a dedicated feature branch based on their assigned responsibility.

| Branch | Responsibility |
|---|---|
| `main` | Stable and integrated project code |
| `feature/admin-repayments` | Admin operations and repayment management |
| `feature/domain-model` | Entities, relationships, and repositories |
| `feature/auth-security` | Authentication and authorization |
| `feature/loan-application` | Borrower loan application and EMI calculation |
| `feature/frontend-testing` | Thymeleaf frontend and API testing |

### Contribution Workflow

1. Clone the GitHub repository.
2. Switch to your assigned feature branch.
3. Implement the assigned functionality.
4. Test your changes.
5. Commit changes with meaningful commit messages.
6. Push changes to your feature branch.
7. Create a Pull Request targeting `main`.
8. Get the changes reviewed before merging.

**Important Git Rules:**

- `main` is the default branch.
- Direct pushes to `main` are not permitted.
- Each member must work on their assigned feature branch.
- All changes must be submitted through Pull Requests.
- Meaningful commit messages must be used.
- Team members should synchronize their branches with the latest `main` regularly.

### Commit Message Convention

| Prefix | Purpose |
|---|---|
| `feat:` | New functionality |
| `fix:` | Bug fixes |
| `refactor:` | Code improvements |
| `test:` | Test cases |
| `docs:` | Documentation |
| `chore:` | Configuration and maintenance |

Examples:

```bash
git commit -m "feat: create borrower profile entity"

git commit -m "feat: implement JWT authentication"

git commit -m "feat: add loan application API"

git commit -m "fix: correct EMI calculation"

git commit -m "docs: update project README"
```

---

## Development Objectives

This project provides practical experience in:

- Java and Object-Oriented Programming.
- Java Collections and Streams.
- Exception Handling.
- Spring Boot and Dependency Injection.
- Spring MVC and REST API development.
- Spring Data JPA and Hibernate.
- MySQL database design.
- One-to-One and One-to-Many relationships.
- DTOs and layered architecture.
- Spring Security and JWT authentication.
- Bean Validation.
- Global Exception Handling.
- Thymeleaf and server-side rendering.
- Git, GitHub, and team collaboration.
- Postman API testing.
- Swagger / OpenAPI documentation.

---

## Project Deliverables

- Working Spring Boot backend.
- MySQL database integration.
- JPA entity relationships.
- JWT authentication and role-based authorization.
- Thymeleaf-based frontend.
- REST API endpoints.
- Swagger API documentation.
- Postman collection.
- Database design and ER diagram.
- README and setup documentation.
- End-to-end project demonstration.

---

## Acknowledgement

Developed as a collaborative mock project under the **PMIS Internship at Capgemini**, with the objective of applying theoretical knowledge to a practical software development scenario.

---

**Disclaimer:** This is an educational mock implementation intended for learning and demonstration purposes. It is not designed for actual financial transactions or real-world lending.
