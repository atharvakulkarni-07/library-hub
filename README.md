# Library Management System

A comprehensive library management system built with **Spring Boot** and **PostgreSQL**, featuring complete book inventory management, member registration, and transaction handling with automated fine calculations.

## 🚀 Features

- **📚 Book Management**: Complete CRUD operations with inventory tracking
- **👥 Member Management**: Member registration with active/inactive status
- **📋 Transaction System**: Book borrowing and returning with fine calculations
- **🔍 Search Functionality**: Search books by title/author and members by name/email
- **💰 Automated Fines**: ₹5 per day for overdue books
- **📊 Transaction History**: Complete audit trail of all library activities
- **🎯 Inventory Tracking**: Real-time available copies management

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.5.4
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **ORM**: Spring JDBC
- **Validation**: Jakarta Validation
- **Development**: Spring Boot DevTools, Lombok

## 📋 Prerequisites

- Java 21 or higher
- PostgreSQL 12 or higher
- Maven 3.6+
- Git

## ⚙️ Setup Instructions

### 1. Clone Repository
- git clone https://github.com/atharvakulkarni-07/library-hub.git
- cd library-hub

### 2. Create the Database 
- Follow the SQL Script in the same repository.


### 3. Application Configuration
Create `src/main/resources/application.properties`:

#### Database Configuration
```
spring.datasource.url=jdbc:postgresql://localhost:5432/library_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
```

#### Application Configuration
```
server.port=8080
spring.application.name=libraryhub
```

#### JSON Configuration
```
spring.jackson.serialization.write-dates-as-timestamps=false
```

#### Disabled Security for Development
```
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration
```


### 4. Run Application
```commandline
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

## 📚 API Endpoints

### Books Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/isbn/{isbn}` | Get book by ISBN |
| POST | `/api/books` | Add new book |
| PUT | `/api/books/{id}` | Update book |
| DELETE | `/api/books/{id}` | Delete book |
| GET | `/api/books/search?query={term}` | Search books |
| GET | `/api/books/{id}/availability` | Check availability |

### Members Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/members` | Get all members |
| GET | `/api/members/active` | Get active members |
| GET | `/api/members/{id}` | Get member by ID |
| GET | `/api/members/member-id/{memberId}` | Get by member ID |
| GET | `/api/members/email/{email}` | Get by email |
| POST | `/api/members` | Add new member |
| PUT | `/api/members/{id}` | Update member |
| DELETE | `/api/members/{id}` | Delete member |
| PUT | `/api/members/{id}/activate` | Activate member |
| PUT | `/api/members/{id}/deactivate` | Deactivate member |
| GET | `/api/members/search?query={term}` | Search members |

### Transaction Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/transactions/issue` | Issue a book |
| PUT | `/api/transactions/{id}/return` | Return a book |
| GET | `/api/transactions` | Get all transactions |
| GET | `/api/transactions/{id}` | Get transaction by ID |
| GET | `/api/transactions/member/{memberId}` | Member's transactions |
| GET | `/api/transactions/book/{bookId}` | Book's transaction history |
| GET | `/api/transactions/active` | Active transactions |
| GET | `/api/transactions/overdue` | Overdue transactions |


## Project Structure
```commandline
src/main/java/com/management/demo/
├── controller/ # REST Controllers
│ ├── BookController.java
│ ├── MemberController.java
│ └── TransactionController.java
├── model/ # Entity Classes
│ ├── Book.java
│ ├── Member.java
│ └── Transaction.java
├── repository/ # Data Access Layer
│ ├── BookRepository.java
│ ├── MemberRepository.java
│ └── TransactionRepository.java
├── service/ # Business Logic Layer
│ ├── BookService.java
│ ├── MemberService.java
│ └── TransactionService.java
└── LibraryhubApplication.java
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 👨‍💻 Author

**Atharva Kulkarni** - [GitHub Profile](https://github.com/atharvakulkarni-07)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- PostgreSQL team for robust database system
- Contributors and testers

---

⭐ **Star this repository if you find it helpful!**