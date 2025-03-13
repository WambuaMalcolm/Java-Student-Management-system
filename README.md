# Student Management System

A comprehensive Java-based application for managing student data, academic performance, and administrative tasks. This system provides an intuitive interface for tracking student progress, generating reports, and visualizing performance metrics.

## Features

- **Student Information Management**: Store and manage complete student profiles
- **Performance Tracking**: Monitor academic progress and generate trend analysis
- **Secure Authentication**: BCrypt encryption for password security
- **Data Visualization**: Interactive charts and graphs using JFreeChart
- **Report Generation**: Export performance reports in multiple formats
- **User Role Management**: Different access levels for students, faculty, and administrators
- **Database Integration**: Persistent storage with MySQL
- **Intuitive UI**: User-friendly interface for all system functions

## Prerequisites/Dependencies

- Java Development Kit (JDK) 8 or higher
- MySQL Server 5.7+ or MariaDB 10.3+
- Ant build system
- Required Java libraries:
  - JFreeChart 1.5.0+
  - MySQL JDBC Connector
  - BCrypt Library
  - Apache Commons Libraries

## Setup and Installation

1. **Clone the repository**
   ```
   git clone https://github.com/yourusername/StudentManagementSystem.git
   cd StudentManagementSystem
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE student_management;
   USE student_management;
   -- Run the included SQL script to create tables
   SOURCE database/schema.sql;
   ```

3. **Configure Database Connection**
   - Edit `src/config/db.properties` with your database credentials:
   ```
   db.url=jdbc:mysql://localhost:3306/student_management
   db.user=yourusername
   db.password=yourpassword
   ```

4. **Install Dependencies**
   - All required libraries are included in the `lib/` directory
   - Alternatively, use Ant to download dependencies:
   ```
   ant resolve
   ```

## Build Instructions

1. **Using Ant Build System**
   ```
   ant clean        # Clean previous builds
   ant compile      # Compile the source code
   ant jar          # Create executable JAR file
   ant run          # Run the application
   ant test         # Run unit tests
   ant dist         # Create distribution package
   ```

2. **Manual Build**
   ```
   mkdir -p build/classes
   javac -d build/classes -cp "lib/*" src/com/cuea/spm/**/*.java
   java -cp "build/classes:lib/*" com.cuea.spm.Main.Main
   ```

## Project Structure

```
StudentManagementSystem/
├── src/
│   └── com/
│       └── cuea/
│           └── spm/
│               ├── Main/        # Application entry point
│               ├── controller/  # Business logic and control flow
│               ├── dao/         # Data Access Objects for database operations
│               ├── model/       # Domain model classes
│               ├── util/        # Utility classes and helpers
│               └── view/        # UI components
├── lib/              # External libraries and dependencies
├── database/         # SQL scripts and database schema
├── build/            # Compiled bytecode (generated)
├── dist/             # Distribution packages (generated)
├── test/             # Unit and integration tests
├── build.xml         # Ant build configuration
├── manifest.mf       # Manifest file for JAR
└── README.md         # This documentation file
```

The project follows the DAO (Data Access Object) pattern to separate data persistence logic from business logic, making the codebase more maintainable and testable.

