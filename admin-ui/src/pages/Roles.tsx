import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { 
  PlusIcon, 
  PencilIcon, 
  TrashIcon,
  MagnifyingGlassIcon,
  ArrowDownTrayIcon,
  ShieldCheckIcon,
} from '@heroicons/react/24/outline'
import { rolesApi, permissionsApi, exportApi } from '../services/api'
import { Role, CreateRoleRequest, Permission } from '../types'
import Modal from '../components/Modal'
import Pagination from '../components/Pagination'
import LoadingSpinner from '../components/LoadingSpinner'
import { toast } from 'react-toastify'

const Roles: React.FC = () => {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const [search, setSearch] = useState('')
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [selectedRole, setSelectedRole] = useState<Role | null>(null)
  
  const queryClient = useQueryClient()

  // Fetch roles
  const { data: rolesData, isLoading } = useQuery({
    queryKey: ['roles', { page, size, search }],
    queryFn: () => rolesApi.getRoles({ 
      page, 
      size, 
      name: search || undefined 
    }),
  })

  // Fetch permissions for dropdowns
  const { data: permissionsData } = useQuery({
    queryKey: ['permissions-grouped'],
    queryFn: () => permissionsApi.getPermissionsGrouped(),
  })

  // Create role mutation
  const createRoleMutation = useMutation({
    mutationFn: rolesApi.createRole,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles'] })
      setIsCreateModalOpen(false)
      toast.success('Role created successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create role')
    },
  })

  // Update role mutation
  const updateRoleMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: CreateRoleRequest }) =>
      rolesApi.updateRole(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles'] })
      setIsEditModalOpen(false)
      setSelectedRole(null)
      toast.success('Role updated successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update role')
    },
  })

  // Delete role mutation
  const deleteRoleMutation = useMutation({
    mutationFn: rolesApi.deleteRole,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['roles'] })
      toast.success('Role deleted successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete role')
    },
  })

  const handleExport = async (format: 'excel' | 'csv') => {
    try {
      const response = format === 'excel' 
        ? await exportApi.exportRolesExcel()
        : await exportApi.exportRolesCSV()
      
      const blob = new Blob([response.data])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `roles.${format === 'excel' ? 'xlsx' : 'csv'}`
      link.click()
      window.URL.revokeObjectURL(url)
      
      toast.success(`Roles exported to ${format.toUpperCase()} successfully`)
    } catch (error) {
      toast.error(`Failed to export roles to ${format.toUpperCase()}`)
    }
  }

  const handleDelete = (role: Role) => {
    if (role.isSystemRole) {
      toast.error('Cannot delete system roles')
      return
    }
    
    if (window.confirm(`Are you sure you want to delete role "${role.name}"?`)) {
      deleteRoleMutation.mutate(role.id)
    }
  }

  const handleEdit = (role: Role) => {
    if (role.isSystemRole) {
      toast.error('Cannot edit system roles')
      return
    }
    
    setSelectedRole(role)
    setIsEditModalOpen(true)
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Roles</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage roles and their permissions
          </p>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => handleExport('excel')}
            className="btn-secondary flex items-center"
          >
            <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
            Export Excel
          </button>
          <button
            onClick={() => handleExport('csv')}
            className="btn-secondary flex items-center"
          >
            <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
            Export CSV
          </button>
          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="btn-primary flex items-center"
          >
            <PlusIcon className="h-4 w-4 mr-2" />
            Add Role
          </button>
        </div>
      </div>

      {/* Search */}
      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search roles..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10 input-field"
          />
        </div>
      </div>

      {/* Roles Table */}
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
                    <th className="table-header">Role</th>
                    <th className="table-header">Description</th>
                    <th className="table-header">Permissions</th>
                    <th className="table-header">Users</th>
                    <th className="table-header">Type</th>
                    <th className="table-header">Created</th>
                    <th className="table-header">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {rolesData?.data.content.map((role) => (
                    <tr key={role.id} className="hover:bg-gray-50">
                      <td className="table-cell">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-10 w-10">
                            <div className="h-10 w-10 rounded-full bg-primary-100 flex items-center justify-center">
                              <ShieldCheckIcon className="h-6 w-6 text-primary-600" />
                            </div>
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">
                              {role.name}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {role.description || 'No description'}
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {role.permissions.length} permissions
                        </div>
                        <div className="text-sm text-gray-500">
                          {role.permissions.slice(0, 3).join(', ')}
                          {role.permissions.length > 3 && '...'}
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {role.userCount} users
                        </div>
                      </td>
                      <td className="table-cell">
                        <span
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                            role.isSystemRole
                              ? 'bg-red-100 text-red-800'
                              : 'bg-green-100 text-green-800'
                          }`}
                        >
                          {role.isSystemRole ? 'System' : 'Custom'}
                        </span>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {new Date(role.createdAt).toLocaleDateString()}
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="flex space-x-2">
                          <button
                            onClick={() => handleEdit(role)}
                            disabled={role.isSystemRole}
                            className={`${
                              role.isSystemRole
                                ? 'text-gray-400 cursor-not-allowed'
                                : 'text-primary-600 hover:text-primary-900'
                            }`}
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(role)}
                            disabled={role.isSystemRole}
                            className={`${
                              role.isSystemRole
                                ? 'text-gray-400 cursor-not-allowed'
                                : 'text-red-600 hover:text-red-900'
                            }`}
                          >
                            <TrashIcon className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            
            {rolesData && (
              <Pagination
                currentPage={page}
                totalPages={rolesData.data.totalPages}
                totalElements={rolesData.data.totalElements}
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

      {/* Create Role Modal */}
      <RoleModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={(data) => createRoleMutation.mutate(data)}
        permissions={permissionsData?.data || {}}
        loading={createRoleMutation.isPending}
        title="Create New Role"
      />

      {/* Edit Role Modal */}
      {selectedRole && (
        <RoleModal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false)
            setSelectedRole(null)
          }}
          onSubmit={(data) => updateRoleMutation.mutate({ id: selectedRole.id, data })}
          permissions={permissionsData?.data || {}}
          loading={updateRoleMutation.isPending}
          title="Edit Role"
          initialData={selectedRole}
        />
      )}
    </div>
  )
}

// Role Modal Component
interface RoleModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: CreateRoleRequest) => void
  permissions: Record<string, Permission[]>
  loading: boolean
  title: string
  initialData?: Role
}

const RoleModal: React.FC<RoleModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  permissions,
  loading,
  title,
  initialData,
}) => {
  const [selectedPermissions, setSelectedPermissions] = useState<Set<number>>(
    new Set(initialData ? [] : []) // We'll need to map permission keys to IDs
  )

  const { register, handleSubmit, formState: { errors }, reset } = useForm<CreateRoleRequest>({
    defaultValues: initialData ? {
      name: initialData.name,
      description: initialData.description || '',
    } : undefined
  })

  const handleFormSubmit = (data: CreateRoleRequest) => {
    const formData = {
      ...data,
      permissionIds: Array.from(selectedPermissions)
    }
    onSubmit(formData)
    reset()
    setSelectedPermissions(new Set())
  }

  const togglePermission = (permissionId: number) => {
    const newSelected = new Set(selectedPermissions)
    if (newSelected.has(permissionId)) {
      newSelected.delete(permissionId)
    } else {
      newSelected.add(permissionId)
    }
    setSelectedPermissions(newSelected)
  }

  const toggleModule = (modulePermissions: Permission[]) => {
    const moduleIds = modulePermissions.map(p => p.id)
    const allSelected = moduleIds.every(id => selectedPermissions.has(id))
    
    const newSelected = new Set(selectedPermissions)
    if (allSelected) {
      moduleIds.forEach(id => newSelected.delete(id))
    } else {
      moduleIds.forEach(id => newSelected.add(id))
    }
    setSelectedPermissions(newSelected)
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} maxWidth="2xl">
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Role Name</label>
            <input
              {...register('name', { required: 'Role name is required' })}
              type="text"
              className="mt-1 input-field"
            />
            {errors.name && (
              <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Description</label>
            <input
              {...register('description')}
              type="text"
              className="mt-1 input-field"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-3">Permissions</label>
          <div className="max-h-96 overflow-y-auto border border-gray-200 rounded-lg">
            {Object.entries(permissions).map(([module, modulePermissions]) => (
              <div key={module} className="border-b border-gray-200 last:border-b-0">
                <div className="bg-gray-50 px-4 py-3">
                  <div className="flex items-center">
                    <input
                      type="checkbox"
                      checked={modulePermissions.every(p => selectedPermissions.has(p.id))}
                      onChange={() => toggleModule(modulePermissions)}
                      className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                    />
                    <label className="ml-3 text-sm font-medium text-gray-900">
                      {module} ({modulePermissions.length})
                    </label>
                  </div>
                </div>
                <div className="px-4 py-2 space-y-2">
                  {modulePermissions.map((permission) => (
                    <div key={permission.id} className="flex items-center">
                      <input
                        type="checkbox"
                        checked={selectedPermissions.has(permission.id)}
                        onChange={() => togglePermission(permission.id)}
                        className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                      />
                      <div className="ml-3">
                        <label className="text-sm text-gray-900">
                          {permission.name}
                        </label>
                        <p className="text-xs text-gray-500">
                          {permission.description}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
          <p className="mt-2 text-sm text-gray-500">
            Selected: {selectedPermissions.size} permissions
          </p>
        </div>

        <div className="flex justify-end space-x-3 pt-4">
          <button
            type="button"
            onClick={onClose}
            className="btn-secondary"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={loading}
            className="btn-primary flex items-center"
          >
            {loading && <LoadingSpinner size="sm" className="mr-2" />}
            {initialData ? 'Update Role' : 'Create Role'}
          </button>
        </div>
      </form>
    </Modal>
  )
}

export default Roles