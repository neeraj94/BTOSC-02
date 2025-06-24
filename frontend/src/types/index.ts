export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  isActive: boolean;
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
  roles: Role[];
}

export interface Role {
  id: number;
  name: string;
  description?: string;
  isSystemRole: boolean;
  createdAt: string;
  updatedAt: string;
  permissions: Permission[];
}

export interface Permission {
  id: number;
  name: string;
  description: string;
  module: string;
  action: string;
  resource: string;
}

export interface PermissionOverride {
  id: number;
  userId: number;
  permissionId: number;
  overrideType: 'GRANT' | 'DENY';
}

export interface PermissionState {
  permission: Permission;
  state: 'allow' | 'deny' | 'inherit';
  inherited?: boolean;
}

export interface PermissionModule {
  name: string;
  displayName: string;
  permissions: PermissionState[];
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}

export interface CreateRoleRequest {
  name: string;
  description?: string;
  permissionIds: number[];
}

export interface UpdateRoleRequest {
  name: string;
  description?: string;
  permissionIds: number[];
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  roleIds: number[];
  isActive: boolean;
}