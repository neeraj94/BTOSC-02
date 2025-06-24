import { User, Role, Permission, PermissionOverride } from '../types';

// Mock Permissions Data
export const mockPermissions: Permission[] = [
  // User Management
  { id: 1, name: 'user.create', description: 'Create new users', module: 'User Management', action: 'create', resource: 'user' },
  { id: 2, name: 'user.read', description: 'View user details', module: 'User Management', action: 'read', resource: 'user' },
  { id: 3, name: 'user.update', description: 'Update user information', module: 'User Management', action: 'update', resource: 'user' },
  { id: 4, name: 'user.delete', description: 'Delete users', module: 'User Management', action: 'delete', resource: 'user' },
  { id: 5, name: 'user.activate', description: 'Activate/deactivate users', module: 'User Management', action: 'activate', resource: 'user' },
  
  // Role Management
  { id: 6, name: 'role.create', description: 'Create new roles', module: 'Role Management', action: 'create', resource: 'role' },
  { id: 7, name: 'role.read', description: 'View role details', module: 'Role Management', action: 'read', resource: 'role' },
  { id: 8, name: 'role.update', description: 'Update role information', module: 'Role Management', action: 'update', resource: 'role' },
  { id: 9, name: 'role.delete', description: 'Delete roles', module: 'Role Management', action: 'delete', resource: 'role' },
  
  // Permission Management
  { id: 10, name: 'permission.read', description: 'View permissions', module: 'Permission Management', action: 'read', resource: 'permission' },
  { id: 11, name: 'permission.assign', description: 'Assign permissions to roles', module: 'Permission Management', action: 'assign', resource: 'permission' },
  { id: 12, name: 'permission.override', description: 'Override user permissions', module: 'Permission Management', action: 'override', resource: 'permission' },
  
  // Attribute Management
  { id: 13, name: 'attribute.create', description: 'Create attributes', module: 'Attribute Management', action: 'create', resource: 'attribute' },
  { id: 14, name: 'attribute.read', description: 'View attributes', module: 'Attribute Management', action: 'read', resource: 'attribute' },
  { id: 15, name: 'attribute.update', description: 'Update attributes', module: 'Attribute Management', action: 'update', resource: 'attribute' },
  { id: 16, name: 'attribute.delete', description: 'Delete attributes', module: 'Attribute Management', action: 'delete', resource: 'attribute' },
  
  // Attribute Set Management
  { id: 17, name: 'attribute_set.create', description: 'Create attribute sets', module: 'Attribute Set Management', action: 'create', resource: 'attribute_set' },
  { id: 18, name: 'attribute_set.read', description: 'View attribute sets', module: 'Attribute Set Management', action: 'read', resource: 'attribute_set' },
  { id: 19, name: 'attribute_set.update', description: 'Update attribute sets', module: 'Attribute Set Management', action: 'update', resource: 'attribute_set' },
  { id: 20, name: 'attribute_set.delete', description: 'Delete attribute sets', module: 'Attribute Set Management', action: 'delete', resource: 'attribute_set' },
  
  // Blog Management
  { id: 21, name: 'blog.create', description: 'Create blog posts', module: 'Blog Management', action: 'create', resource: 'blog' },
  { id: 22, name: 'blog.read', description: 'View blog posts', module: 'Blog Management', action: 'read', resource: 'blog' },
  { id: 23, name: 'blog.update', description: 'Update blog posts', module: 'Blog Management', action: 'update', resource: 'blog' },
  { id: 24, name: 'blog.delete', description: 'Delete blog posts', module: 'Blog Management', action: 'delete', resource: 'blog' },
  { id: 25, name: 'blog.publish', description: 'Publish blog posts', module: 'Blog Management', action: 'publish', resource: 'blog' },
  
  // System Administration
  { id: 26, name: 'system.config', description: 'System configuration', module: 'System Administration', action: 'config', resource: 'system' },
  { id: 27, name: 'system.backup', description: 'System backup', module: 'System Administration', action: 'backup', resource: 'system' },
  { id: 28, name: 'system.monitor', description: 'System monitoring', module: 'System Administration', action: 'monitor', resource: 'system' },
  
  // Reports
  { id: 29, name: 'report.view', description: 'View reports', module: 'Reports', action: 'view', resource: 'report' },
  { id: 30, name: 'report.export', description: 'Export reports', module: 'Reports', action: 'export', resource: 'report' },
  { id: 31, name: 'report.create', description: 'Create custom reports', module: 'Reports', action: 'create', resource: 'report' },
  
  // Audit
  { id: 32, name: 'audit.view', description: 'View audit logs', module: 'Audit', action: 'view', resource: 'audit' },
  { id: 33, name: 'audit.export', description: 'Export audit logs', module: 'Audit', action: 'export', resource: 'audit' }
];

