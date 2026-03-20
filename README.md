🚀 Expert Hub (Professional Mentorship & Expert Hub)
Java Spring Boot Angular Status

Expert Hub is a purpose-driven, full-stack platform designed to accelerate professional growth. It bridges the gap between aspiring professionals and seasoned industry experts, providing a dedicated space for paid mentorship, secure networking, and authentic skill development.

📖 Table of Contents
Project Overview
User Journey & Flow
Key Features
System Architecture
Tech Stack
Challenges & Solutions
Installation & Setup
🔎 Project Overview
The core mission of Expert Hub is to empower individuals to "level up" in their respective fields. Users can utilize advanced search filters to discover verified experts, securely pay for mentorship sessions, and unlock dedicated chat channels to gain real-world experience. To maintain a transparent and trustworthy community, the platform enforces a strict feedback loop where reviews can only be left after a verified interaction.

The application is powered by a robust Spring Boot backend that ensures secure communication, payment status validation, and complex data handling, while the Angular frontend delivers a highly responsive SPA experience.

🗺️ User Journey & Flow
Discover: User searches for an expert using multiple professional criteria.
Commit: User pays the expert for a mentorship session or consultation.
Level Up: A private chat is unlocked, allowing 1-on-1 knowledge transfer.
Evaluate: Post-mentorship, the user leaves an authentic public review.
✨ Key Features
🤝 Premium Mentorship & Communication
Advanced Multi-Field Search: Find the perfect mentor by filtering through various criteria, specific skills, and professional fields.
Payment-Gated Chat: Direct messaging is an exclusive feature unlocked only after the user has paid the expert (hasPaid validation). This ensures experts are compensated for their time and users receive dedicated 1-on-1 mentorship to level up their skills.
⭐ Trust & Authenticity System
Verified Public Reviews: To maintain platform integrity, users can only leave public reviews and comments after they have officially contacted/paid the expert. This ensures that all visible feedback on an expert's profile—whether positive or critical—is 100% genuine and based on real interactions.
Dynamic Portfolios: Detailed profiles showcasing user skills, bios, and Base64 encoded avatars to build credibility.
🛡️ Security & Performance
Role-Based Access: Controlled endpoints and functionality based on user roles and payment status using Spring Security.
Optimized Data Fetching: Highly optimized backend queries returning localized Data Transfer Objects (DTOs) instead of heavy database entities, preventing memory leaks and speeding up load times.
🏗️ System Architecture
The application follows a standard RESTful client-server architecture. The Angular frontend communicates with the Spring Boot backend via HTTP Observables. Complex relational data is safely managed using Data Transfer Objects (DTOs) before being serialized into JSON payloads.

🛠️ Tech Stack
Frontend	Backend	Database & Tools
Angular	Java 17+	MySQL / PostgreSQL
TypeScript	Spring Boot 3.x	Spring Data JPA / Hibernate
RxJS (Observables)	Spring Security	Maven
HTML5 & CSS3/SCSS	RESTful APIs	ModelMapper
Bootstrap	Lombok	Git & GitHub
⚙️ Installation & Setup
Prerequisites
Node.js (v18+) & Angular CLI
Java Development Kit (JDK) 17+
Maven
A running instance of your preferred SQL Database
1. Backend Setup
# Clone the repository
git clone [https://github.com/yourusername/skillsh.git](https://github.com/yourusername/skillsh.git)

# Navigate to backend directory
cd skillsh/backend
###2.Frontend Setup

Navigate to frontend directory
cd ../frontend

Install dependencies
npm install

Start the Angular server
ng serve

Soon it will be finished.

Configure your application.properties with your DB credentials
Run the application
mvn spring-boot:run
