# Role and Permission Management System

A comprehensive Spring Boot-based Role-Based Access Control (RBAC) system that provides robust permission configurations, customizable role creation, and user access management.

## Features

### Core Functionality
- **User Management**: Create, update, activate/deactivate users with email verification
- **Role Management**: Dynamic role creation with permission assignments
- **Permission System**: 50+ pre-defined permissions across multiple modules
- **Permission Overrides**: User-level permission grants/denials beyond role permissions
- **Authentication**: JWT-based authentication with email verification
- **Password Reset**: Secure password reset functionality via email
- **Export Capabilities**: Export users and roles data in Excel and CSV formats

### System Architecture
- **Backend**: Spring Boot 3.2.0 with Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Security**: JWT tokens with Spring Security
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Email**: Spring Mail integration

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL database

### Installation

1. Clone the repository
2. Configure database connection in `application.yml`
3. Build and run the application:

```bash
mvn clean compile
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Default Super Admin
- **Username**: `superadmin`
- **Password**: `SuperAdmin@123`
- **Email**: `superadmin@system.com`

## API Documentation

### Swagger UI
Access the interactive API documentation at: `http://localhost:8080/swagger-ui.html`

### OpenAPI Specification
Raw API specification available at: `http://localhost:8080/api-docs`

### Postman Collection
Import the complete Postman collection from: `resources/RBAC_System_Postman_Collection.json`

## API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `GET /api/auth/verify-email` - Email verification
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password

### User Management
- `POST /api/users` - Create user
- `GET /api/users` - List users (with pagination and filtering)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `PATCH /api/users/{id}/toggle-activation` - Toggle user activation
- `DELETE /api/users/{id}` - Delete user

### Role Management
- `POST /api/roles` - Create role
- `GET /api/roles` - List roles (with pagination and filtering)
- `GET /api/roles/{id}` - Get role by ID
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `GET /api/roles/non-system` - Get non-system roles

### Permission Management
- `GET /api/permissions` - List permissions (with pagination and filtering)
- `GET /api/permissions/modules` - Get permission modules
- `GET /api/permissions/grouped` - Get permissions grouped by module
- `GET /api/permissions/user/{userId}` - Get user effective permissions
- `POST /api/permissions/user/overrides` - Set user permission overrides
- `GET /api/permissions/check/{userId}/{permissionKey}` - Check user permission

### Export
- `GET /api/export/users/excel` - Export users to Excel
- `GET /api/export/users/csv` - Export users to CSV
- `GET /api/export/roles/excel` - Export roles to Excel
- `GET /api/export/roles/csv` - Export roles to CSV

## Permission System

The system includes 50+ permissions across the following modules:
- User Management
- Role Management
- Permission Management
- Reporting
- Dashboard
- Contract Management
- Communication
- System Administration
- Analytics
- Finance
- Inventory Management
- Customer Support
- Integration Management
- API Management
- Security
- Template Management
- Workflow Management

### Permission Override System
Users can have permissions overridden at the individual level:
- **GRANT**: Give permission even if not in role
- **DENY**: Remove permission even if in role

Final permissions = (Role Permissions) - (Denied Overrides) + (Granted Overrides)

## Database Schema

### Core Tables
- `users` - User accounts and profile information
- `roles` - Role definitions
- `permissions` - System permissions
- `user_roles` - User-role assignments
- `role_permissions` - Role-permission assignments
- `user_permission_overrides` - Individual user permission overrides

## Security Features

- JWT-based authentication
- Email verification for new accounts
- Password reset via secure tokens
- Permission-based access control on all endpoints
- Hardcoded Super Admin with elevated privileges
- System roles protection (cannot be modified/deleted)

## Configuration

### Environment Variables
- `DATABASE_URL` - PostgreSQL connection string
- `PGUSER` - Database username
- `PGPASSWORD` - Database password
- `MAIL_USERNAME` - SMTP username
- `MAIL_PASSWORD` - SMTP password

### Application Properties
Key configurations in `application.yml`:
- JWT secret and expiration
- Super Admin credentials
- Email SMTP settings
- Database connection

## Data Initialization

On application startup:
1. Permissions are loaded from `permissions.json`
2. Default roles (SUPER_ADMIN, CUSTOMER) are created
3. Super Admin user is created with all permissions
4. Customer role gets limited permissions from `customer-permissions.json`

## Testing with Postman

1. Import the collection from `resources/RBAC_System_Postman_Collection.json`
2. Set the `baseUrl` variable to `http://localhost:8080`
3. Login with Super Admin credentials to get authentication token
4. Set the `authToken` variable with the received JWT token
5. Test all available endpoints

## Development

### Adding New Permissions
1. Add permission definition to `permissions.json`
2. Restart application to load new permissions
3. Assign to roles as needed

### Creating Custom Roles
Use the role management APIs to create roles with specific permission sets.

### User Permission Overrides
Use the permission override APIs to grant or deny specific permissions to individual users.

## License

This project is licensed under the Apache 2.0 License.