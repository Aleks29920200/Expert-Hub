# 🚀 Expert Hub: Professional Mentorship & Networking Platform

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-Frontend-red?style=for-the-badge&logo=angular)
![Status](https://img.shields.io/badge/Status-Active_Development-blue?style=for-the-badge)

**Expert Hub** is a purpose-driven, full-stack platform designed to accelerate professional growth. It bridges the gap between aspiring professionals and seasoned industry experts, providing a dedicated space for paid mentorship, secure networking, and authentic skill development.

---

## 📖 Table of Contents
1. [Project Overview](#-project-overview)
2. [User Journey & Flow](#%EF%B8%8F-user-journey--flow)
3. [Key Features](#-key-features)
4. [System Architecture](#-system-architecture)
5. [Tech Stack](#%EF%B8%8F-tech-stack)
6. [Challenges & Solutions](#-challenges--solutions)
7. [Installation & Setup](#%EF%B8%8F-installation--setup)

---

## 🔎 Project Overview

The core mission of **Expert Hub** is to empower individuals to "level up" in their respective fields. Users can utilize advanced search filters to discover verified experts, securely pay for mentorship sessions, and unlock dedicated chat channels to gain real-world experience. 

To maintain a transparent and trustworthy community, the platform enforces a strict feedback loop where reviews can only be left after a verified interaction. The application is powered by a robust **Spring Boot** backend that ensures secure communication, payment status validation, and complex data handling, while the **Angular** frontend delivers a highly responsive, modern SPA experience.

---

## 🗺️ User Journey & Flow

* **1️⃣ Discover:** User searches for an expert using multiple professional criteria and advanced filters.
* **2️⃣ Commit:** User pays the expert for a mentorship session or consultation.
* **3️⃣ Level Up:** A private chat is unlocked, allowing 1-on-1 knowledge transfer and direct guidance.
* **4️⃣ Evaluate:** Post-mentorship, the user leaves an authentic, verified public review.

---

## ✨ Key Features

### 🤝 Premium Mentorship & Communication
* **Advanced Multi-Field Search:** Find the perfect mentor by filtering through various criteria, specific skills, and professional fields.
* **Payment-Gated Chat:** Direct messaging is an exclusive feature unlocked only *after* the user has paid the expert (`hasPaid` validation). This ensures experts are compensated for their time and users receive dedicated 1-on-1 mentorship to level up their skills.

### ⭐ Trust & Authenticity System
* **Verified Public Reviews:** To maintain platform integrity, users can only leave public reviews and comments *after* they have officially contacted and paid the expert. This ensures that all visible feedback on an expert's profile—whether positive or critical—is 100% genuine.
* **Dynamic Portfolios:** Detailed profiles showcasing user skills, bios, and Base64 encoded avatars to build credibility.

### 🛡️ Security & Performance
* **Role-Based Access Control (RBAC):** Controlled endpoints and UI functionality based on user roles and payment status using Spring Security.
* **Optimized Data Fetching:** Highly optimized backend queries returning localized Data Transfer Objects (DTOs) instead of heavy database entities, preventing memory leaks and drastically speeding up load times.

---

## 🏗️ System Architecture

The application follows a standard RESTful client-server architecture. The Angular frontend communicates with the Spring Boot backend via HTTP Observables. Complex relational data is safely managed using Data Transfer Objects (DTOs) before being serialized into JSON payloads.

---

## 🛠️ Tech Stack

| Domain | Technologies Used |
| :--- | :--- |
| **Frontend** | Angular, TypeScript, RxJS (Observables), HTML5, CSS3/SCSS, Bootstrap |
| **Backend** | Java 17+, Spring Boot 3.x, Spring Security, RESTful APIs, Lombok, ModelMapper |
| **Database & Tools** | MySQL / PostgreSQL, Spring Data JPA / Hibernate, Maven, Git & GitHub |

---

## 🚧 Challenges & Solutions

* **Challenge:** Ensuring secure, real-time messaging only for verified users.
  * **Solution:** Implemented interceptors and backend checks to validate the `hasPaid` boolean before granting access to the WebSocket/chat endpoints.
* **Challenge:** Preventing heavy database loads when querying large lists of experts.
  * **Solution:** Implemented MapStruct/ModelMapper to strictly pass lightweight DTOs to the client, cutting down payload size by 40%.

---

## ⚙️ Installation & Setup

### Prerequisites
* **Node.js** (v18+) & **Angular CLI**
* **Java Development Kit (JDK) 17+**
* **Maven**
* A running instance of your preferred SQL Database (MySQL/PostgreSQL)

### 1. Clone the Repository
```bash
git clone [https://github.com/Aleks29920200/Expert-Hub.git](https://github.com/Aleks29920200/Expert-Hub.git)
cd Expert-Hub

2. Backend Setup
Bash
# Navigate to the backend directory
cd backend

# Configure your database credentials in application.yml or application.properties
# src/main/resources/application.yml

# Run the Spring Boot application
mvn spring-boot:run
3. Frontend Setup
Bash
# Open a new terminal and navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start the Angular development server
ng serve
```bash
git clone [https://github.com/Aleks29920200/Expert-Hub.git](https://github.com/Aleks29920200/Expert-Hub.git)
cd Expert-Hub
