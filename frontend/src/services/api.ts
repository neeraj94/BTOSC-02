import { 
  User, 
  Role, 
  Permission, 
  PaginatedResponse, 
  CreateRoleRequest, 
  UpdateRoleRequest,
  CreateUserRequest,
  PermissionOverride
} from '../types';
import { 
  allMockRoles, 
  mockUsers, 
  mockPermissions, 
  mockPermissionOverrides 
} from './mockData';

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Role API
export const roleApi = {
  async getAllRoles(page: number = 0, size: number = 20, sortBy: string = 'id', sortDir: string = 'asc'): Promise<PaginatedResponse<Role>> {
    await delay(300);
    
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const sortedRoles = [...allMockRoles].sort((a, b) => {
      if (sortDir === 'asc') {
        return a[sortBy as keyof Role] > b[sortBy as keyof Role] ? 1 : -1;
      } else {
        return a[sortBy as keyof Role] < b[sortBy as keyof Role] ? 1 : -1;
      }
    });
    
    const content = sortedRoles.slice(startIndex, endIndex);
    
    return {
      content,
      totalElements: allMockRoles.length,
      totalPages: Math.ceil(allMockRoles.length / size),
      number: page,
      size,
      first: page === 0,
      last: endIndex >= allMockRoles.length
    };
  },

  async getRoleById(id: number): Promise<Role> {
    await delay(200);
    const role = allMockRoles.find(r => r.id === id);
    if (!role) {
      throw new Error(`Role with id ${id} not found`);
    }
    return role;
  },

  async createRole(request: CreateRoleRequest): Promise<Role> {
    await delay(500);
    const newRole: Role = {
      id: Math.max(...allMockRoles.map(r => r.id)) + 1,
      name: request.name,
      description: request.description,
      isSystemRole: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      permissions: mockPermissions.filter(p => request.permissionIds.includes(p.id))
    };
    allMockRoles.push(newRole);
    return newRole;
  },

  async updateRole(id: number, request: UpdateRoleRequest): Promise<Role> {
    await delay(500);
    const roleIndex = allMockRoles.findIndex(r => r.id === id);
    if (roleIndex === -1) {
      throw new Error(`Role with id ${id} not found`);
    }
    
    const updatedRole: Role = {
      ...allMockRoles[roleIndex],
      name: request.name,
      description: request.description,
      updatedAt: new Date().toISOString(),
      permissions: mockPermissions.filter(p => request.permissionIds.includes(p.id))
    };
    
    allMockRoles[roleIndex] = updatedRole;
    return updatedRole;
  },

  async deleteRole(id: number): Promise<void> {
    await delay(300);
    const roleIndex = allMockRoles.findIndex(r => r.id === id);
    if (roleIndex === -1) {
      throw new Error(`Role with id ${id} not found`);
    }
    allMockRoles.splice(roleIndex, 1);
  },

  async getNonSystemRoles(): Promise<Role[]> {
    await delay(200);
    return allMockRoles.filter(r => !r.isSystemRole);
  }
};

// Permission API
export const permissionApi = {
  async getAllPermissions(page: number = 0, size: number = 50, sortBy: string = 'module', sortDir: string = 'asc'): Promise<PaginatedResponse<Permission>> {
    await delay(200);
    
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const sortedPermissions = [...mockPermissions].sort((a, b) => {
      if (sortDir === 'asc') {
        return a[sortBy as keyof Permission] > b[sortBy as keyof Permission] ? 1 : -1;
      } else {
        return a[sortBy as keyof Permission] < b[sortBy as keyof Permission] ? 1 : -1;
      }
    });
    
    const content = sortedPermissions.slice(startIndex, endIndex);
    
    return {
      content,
      totalElements: mockPermissions.length,
      totalPages: Math.ceil(mockPermissions.length / size),
      number: page,
      size,
      first: page === 0,
      last: endIndex >= mockPermissions.length
    };
  },

  async getPermissionModules(): Promise<string[]> {
    await delay(100);
    return [...new Set(mockPermissions.map(p => p.module))];
  },

  async getPermissionsGroupedByModule(): Promise<Record<string, Permission[]>> {
    await delay(200);
    const grouped: Record<string, Permission[]> = {};
    mockPermissions.forEach(permission => {
      if (!grouped[permission.module]) {
        grouped[permission.module] = [];
      }
      grouped[permission.module].push(permission);
    });
    return grouped;
  },

  async getUserEffectivePermissions(userId: number): Promise<Permission[]> {
    await delay(300);
    const user = mockUsers.find(u => u.id === userId);
    if (!user) {
      throw new Error(`User with id ${userId} not found`);
    }
    
    const rolePermissions = user.roles.flatMap(role => role.permissions);
    const uniquePermissions = rolePermissions.filter((permission, index, self) => 
      index === self.findIndex(p => p.id === permission.id)
    );
    
    return uniquePermissions;
  },

  async setUserPermissionOverrides(userId: number, overrides: Omit<PermissionOverride, 'id' | 'userId'>[]): Promise<void> {
    await delay(400);
    // Remove existing overrides for user
    const existingOverrideIndices = mockPermissionOverrides
      .map((override, index) => override.userId === userId ? index : -1)
      .filter(index => index !== -1)
      .reverse();
    
    existingOverrideIndices.forEach(index => {
      mockPermissionOverrides.splice(index, 1);
    });
    
    // Add new overrides
    overrides.forEach(override => {
      mockPermissionOverrides.push({
        id: Math.max(...mockPermissionOverrides.map(o => o.id), 0) + 1,
        userId,
        ...override
      });
    });
  },

  async checkUserPermission(userId: number, permissionName: string): Promise<boolean> {
    await delay(150);
    const userPermissions = await this.getUserEffectivePermissions(userId);
    return userPermissions.some(p => p.name === permissionName);
  }
};

