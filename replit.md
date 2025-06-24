# Role and Permission Management System

## Overview

This is a Spring Boot-based Role and Permission Management System (RBAC) that provides robust permission configurations, customizable role creation, and user access management. The system follows a backend-first approach with a hardcoded Super Admin and JSON-based permission definitions.

## System Architecture

### Backend Architecture
- **Framework**: Spring Boot (Java)
- **Database**: MySQL for data persistence
- **Authentication**: JWT (JSON Web Token) based authentication
- **Permission Model**: JSON resource file defining permissions, imported on application startup
- **Authorization**: Role-Based Access Control (RBAC) with user-level permission overrides

### Key Design Decisions
- **Backend-only approach**: Role and permission management is handled entirely through backend APIs
- **JSON permission definitions**: Permissions are defined in a JSON resource file for easy configuration and version control
- **Hardcoded Super Admin**: A fixed Super Admin account with predefined credentials for system administration
- **Permission inheritance and override**: Users inherit permissions from roles but can have individual permission overrides

## Key Components

### User Management
- User creation, modification, and deactivation
- User-role assignments
- Individual permission overrides beyond role permissions

### Role Management
- Dynamic role creation and modification
- Role-permission assignments
- Role hierarchy support

### Permission System
- JSON-based permission definitions
- Startup import of permissions into database
- Granular permission control at resource and action levels

### Authentication & Authorization
- JWT-based authentication
- RBAC implementation with permission overrides
- Super Admin with elevated privileges

## Data Flow

1. **Application Startup**: JSON permission definitions are imported into MySQL database
2. **User Authentication**: Users authenticate via JWT tokens
3. **Permission Resolution**: System resolves effective permissions by combining role permissions and user-specific overrides
4. **Access Control**: API endpoints enforce permissions based on resolved user permissions

## External Dependencies

### Core Dependencies
- Spring Boot Framework
- Spring Security for authentication/authorization
- Spring Data JPA for database operations
- MySQL JDBC Driver
- JWT library for token management
- Jackson for JSON processing

### Database
- MySQL database for persistent storage of users, roles, permissions, and their relationships

## Deployment Strategy

- Spring Boot application packaged as executable JAR
- MySQL database deployment (local or cloud-based)
- Environment-specific configuration for database connections
- JSON permission file bundled with application resources

## User Preferences

Preferred communication style: Simple, everyday language.

## Changelog

✓ Complete Spring Boot RBAC system implemented with:
  - 50+ permissions across 17 modules (User Management, Role Management, etc.)
  - JWT authentication with email verification and security fix
  - User management with role assignments and permission overrides
  - Dynamic role creation with permission assignments
  - Permission override system (GRANT/DENY at user level)
  - Export functionality (Excel/CSV) for users and roles
  - OpenAPI documentation with Swagger UI
  - Complete Postman collection with all endpoints
  - MySQL database integration with H2 for development

- June 24, 2025: Fixed JWT WeakKeyException by implementing proper key padding (256+ bits)
- June 24, 2025: Complete RBAC system implementation
- June 24, 2025: Initial setup