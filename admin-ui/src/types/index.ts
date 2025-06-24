export interface User {
  id: number
  username: string
  email: string
  firstName?: string
  lastName?: string
  isActive: boolean
  emailVerified: boolean
  createdAt: string
  updatedAt: string
  roles: string[]
}

export interface Role {
  id: number
  name: string
  description?: string
  isSystemRole: boolean
  createdAt: string
  updatedAt: string
  permissions: string[]
  userCount: number
}

export interface Permission {
  id: number
  permissionKey: string
  name: string
  description?: string
  module: string
}

export interface CreateUserRequest {
  username: string
  email: string
  password: string
  firstName?: string
  lastName?: string
  roleIds?: number[]
  isActive?: boolean
}

export interface UpdateUserRequest {
  username?: string
  email?: string
  firstName?: string
  lastName?: string
  roleIds?: number[]
  isActive?: boolean
}

export interface CreateRoleRequest {
  name: string
  description?: string
  permissionIds?: number[]
}

export interface LoginRequest {
  username: string
  password: string
}

export interface AuthUser {
  id: number
  username: string
  email: string
  roles: string[]
  permissions: string[]
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface PermissionOverride {
  permissionId: number
  overrideType: 'GRANT' | 'DENY'
}

export interface UserPermissionOverrideRequest {
  userId: number
  overrides: PermissionOverride[]
}