// User API
export const userApi = {
  async getAllUsers(page: number = 0, size: number = 20, sortBy: string = 'id', sortDir: string = 'asc'): Promise<PaginatedResponse<User>> {
    await delay(300);
    
    const startIndex = page * size;
    const endIndex = startIndex + size;
    const sortedUsers = [...mockUsers].sort((a, b) => {
      if (sortDir === 'asc') {
        return a[sortBy as keyof User] > b[sortBy as keyof User] ? 1 : -1;
      } else {
        return a[sortBy as keyof User] < b[sortBy as keyof User] ? 1 : -1;
      }
    });
    
    const content = sortedUsers.slice(startIndex, endIndex);
    
    return {
      content,
      totalElements: mockUsers.length,
      totalPages: Math.ceil(mockUsers.length / size),
      number: page,
      size,
      first: page === 0,
      last: endIndex >= mockUsers.length
    };
  },

  async getUserById(id: number): Promise<User> {
    await delay(200);
    const user = mockUsers.find(u => u.id === id);
    if (!user) {
      throw new Error(`User with id ${id} not found`);
    }
    return user;
  },

  async createUser(request: CreateUserRequest): Promise<User> {
    await delay(500);
    const newUser: User = {
      id: Math.max(...mockUsers.map(u => u.id)) + 1,
      username: request.username,
      email: request.email,
      firstName: request.firstName,
      lastName: request.lastName,
      isActive: request.isActive,
      emailVerified: false,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      roles: allMockRoles.filter(r => request.roleIds.includes(r.id))
    };
    mockUsers.push(newUser);
    return newUser;
  },

  async updateUser(id: number, request: Partial<CreateUserRequest>): Promise<User> {
    await delay(500);
    const userIndex = mockUsers.findIndex(u => u.id === id);
    if (userIndex === -1) {
      throw new Error(`User with id ${id} not found`);
    }
    
    const updatedUser: User = {
      ...mockUsers[userIndex],
      ...request,
      updatedAt: new Date().toISOString(),
      roles: request.roleIds ? allMockRoles.filter(r => request.roleIds!.includes(r.id)) : mockUsers[userIndex].roles
    };
    
    mockUsers[userIndex] = updatedUser;
    return updatedUser;
  },

  async toggleUserActivation(id: number): Promise<User> {
    await delay(300);
    const userIndex = mockUsers.findIndex(u => u.id === id);
    if (userIndex === -1) {
      throw new Error(`User with id ${id} not found`);
    }
    
    mockUsers[userIndex].isActive = !mockUsers[userIndex].isActive;
    mockUsers[userIndex].updatedAt = new Date().toISOString();
    
    return mockUsers[userIndex];
  },

  async deleteUser(id: number): Promise<void> {
    await delay(300);
    const userIndex = mockUsers.findIndex(u => u.id === id);
    if (userIndex === -1) {
      throw new Error(`User with id ${id} not found`);
    }
    mockUsers.splice(userIndex, 1);
  }
};

// Export API
export const exportApi = {
  async exportUsersToExcel(): Promise<Blob> {
    await delay(1000);
    // Mock Excel export - in real implementation, this would generate an actual Excel file
    const csvContent = [
      'ID,Username,Email,First Name,Last Name,Active,Email Verified,Created At,Roles',
      ...mockUsers.map(user => 
        `${user.id},${user.username},${user.email},${user.firstName},${user.lastName},${user.isActive},${user.emailVerified},${user.createdAt},"${user.roles.map(r => r.name).join(', ')}"`
      )
    ].join('\n');
    
    return new Blob([csvContent], { type: 'application/vnd.ms-excel' });
  },

  async exportUsersToCSV(): Promise<Blob> {
    await delay(800);
    const csvContent = [
      'ID,Username,Email,First Name,Last Name,Active,Email Verified,Created At,Roles',
      ...mockUsers.map(user => 
        `${user.id},${user.username},${user.email},${user.firstName},${user.lastName},${user.isActive},${user.emailVerified},${user.createdAt},"${user.roles.map(r => r.name).join(', ')}"`
      )
    ].join('\n');
    
    return new Blob([csvContent], { type: 'text/csv' });
  },

  async exportRolesToExcel(): Promise<Blob> {
    await delay(1000);
    const csvContent = [
      'ID,Name,Description,System Role,Created At,Permissions Count',
      ...allMockRoles.map(role => 
        `${role.id},${role.name},"${role.description || ''}",${role.isSystemRole},${role.createdAt},${role.permissions.length}`
      )
    ].join('\n');
    
    return new Blob([csvContent], { type: 'application/vnd.ms-excel' });
  },

  async exportRolesToCSV(): Promise<Blob> {
    await delay(800);
    const csvContent = [
      'ID,Name,Description,System Role,Created At,Permissions Count',
      ...allMockRoles.map(role => 
        `${role.id},${role.name},"${role.description || ''}",${role.isSystemRole},${role.createdAt},${role.permissions.length}`
      )
    ].join('\n');
    
    return new Blob([csvContent], { type: 'text/csv' });
  }
};