# ToDo App

A simple and efficient ToDo application built with Java and Spring Boot. This app allows users to register, log in, and manage their daily tasks. The ToDo App supports CRUD operations for tasks, including setting priorities, due dates, and marking tasks as completed.

## Features

- **User Authentication**: Secure login and registration with JWT-based authentication.
- **Task Management**: Create, read, update, and delete tasks with details such as title, description, due date, and priority.
- **Data Persistence**: Utilizes MySQL to store user and task information persistently.
- **Error Handling**: Global exception handling to manage application errors gracefully.
- **Validation**: Ensures valid data is entered through field validation.

## Technologies Used

- **Java 17**
- **Spring Boot 3** - Core framework for application structure.
- **Spring Security** - For secure authentication and authorization.
- **JWT (JSON Web Token)** - Token-based authentication for secure API access.
- **MySQL** - Database for storing user and task data.
- **Hibernate** - ORM for easy database interactions.
- **Maven** - Dependency management.

## Prerequisites

- **Java 17**
- **Maven**
- **MySQL** (or other compatible database)
- **Git** (optional, for version control)

## Setup and Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ChathurRandul/todo-app.git
   cd todo-app
