# 🚀  Share/Hub

A full-stack web application designed to connect users, showcase their skills, and build a trusted community through peer reviews and messaging. 

## ✨ Features

* **User Profiles:** Detailed user profiles including avatars (Base64 encoding), bio, and personalized URLs (search by ID or Username).
* **Review System:** Users can leave and receive reviews. (Implemented with safe DTO mappings to ensure optimal API performance).
* **Real-time Interaction:** Features include user-to-user messaging and commenting.
* **Security & Access Control:** Role-based access and authentication built with Spring Security.
* **Premium Features:** Tracking for paid/premium user statuses (`hasPaid` flag).

## 🛠️ Tech Stack

### Frontend
* **Framework:** Angular
* **Styling:** HTML5, CSS3, Bootstrap (responsive design)
* **HTTP Communication:** RxJS Observables & Angular HttpClient

### Backend
* **Core:** Java & Spring Boot
* **Security:** Spring Security
* **Database Interaction:** Spring Data JPA (Hibernate)
* **Data Mapping:** ModelMapper & carefully designed DTOs (Data Transfer Objects) to prevent infinite recursion and optimize payload size.
* **API Architecture:** RESTful API

## ⚙️ Installation & Setup

### Prerequisites
* Node.js and npm (for Angular)
* Java 17 or higher
* Maven
* Relational Database (e.g., MySQL or PostgreSQL)

### Backend Setup
1. Clone the repository: `git clone https://github.com/yourusername/skillsh.git`
2. Navigate to the backend directory.
3. Update the `application.properties` or `application.yml` file with your database credentials.
4. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
The backend will start on http://localhost:8080

Frontend Setup
Navigate to the frontend (Angular) directory.

Install dependencies:

Bash
npm install
Start the development server:

Bash
ng serve
The application will be available at http://localhost:4200
