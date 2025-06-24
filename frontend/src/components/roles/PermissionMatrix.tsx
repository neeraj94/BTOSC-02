import React from 'react';
import { PermissionModule } from '../../types';

interface PermissionMatrixProps {
  modules: PermissionModule[];
  onPermissionChange: (permissionId: number, state: 'allow' | 'deny' | 'inherit') => void;
  onModuleChange: (moduleName: string, state: 'allow' | 'deny' | 'inherit') => void;
}

const PermissionMatrix: React.FC<PermissionMatrixProps> = ({
  modules,
  onPermissionChange,
  onModuleChange
}) => {
  const getModuleState = (module: PermissionModule): 'allow' | 'deny' | 'inherit' | 'mixed' => {
    const states = module.permissions.map(p => p.state);
    const allowCount = states.filter(s => s === 'allow').length;
    const denyCount = states.filter(s => s === 'deny').length;
    const inheritCount = states.filter(s => s === 'inherit').length;
    
    if (allowCount === states.length) return 'allow';
    if (denyCount === states.length) return 'deny';
    if (inheritCount === states.length) return 'inherit';
    return 'mixed';
  };

  return (
    <div className="space-y-6">
      {modules.map((module) => {
        const moduleState = getModuleState(module);
        
        return (
          <div key={module.name} className="border border-gray-200 rounded-lg overflow-hidden">
            {/* Module Header */}
            <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-medium text-gray-900">{module.displayName}</h3>
                <div className="flex space-x-2">
                  <button
                    type="button"
                    onClick={() => onModuleChange(module.name, 'allow')}
                    className="text-xs px-2 py-1 rounded text-blue-600 hover:bg-blue-50 transition-colors"
                  >
                    Allow all
                  </button>
                  <button
                    type="button"
                    onClick={() => onModuleChange(module.name, 'deny')}
                    className="text-xs px-2 py-1 rounded text-red-600 hover:bg-red-50 transition-colors"
                  >
                    Deny all
                  </button>
                  <button
                    type="button"
                    onClick={() => onModuleChange(module.name, 'inherit')}
                    className="text-xs px-2 py-1 rounded text-gray-600 hover:bg-gray-50 transition-colors"
                  >
                    Inherit all
                  </button>
                </div>
              </div>
            </div>

            {/* Permissions List */}
            <div className="divide-y divide-gray-200">
              {module.permissions.map((permissionState) => (
                <div key={permissionState.permission.id} className="px-4 py-3 hover:bg-gray-25 transition-colors">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="text-sm font-medium text-gray-900 mb-1">
                        {permissionState.permission.description}
                      </div>
                      <div className="text-xs text-gray-500">
                        {permissionState.permission.name}
                      </div>
                    </div>
                    
                    <div className="flex items-center space-x-4 ml-4">
                      <label className="flex items-center">
                        <input
                          type="radio"
                          name={`permission-${permissionState.permission.id}`}
                          value="allow"
                          checked={permissionState.state === 'allow'}
                          onChange={() => onPermissionChange(permissionState.permission.id, 'allow')}
                          className="form-radio h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300"
                        />
                        <span className="ml-2 text-sm text-gray-700">Allow</span>
                      </label>
                      
                      <label className="flex items-center">
                        <input
                          type="radio"
                          name={`permission-${permissionState.permission.id}`}
                          value="deny"
                          checked={permissionState.state === 'deny'}
                          onChange={() => onPermissionChange(permissionState.permission.id, 'deny')}
                          className="form-radio h-4 w-4 text-red-600 focus:ring-red-500 border-gray-300"
                        />
                        <span className="ml-2 text-sm text-gray-700">Deny</span>
                      </label>
                      
                      <label className="flex items-center">
                        <input
                          type="radio"
                          name={`permission-${permissionState.permission.id}`}
                          value="inherit"
                          checked={permissionState.state === 'inherit'}
                          onChange={() => onPermissionChange(permissionState.permission.id, 'inherit')}
                          className="form-radio h-4 w-4 text-gray-600 focus:ring-gray-500 border-gray-300"
                        />
                        <span className="ml-2 text-sm text-gray-700">Inherit</span>
                      </label>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default PermissionMatrix;