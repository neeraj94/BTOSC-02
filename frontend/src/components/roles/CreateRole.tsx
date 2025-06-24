import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { Permission, PermissionModule, CreateRoleRequest } from '../../types';
import { roleApi, permissionApi } from '../../services/api';
import Button from '../common/Button';
import PermissionMatrix from './PermissionMatrix';
import { ToastContainer } from '../common/Toast';
import { useToast } from '../../hooks/useToast';

const CreateRole: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'general' | 'permissions'>('general');
  const [loading, setLoading] = useState(false);
  const [permissions, setPermissions] = useState<Permission[]>([]);
  const [permissionModules, setPermissionModules] = useState<PermissionModule[]>([]);
  
  const [formData, setFormData] = useState<CreateRoleRequest>({
    name: '',
    description: '',
    permissionIds: []
  });

  const { toasts, removeToast, success, error } = useToast();

  useEffect(() => {
    loadPermissions();
  }, []);

  const loadPermissions = async () => {
    try {
      const permissionsResponse = await permissionApi.getAllPermissions(0, 100);
      const groupedPermissions = await permissionApi.getPermissionsGroupedByModule();
      
      setPermissions(permissionsResponse.content);
      
      // Convert grouped permissions to PermissionModule format
      const modules: PermissionModule[] = Object.entries(groupedPermissions).map(([moduleName, modulePermissions]) => ({
        name: moduleName.toLowerCase().replace(/\s+/g, '_'),
        displayName: moduleName,
        permissions: modulePermissions.map(permission => ({
          permission,
          state: 'inherit' as const,
          inherited: false
        }))
      }));
      
      setPermissionModules(modules);
    } catch (err) {
      console.error('Failed to load permissions:', err);
      error('Failed to load permissions', 'Please try again later');
    }
  };

  const handleInputChange = (field: keyof CreateRoleRequest, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handlePermissionChange = (permissionId: number, state: 'allow' | 'deny' | 'inherit') => {
    setFormData(prev => {
      let newPermissionIds = [...prev.permissionIds];
      
      if (state === 'allow') {
        if (!newPermissionIds.includes(permissionId)) {
          newPermissionIds.push(permissionId);
        }
      } else {
        newPermissionIds = newPermissionIds.filter(id => id !== permissionId);
      }
      
      return {
        ...prev,
        permissionIds: newPermissionIds
      };
    });
    
    // Update permission modules state
    setPermissionModules(prev => 
      prev.map(module => ({
        ...module,
        permissions: module.permissions.map(perm => 
          perm.permission.id === permissionId
            ? { ...perm, state }
            : perm
        )
      }))
    );
  };

  const handleModuleChange = (moduleName: string, state: 'allow' | 'deny' | 'inherit') => {
    const module = permissionModules.find(m => m.name === moduleName);
    if (!module) return;
    
    module.permissions.forEach(perm => {
      handlePermissionChange(perm.permission.id, state);
    });
  };

  const handleGlobalPermissionChange = (state: 'allow' | 'deny' | 'inherit') => {
    permissionModules.forEach(module => {
      handleModuleChange(module.name, state);
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      error('Validation Error', 'Role name is required');
      return;
    }

    try {
      setLoading(true);
      await roleApi.createRole(formData);
      success('Role Created', 'The role has been successfully created');
      navigate('/roles');
    } catch (err) {
      console.error('Failed to create role:', err);
      error('Failed to create role', 'Please try again later');
    } finally {
      setLoading(false);
    }
  };

  const tabs = [
    { id: 'general', label: 'General' },
    { id: 'permissions', label: 'Permissions' }
  ];

  return (
    <div className="px-6">
      {/* Toast Container */}
      <ToastContainer toasts={toasts} onRemove={removeToast} />

      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/roles')}
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
          >
            <ArrowLeft className="h-5 w-5 text-gray-600" />
          </button>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Create Role</h1>
            <p className="text-gray-600 mt-1">Create a new role with specific permissions</p>
          </div>
        </div>
      </div>

      <div className="flex space-x-6">
        {/* Sidebar */}
        <div className="w-64 bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <h3 className="font-medium text-gray-900 mb-3">Role Information</h3>
          <ul className="space-y-1">
            {tabs.map((tab) => (
              <li key={tab.id}>
                <button
                  onClick={() => setActiveTab(tab.id as 'general' | 'permissions')}
                  className={`w-full text-left px-3 py-2 text-sm rounded-lg transition-colors ${
                    activeTab === tab.id
                      ? 'bg-blue-50 text-blue-700 border-r-2 border-blue-600'
                      : 'text-gray-700 hover:bg-gray-50'
                  }`}
                >
                  {tab.label}
                </button>
              </li>
            ))}
          </ul>
        </div>

        {/* Main Content */}
        <div className="flex-1">
          <div className="bg-white rounded-lg shadow-sm border border-gray-200">
            <form onSubmit={handleSubmit}>
              {activeTab === 'general' && (
                <div className="p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-6">General</h2>
                  
                  <div className="space-y-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Name <span className="text-red-500">*</span>
                      </label>
                      <input
                        type="text"
                        value={formData.name}
                        onChange={(e) => handleInputChange('name', e.target.value)}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Enter role name"
                        required
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Description
                      </label>
                      <textarea
                        value={formData.description || ''}
                        onChange={(e) => handleInputChange('description', e.target.value)}
                        rows={3}
                        className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        placeholder="Enter role description"
                      />
                    </div>
                    
                    <div className="flex justify-end">
                      <Button type="submit" loading={loading}>
                        <Save className="h-4 w-4 mr-2" />
                        Save
                      </Button>
                    </div>
                  </div>
                </div>
              )}

              {activeTab === 'permissions' && (
                <div className="p-6">
                  <div className="flex items-center justify-between mb-6">
                    <h2 className="text-lg font-semibold text-gray-900">Permissions</h2>
                    <div className="flex space-x-2">
                      <Button 
                        type="button" 
                        variant="secondary" 
                        size="sm"
                        onClick={() => handleGlobalPermissionChange('allow')}
                      >
                        Allow all
                      </Button>
                      <Button 
                        type="button" 
                        variant="secondary" 
                        size="sm"
                        onClick={() => handleGlobalPermissionChange('deny')}
                      >
                        Deny all
                      </Button>
                      <Button 
                        type="button" 
                        variant="secondary" 
                        size="sm"
                        onClick={() => handleGlobalPermissionChange('inherit')}
                      >
                        Inherit all
                      </Button>
                    </div>
                  </div>

                  <PermissionMatrix
                    modules={permissionModules}
                    onPermissionChange={handlePermissionChange}
                    onModuleChange={handleModuleChange}
                  />
                  
                  <div className="mt-6 flex justify-end">
                    <Button type="submit" loading={loading}>
                      <Save className="h-4 w-4 mr-2" />
                      Save
                    </Button>
                  </div>
                </div>
              )}
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateRole;