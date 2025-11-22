# 🚀 EcommerceHub – Enterprise Multi-Vendor E-Commerce Platform

A production-grade full-stack multi-vendor marketplace built using **Spring Boot**, **PostgreSQL**, **React**, **Redux**, and **JWT Authentication**.

This platform supports **Customer**, **Seller**, and **Admin** portals with real-time inventory tracking, product & category management, seller dashboards, secure authentication, and clean layered architecture.

---

## 📑 Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Running the Backend](#running-the-backend)
- [API Security](#api-security)
- [Future Enhancements](#future-enhancements)
- [Contact](#contact)

---

## 🌟 Features

### 🛒 Customer Features
- Browse products with filters & pagination  
- Add/remove items from cart  
- Place orders  
- View order history  
- Manage personal profile  

### 🛍️ Seller Features
- Add/update/delete products  
- Manage stock & inventory  
- View sales analytics  
- Seller dashboard  

### 🛠️ Admin Features
- Manage all users  
- Manage categories  
- Manage products  
- View platform-wide sales & performance  

### 🔐 Authentication & Security
- JWT-based secure login/signup  
- Role-based access (ADMIN / SELLER / USER)  
- Spring Security filter chain  
- HTTP-Only authentication cookies  

### 📦 Other Features
- Image/file upload support  
- Centralized exception handling  
- Configurable using environment variables  
- AWS-ready backend deployment  

---

## 🧰 Tech Stack

### **Backend**
- Java 21  
- Spring Boot  
- Spring Security + JWT  
- Spring Data JPA  
- Hibernate  
- PostgreSQL  
- Maven  
- Lombok  

### **Frontend (in progress)**
- React.js  
- Redux Toolkit  
- TailwindCSS (optional)  

### **Deployment**
- AWS EC2 / RDS  
- Nginx (optional)  

---

## 🏗 Architecture

Client (React)
|
| REST API (JSON)
v
Spring Boot (Controller → Service → Repository)
|
v
PostgreSQL Database

yaml
Copy code

### ✔ Clean Layered Architecture
- OOP + SOLID principles  
- DTO + Service + Repository pattern  
- Authentication filter chain  
- Centralized exception handling  

---

## 📁 Project Structure (Backend)

Ecommerce-Multivendor/
│── src/
│ ├── main/
│ │ ├── java/com/project/ecommerce
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ ├── model/
│ │ │ ├── security/
│ │ │ └── EcommerceApplication.java
│ │ └── resources/
│ │ ├── application.properties
│ │ ├── static/
│ │ └── templates/
│── pom.xml
│── .gitignore
│── .gitattributes
│── README.md

---

## 🔐 Environment Variables

Create a `.env` file in the project root:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ecommerce
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_db_password

SPRING_APP_JWTSECRET=your_jwt_secret_key
SPRING_APP_JWTEXPIRATION_MS=3000000
SPRING_APP_JWTCOOKIE_NAME=springBootEcom

SERVER_PORT=5000
SPRING_JPA_HBM2DDL=update
SPRING_JPA_SHOW_SQL=true
```

⚠️ Do NOT commit .env to GitHub.

```
 ▶️ Running the Backend
Using Maven
bash
Copy code
mvn clean install
mvn spring-boot:run
Using IntelliJ IDEA
Go to Run → Edit Configurations
```

```
Add environment variables
```

```
Run EcommerceApplication
Running the JAR
bash
Copy code
java -jar target/ecommerce.jar
```

```
🛡 API Security
Spring Security filter chain
JWT-based authentication
Unauthorized → 401 / 403
Role restrictions via @PreAuthorize
HTTP-only cookies for protection
```

```
🚀 Future Enhancements
Product recommendation engine
Payment gateway integration (Stripe/Razorpay)
Email/SMS OTP authentication
Multi-language support
Seller payout module
Docker + Docker Compose setup
Analytics dashboards
```

```
📞 Contact
Rahul Agarwal
🔗 GitHub: https://github.com/Rahu0909
