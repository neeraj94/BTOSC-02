import axios from 'axios'
import { 
  LoginRequest, 
  User, 
  Role, 
  Permission, 
  CreateUserRequest, 
  UpdateUserRequest, 
  CreateRoleRequest,
  PaginatedResponse,
  UserPermissionOverrideRequest
} from '../types'

const API_BASE_URL = '/api'

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('userData')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Auth API
export const authApi = {
  login: (credentials: LoginRequest) => api.post('/auth/signin', credentials),
}

// Users API
export const usersApi = {
  getUsers: (params?: {
    page?: number
    size?: number
    sortBy?: string
    sortDir?: string
    username?: string
    email?: string
    isActive?: boolean
  }) => api.get<PaginatedResponse<User>>('/users', { params }),
  
  getUserById: (id: number) => api.get<User>(`/users/${id}`),
  
  createUser: (data: CreateUserRequest) => api.post<User>('/users', data),
  
  updateUser: (id: number, data: UpdateUserRequest) => api.put<User>(`/users/${id}`, data),
  
  deleteUser: (id: number) => api.delete(`/users/${id}`),
  
  toggleUserActivation: (id: number) => api.patch<User>(`/users/${id}/toggle-activation`),
}

// Roles API
export const rolesApi = {
  getRoles: (params?: {
    page?: number
    size?: number
    sortBy?: string
    sortDir?: string
    name?: string
    description?: string
  }) => api.get<PaginatedResponse<Role>>('/roles', { params }),
  
  getRoleById: (id: number) => api.get<Role>(`/roles/${id}`),
  
  createRole: (data: CreateRoleRequest) => api.post<Role>('/roles', data),
  
  updateRole: (id: number, data: CreateRoleRequest) => api.put<Role>(`/roles/${id}`, data),
  
  deleteRole: (id: number) => api.delete(`/roles/${id}`),
  
  getNonSystemRoles: () => api.get<Role[]>('/roles/non-system'),
}

// Permissions API
export const permissionsApi = {
  getPermissions: (params?: {
    page?: number
    size?: number
    sortBy?: string
    sortDir?: string
    module?: string
    name?: string
  }) => api.get<PaginatedResponse<Permission>>('/permissions', { params }),
  
  getPermissionModules: () => api.get<string[]>('/permissions/modules'),
  
  getPermissionsGrouped: () => api.get<Record<string, Permission[]>>('/permissions/grouped'),
  
  getUserEffectivePermissions: (userId: number) => api.get<string[]>(`/permissions/user/${userId}`),
  
  setUserPermissionOverrides: (data: UserPermissionOverrideRequest) => 
    api.post('/permissions/user/overrides', data),
  
  checkUserPermission: (userId: number, permissionKey: string) => 
    api.get<{ hasPermission: boolean }>(`/permissions/check/${userId}/${permissionKey}`),
}

// Export API
export const exportApi = {
  exportUsersExcel: () => api.get('/export/users/excel', { responseType: 'blob' }),
  exportUsersCSV: () => api.get('/export/users/csv', { responseType: 'blob' }),
  exportRolesExcel: () => api.get('/export/roles/excel', { responseType: 'blob' }),
  exportRolesCSV: () => api.get('/export/roles/csv', { responseType: 'blob' }),
}

export default api