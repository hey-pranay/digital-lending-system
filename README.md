# Digital Lending & Loan Management System

A mock full-stack web application developed as part of the **PMIS Internship at Capgemini**.

The Digital Lending & Loan Management System aims to simplify the loan application, approval, and repayment process. Borrowers can register, manage their profiles, apply for loans, view EMI schedules, and manage repayments. Administrators can review loan applications, approve or reject requests, and monitor repayment activities.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
    - [Backend](#backend)
    - [Frontend](#frontend)
    - [Database](#database)
    - [Development Tools](#development-tools)
3. [Core Features](#core-features)
    - [Borrower Module](#1-borrower-module)
    - [Admin Module](#2-admin-module)
    - [Security](#3-security)
    - [Validation & Exception Handling](#4-validation--exception-handling)
4. [Domain Model & Database Relationships](#domain-model--database-relationships)
    - [Entity Relationships](#entity-relationships)
    - [Loan Status](#loan-status)
    - [Repayment Status](#repayment-status)
    - [User Roles](#user-roles)
5. [Project Architecture](#project-architecture)
    - [Project Structure](#project-structure)
6. [REST API Endpoints](#rest-api-endpoints)
    - [Authentication APIs](#authentication-apis)
    - [Borrower APIs](#borrower-apis)
    - [Admin APIs](#admin-apis)
7. [Frontend Pages](#frontend-pages)
8. [EMI Calculation Formula](#emi-calculation-formula)
9. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Clone the Repository](#1-clone-the-repository)
    - [Navigate to the Project](#2-navigate-to-the-project)
    - [Configure MySQL](#3-configure-mysql)
    - [Configure Application Properties](#4-configure-application-properties)
    - [Run the Application](#5-run-the-application)
10. [Team Collaboration & Git Workflow](#team-collaboration--git-workflow)
    - [Team Responsibilities & Branch Allocation](#1-team-responsibilities--branch-allocation)
    - [Branch Strategy](#2-branch-strategy)
    - [Development Workflow](#3-development-workflow)
    - [Commit Message Convention](#4-commit-message-convention)
    - [Pull Request Guidelines](#5-pull-request-guidelines)
11. [Development Objectives](#development-objectives)
12. [Project Deliverables](#project-deliverables)
13. [Acknowledgement](#acknowledgement)

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
| Backend | Java 21, Spring Boot |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Database | MySQL |
| Project Status | In Development |

## Technology Stack

### Backend
- Java 21
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
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       ├── dto/
│   │   │       │   ├── request/
│   │   │       │   └── response/
│   │   │       ├── security/
│   │   │       ├── exception/
│   │   │       ├── config/
│   │   │       └── LendingApplication.java
│   │   │
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── auth/
│   │       │   ├── borrower/
│   │       │   └── admin/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── images/
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

## EMI Calculation Formula

The Equated Monthly Instalment (EMI) is calculated using the following formula:

$$
EMI = \frac{P \times r \times (1+r)^n}{(1+r)^n-1}
$$

**Where:**
- `P` = Principal loan amount
- `r` = Monthly interest rate (Annual interest rate / 12 / 100)
- `n` = Loan tenure in months
- `EMI` = Equated Monthly Instalment

---

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 21.
- Maven.
- MySQL 8 or higher.
- Git.
- IntelliJ IDEA or another Java IDE.

### 1. Clone the Repository

```bash
git clone https://github.com/hey-pranay/digital-lending-system.git
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

On Windows, you can also use:

```bash
mvnw.cmd spring-boot:run
```

Or run `LendingApplication.java` directly from your IDE.

The application will be available at:

```text
http://localhost:8080
```

---

# Team Collaboration & Git Workflow

This project is developed collaboratively by a team of five members.

GitHub is used for source-code management, version control, collaboration, code reviews, and integration.

## 1. Team Responsibilities & Branch Allocation

Each team member is responsible for implementing and maintaining their assigned feature.

| Member | Feature / Responsibility | Branch |
|---|---|---|
| Member 1 – Team Lead | Thymeleaf Frontend, GitHub Management & Integration | `feature/thymeleaf-frontend` |
| Member 2 | Authentication & Security | `feature/auth-security` |
| Member 3 | Domain Model & Database | `feature/domain-model` |
| Member 4 | Borrower Loan Application & EMI | `feature/loan-application` |
| Member 5 | Admin Loan Management & Repayments | `feature/admin-repayment-management` |

### Member 1 – Thymeleaf Frontend & Team Lead
- Develop login and registration pages.
- Develop borrower and admin dashboards.
- Develop loan application forms.
- Display loan details and repayment schedules.
- Integrate Thymeleaf pages with backend functionality.
- Manage GitHub repository and branch coordination.
- Review Pull Requests and coordinate integration.

### Member 2 – Authentication & Security
- Implement borrower registration and login.
- Implement BCrypt password hashing.
- Generate and validate JWT tokens.
- Configure Spring Security.
- Implement role-based authorization.
- Secure application endpoints.

### Member 3 – Domain Model & Database
- Create User, BorrowerProfile, Loan, and Repayment entities.
- Implement JPA entity relationships.
- Create Spring Data JPA repositories.
- Configure MySQL database integration.
- Prepare database initialization and sample data.

### Member 4 – Borrower Loan Application & EMI
- Implement loan application functionality.
- Validate loan amount and tenure.
- Implement income eligibility checks.
- Calculate monthly EMI.
- Prevent duplicate active loan applications.
- Allow borrowers to view their loan applications.

### Member 5 – Admin Loan Management & Repayments
- Implement admin loan application listing.
- Filter applications by status.
- Implement loan approval and rejection.
- Add rejection remarks.
- Generate repayment schedules after approval.
- Implement repayment viewing and payment status updates.

**Team coordination:** Entities, DTOs, API contracts, shared configurations, and database changes must be discussed and agreed upon by the relevant members before implementation to avoid integration conflicts.

## 2. Branch Strategy

We follow a feature-based branching strategy.

```text
main
│
├── feature/thymeleaf-frontend
├── feature/auth-security
├── feature/domain-model
├── feature/loan-application
└── feature/admin-repayment-management
```

### Branch Naming Convention

Use meaningful branch names with the following prefixes:

| Prefix | Purpose | Example |
|---|---|---|
| `main` | Main integration branch | `main` |
| `feature/` | New application functionality | `feature/loan-application` |
| `fix/` | Bug fixes | `fix/emi-calculation` |
| `docs/` | Documentation changes | `docs/update-readme` |
| `refactor/` | Code restructuring | `refactor/loan-service` |
| `test/` | Testing-related changes | `test/loan-service` |

## 3. Development Workflow

Every team member should follow this workflow:

**Step 1: Clone the repository**

```bash
git clone https://github.com/hey-pranay/digital-lending-system.git

cd digital-lending-system
```

**Step 2: Switch to your assigned branch**

Example for Member 4:

```bash
git switch feature/loan-application
```

**Step 3: Get the latest changes from your branch**

```bash
git pull origin feature/loan-application
```

**Step 4: Implement and test your assigned feature.**

**Step 5: Stage your changes**

```bash
git add .
```

**Step 6: Commit your changes**

```bash
git commit -m "feat(loan): implement loan application"
```

**Step 7: Push to your assigned branch**

```bash
git push origin feature/loan-application
```

**Step 8: Create a Pull Request**

- Open the repository on GitHub.
- Select Pull Requests.
- Click New Pull Request.
- Select your feature branch as the source branch.
- Select `main` as the target branch.
- Add a meaningful title and description.
- Submit the Pull Request.

**Step 9: Code Review and Merge**

The team lead reviews the changes, requests corrections if needed, and merges approved Pull Requests into `main`.

### Important Git Rules

- `main` is the main integration branch.
- Do not work directly on `main`.
- Push your work to your assigned feature branch.
- Submit changes through Pull Requests.
- Do not merge your own Pull Request without the agreed review.
- Keep commits focused and meaningful.
- Test changes before pushing.
- Coordinate changes to shared entities, DTOs, and configurations.
- Never commit passwords, database credentials, JWT secrets, or other sensitive information.
- Synchronize your feature branch with the latest `main` when required.

*Note: These are the team's contribution rules. Technical restrictions on direct pushes to `main` depend on the repository's active GitHub branch protection settings.*

## 4. Commit Message Convention

We follow a simplified version of **Conventional Commits** to maintain consistent and meaningful commit history.

### Commit Message Format

```text
type(scope): short description
```

- `type`: Category of the change.
- `scope`: Optional module or area affected.
- `short description`: A brief explanation of the change.

### Commit Types Explained

| Prefix | Meaning | When to Use |
|---|---|---|
| `feat` | Feature | Adding new functionality to the application |
| `fix` | Fix | Correcting a bug or unexpected behavior |
| `docs` | Documentation | Updating README, API documentation, or other documents |
| `chore` | Maintenance | Project setup, configuration, dependencies, or routine maintenance |
| `refactor` | Refactoring | Improving code structure without changing existing functionality |
| `test` | Testing | Adding or updating unit and integration tests |
| `style` | Code Style | Formatting, indentation, and whitespace changes without changing logic |
| `perf` | Performance | Improving application performance |
| `build` | Build System | Updating Maven configuration or build dependencies |
| `ci` | Continuous Integration | Updating CI/CD configuration and GitHub Actions |
| `revert` | Revert | Reversing an earlier commit |

### Understanding the Four Most Important Types

**1. `feat` – New Feature**

Use `feat` when adding something new to the application.

Examples:

```bash
git commit -m "feat(auth): implement borrower registration"

git commit -m "feat(loan): add loan application API"

git commit -m "feat(ui): create borrower dashboard"
```

**2. `fix` – Bug Fix**

Use `fix` when correcting something that is not working as expected.

Examples:

```bash
git commit -m "fix(loan): correct EMI calculation"

git commit -m "fix(auth): resolve invalid JWT validation"

git commit -m "fix(ui): correct login form validation"
```

**3. `docs` – Documentation**

Use `docs` when updating project documentation without changing application functionality.

Examples:

```bash
git commit -m "docs(readme): update project overview"

git commit -m "docs(api): add loan endpoint documentation"

git commit -m "docs(git): add contribution guidelines"
```

**4. `chore` – Project Maintenance**

Use `chore` for routine project maintenance that does not introduce a feature, fix a bug, or primarily change documentation.

Examples:

```bash
git commit -m "chore: initialize Spring Boot project"

git commit -m "chore(git): add gitignore configuration"

git commit -m "chore(config): configure application properties"
```

### More Commit Message Examples

```bash
# Refactoring
git commit -m "refactor(loan): simplify loan service logic"

# Testing
git commit -m "test(auth): add registration service tests"

# Formatting
git commit -m "style(controller): format controller methods"

# Performance
git commit -m "perf(loan): optimize loan retrieval query"

# Build configuration
git commit -m "build(maven): update project dependencies"

# Continuous integration
git commit -m "ci(github): configure GitHub Actions workflow"
```

### Commit Message Best Practices

- Always use lowercase commit types.
- Write short, meaningful descriptions.
- Use imperative verbs such as `add`, `update`, `implement`, and `fix`.
- Keep commits focused on one logical change.
- Avoid vague messages such as `changes`, `updated code`, `final commit`, or `new changes`.
- Add a scope when it helps identify the affected module.

## 5. Pull Request Guidelines

Every Pull Request should contain:

- A descriptive title.
- A summary of the implemented changes.
- A list of important modifications.
- Testing details.
- Any known issues or integration dependencies.

### Example Pull Request Title

```text
feat(loan): implement borrower loan application
```

### Example Pull Request Description

```markdown
## Description
Implemented borrower loan application functionality.

## Changes
- Added loan application endpoint.
- Added loan validation.
- Implemented EMI calculation.
- Added income eligibility checks.

## Testing
- Verified valid loan applications.
- Tested invalid loan amounts.
- Tested tenure validation.

## Related Branch
feature/loan-application
```

All Pull Requests should target `main` and be reviewed before merging.

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