// Mock Roles Data
export const mockRoles: Role[] = [
  {
    id: 1,
    name: 'Super Admin',
    description: 'Full system access with all permissions',
    isSystemRole: true,
    createdAt: '2024-01-15T10:30:00Z',
    updatedAt: '2024-01-15T10:30:00Z',
    permissions: mockPermissions
  },
  {
    id: 2,
    name: 'Admin',
    description: 'Administrative access with most permissions',
    isSystemRole: true,
    createdAt: '2019-06-20T14:22:00Z',
    updatedAt: '2024-02-10T09:15:00Z',
    permissions: mockPermissions.filter(p => !p.name.includes('system'))
  },
  {
    id: 3,
    name: 'Manager',
    description: 'Management role with user and content permissions',
    isSystemRole: false,
    createdAt: '2019-06-20T14:22:00Z',
    updatedAt: '2024-03-05T16:45:00Z',
    permissions: mockPermissions.filter(p => 
      p.module.includes('User') || p.module.includes('Blog') || p.module.includes('Report')
    )
  },
  {
    id: 4,
    name: 'Editor',
    description: 'Content editor with blog management permissions',
    isSystemRole: false,
    createdAt: '2020-03-12T11:10:00Z',
    updatedAt: '2024-02-28T13:20:00Z',
    permissions: mockPermissions.filter(p => 
      p.module.includes('Blog') || (p.module.includes('User') && p.action === 'read')
    )
  },
  {
    id: 5,
    name: 'Viewer',
    description: 'Read-only access to most content',
    isSystemRole: false,
    createdAt: '2020-08-05T09:30:00Z',
    updatedAt: '2024-01-18T14:25:00Z',
    permissions: mockPermissions.filter(p => p.action === 'read')
  },
  {
    id: 6,
    name: 'Customer Support',
    description: 'Customer support role with limited user management',
    isSystemRole: false,
    createdAt: '2021-01-20T08:45:00Z',
    updatedAt: '2024-03-12T10:15:00Z',
    permissions: mockPermissions.filter(p => 
      (p.module.includes('User') && ['read', 'update'].includes(p.action)) ||
      p.module.includes('Audit')
    )
  }
];

// Generate more mock roles to reach 40+ records
const additionalRoles: Role[] = [];
const roleTemplates = [
  { name: 'Content Manager', description: 'Manages all content related operations' },
  { name: 'Marketing Manager', description: 'Marketing and promotion management' },
  { name: 'Sales Manager', description: 'Sales operations and customer management' },
  { name: 'HR Manager', description: 'Human resources management' },
  { name: 'Finance Manager', description: 'Financial operations and reporting' },
  { name: 'Operations Manager', description: 'Operational processes and workflows' },
  { name: 'Quality Assurance', description: 'Quality control and testing' },
  { name: 'Technical Lead', description: 'Technical leadership and architecture' },
  { name: 'Project Coordinator', description: 'Project management and coordination' },
  { name: 'Business Analyst', description: 'Business analysis and requirements' },
  { name: 'Data Analyst', description: 'Data analysis and reporting' },
  { name: 'Security Officer', description: 'Security management and compliance' },
  { name: 'Training Coordinator', description: 'Training and development programs' },
  { name: 'Vendor Manager', description: 'Vendor relations and management' },
  { name: 'Compliance Officer', description: 'Regulatory compliance and auditing' },
  { name: 'Research Analyst', description: 'Market research and analysis' },
  { name: 'Product Manager', description: 'Product development and management' },
  { name: 'Customer Success', description: 'Customer satisfaction and retention' },
  { name: 'Technical Support', description: 'Technical assistance and troubleshooting' },
  { name: 'Content Writer', description: 'Content creation and copywriting' },
  { name: 'Social Media Manager', description: 'Social media strategy and management' },
  { name: 'Event Coordinator', description: 'Event planning and execution' },
  { name: 'Inventory Manager', description: 'Inventory control and management' },
  { name: 'Procurement Officer', description: 'Purchasing and vendor management' },
  { name: 'Legal Advisor', description: 'Legal counsel and contract management' },
  { name: 'IT Support', description: 'Information technology support' },
  { name: 'Database Administrator', description: 'Database management and optimization' },
  { name: 'Network Administrator', description: 'Network infrastructure management' },
  { name: 'System Administrator', description: 'System maintenance and administration' },
  { name: 'DevOps Engineer', description: 'Development operations and deployment' },
  { name: 'UX Designer', description: 'User experience design and research' },
  { name: 'Graphic Designer', description: 'Visual design and creative assets' },
  { name: 'Video Producer', description: 'Video content creation and production' },
  { name: 'Translator', description: 'Language translation and localization' },
  { name: 'Community Manager', description: 'Community engagement and moderation' },
  { name: 'Partnership Manager', description: 'Strategic partnerships and alliances' },
  { name: 'Risk Manager', description: 'Risk assessment and mitigation' },
  { name: 'Change Manager', description: 'Change management and process improvement' }
];

