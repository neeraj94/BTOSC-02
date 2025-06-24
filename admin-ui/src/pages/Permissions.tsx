import React, { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { MagnifyingGlassIcon, KeyIcon } from '@heroicons/react/24/outline'
import { permissionsApi } from '../services/api'
import { Permission } from '../types'
import Pagination from '../components/Pagination'
import LoadingSpinner from '../components/LoadingSpinner'

const Permissions: React.FC = () => {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const [search, setSearch] = useState('')
  const [selectedModule, setSelectedModule] = useState('')

  // Fetch permissions
  const { data: permissionsData, isLoading } = useQuery({
    queryKey: ['permissions', { page, size, search, selectedModule }],
    queryFn: () => permissionsApi.getPermissions({ 
      page, 
      size, 
      name: search || undefined,
      module: selectedModule || undefined
    }),
  })

  // Fetch modules
  const { data: modulesData } = useQuery({
    queryKey: ['permission-modules'],
    queryFn: () => permissionsApi.getPermissionModules(),
  })

  // Fetch grouped permissions for overview
  const { data: groupedPermissions } = useQuery({
    queryKey: ['permissions-grouped'],
    queryFn: () => permissionsApi.getPermissionsGrouped(),
  })

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Permissions</h1>
          <p className="mt-1 text-sm text-gray-500">
            View and manage system permissions
          </p>
        </div>
      </div>

      {/* Overview Cards */}
      {groupedPermissions && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {Object.entries(groupedPermissions.data).map(([module, permissions]) => (
            <div key={module} className="card">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <div className="bg-primary-100 p-3 rounded-lg">
                    <KeyIcon className="h-6 w-6 text-primary-600" />
                  </div>
                </div>
                <div className="ml-4">
                  <h3 className="text-sm font-medium text-gray-900">{module}</h3>
                  <p className="text-2xl font-bold text-gray-900">{permissions.length}</p>
                  <p className="text-xs text-gray-500">permissions</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Filters */}
      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search permissions..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10 input-field"
          />
        </div>
        <select
          value={selectedModule}
          onChange={(e) => setSelectedModule(e.target.value)}
          className="input-field w-48"
        >
          <option value="">All Modules</option>
          {modulesData?.data.map((module) => (
            <option key={module} value={module}>
              {module}
            </option>
          ))}
        </select>
      </div>

      {/* Permissions Table */}
      <div className="card p-0">
        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <LoadingSpinner size="lg" />
          </div>
        ) : (
          <>
            <div className="table-container">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="table-header">Permission</th>
                    <th className="table-header">Key</th>
                    <th className="table-header">Description</th>
                    <th className="table-header">Module</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {permissionsData?.data.content.map((permission) => (
                    <tr key={permission.id} className="hover:bg-gray-50">
                      <td className="table-cell">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-10 w-10">
                            <div className="h-10 w-10 rounded-full bg-purple-100 flex items-center justify-center">
                              <KeyIcon className="h-6 w-6 text-purple-600" />
                            </div>
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">
                              {permission.name}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="table-cell">
                        <code className="text-sm bg-gray-100 px-2 py-1 rounded">
                          {permission.permissionKey}
                        </code>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {permission.description || 'No description'}
                        </div>
                      </td>
                      <td className="table-cell">
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                          {permission.module}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            
            {permissionsData && (
              <Pagination
                currentPage={page}
                totalPages={permissionsData.data.totalPages}
                totalElements={permissionsData.data.totalElements}
                pageSize={size}
                onPageChange={setPage}
                onPageSizeChange={(newSize) => {
                  setSize(newSize)
                  setPage(0)
                }}
              />
            )}
          </>
        )}
      </div>

      {/* Grouped Permissions View */}
      {groupedPermissions && (
        <div className="space-y-6">
          <h2 className="text-lg font-medium text-gray-900">Permissions by Module</h2>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {Object.entries(groupedPermissions.data).map(([module, permissions]) => (
              <div key={module} className="card">
                <h3 className="text-lg font-medium text-gray-900 mb-4">
                  {module} ({permissions.length})
                </h3>
                <div className="space-y-2 max-h-64 overflow-y-auto">
                  {permissions.map((permission) => (
                    <div key={permission.id} className="flex items-start space-x-3 p-2 hover:bg-gray-50 rounded">
                      <KeyIcon className="h-4 w-4 text-gray-400 mt-0.5 flex-shrink-0" />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-900">
                          {permission.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          {permission.permissionKey}
                        </p>
                        {permission.description && (
                          <p className="text-xs text-gray-600 mt-1">
                            {permission.description}
                          </p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default Permissions