roleTemplates.forEach((template, index) => {
  const baseId = 7 + index;
  const createdDate = new Date(2021 + Math.floor(index / 12), (index % 12), Math.floor(Math.random() * 28) + 1);
  const permissionSubset = mockPermissions.filter((_, i) => i % (index + 2) === 0);
  
  additionalRoles.push({
    id: baseId,
    name: template.name,
    description: template.description,
    isSystemRole: false,
    createdAt: createdDate.toISOString(),
    updatedAt: new Date(createdDate.getTime() + Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
    permissions: permissionSubset
  });
});

export const allMockRoles = [...mockRoles, ...additionalRoles];

// Mock Users Data
export const mockUsers: User[] = [
  {
    id: 1,
    username: 'superadmin',
    email: 'superadmin@company.com',
    firstName: 'Super',
    lastName: 'Admin',
    isActive: true,
    emailVerified: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
    roles: [mockRoles[0]]
  },
  {
    id: 2,
    username: 'admin',
    email: 'admin@company.com',
    firstName: 'System',
    lastName: 'Administrator',
    isActive: true,
    emailVerified: true,
    createdAt: '2024-01-02T08:30:00Z',
    updatedAt: '2024-01-02T08:30:00Z',
    roles: [mockRoles[1]]
  }
];

// Generate additional users
const firstNames = ['John', 'Jane', 'Michael', 'Sarah', 'David', 'Lisa', 'Robert', 'Emma', 'William', 'Olivia', 'James', 'Sophia', 'Benjamin', 'Charlotte', 'Daniel', 'Amelia', 'Matthew', 'Harper', 'Anthony', 'Evelyn'];
const lastNames = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 'Martinez', 'Hernandez', 'Lopez', 'Gonzalez', 'Wilson', 'Anderson', 'Thomas', 'Taylor', 'Moore', 'Jackson', 'Martin'];

for (let i = 3; i <= 50; i++) {
  const firstName = firstNames[Math.floor(Math.random() * firstNames.length)];
  const lastName = lastNames[Math.floor(Math.random() * lastNames.length)];
  const username = `${firstName.toLowerCase()}.${lastName.toLowerCase()}${i}`;
  const email = `${username}@company.com`;
  const roleIndex = Math.floor(Math.random() * mockRoles.length);
  const createdDate = new Date(2023 + Math.random(), Math.floor(Math.random() * 12), Math.floor(Math.random() * 28) + 1);
  
  mockUsers.push({
    id: i,
    username,
    email,
    firstName,
    lastName,
    isActive: Math.random() > 0.1, // 90% active
    emailVerified: Math.random() > 0.05, // 95% verified
    createdAt: createdDate.toISOString(),
    updatedAt: new Date(createdDate.getTime() + Math.random() * 365 * 24 * 60 * 60 * 1000).toISOString(),
    roles: [mockRoles[roleIndex]]
  });
}

export const mockPermissionOverrides: PermissionOverride[] = [
  { id: 1, userId: 2, permissionId: 26, overrideType: 'GRANT' },
  { id: 2, userId: 3, permissionId: 4, overrideType: 'DENY' }
];