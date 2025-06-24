import React, { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { 
  PlusIcon, 
  PencilIcon, 
  TrashIcon,
  MagnifyingGlassIcon,
  ArrowDownTrayIcon,
  UserIcon,
} from '@heroicons/react/24/outline'
import { usersApi, rolesApi, exportApi } from '../services/api'
import { User, CreateUserRequest, UpdateUserRequest } from '../types'
import Modal from '../components/Modal'
import Pagination from '../components/Pagination'
import LoadingSpinner from '../components/LoadingSpinner'
import { toast } from 'react-toastify'

const Users: React.FC = () => {
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(20)
  const [search, setSearch] = useState('')
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
  const [isEditModalOpen, setIsEditModalOpen] = useState(false)
  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  
  const queryClient = useQueryClient()

  // Fetch users
  const { data: usersData, isLoading } = useQuery({
    queryKey: ['users', { page, size, search }],
    queryFn: () => usersApi.getUsers({ 
      page, 
      size, 
      username: search || undefined,
      email: search || undefined 
    }),
  })

  // Fetch roles for dropdowns
  const { data: rolesData } = useQuery({
    queryKey: ['roles-all'],
    queryFn: () => rolesApi.getRoles({ page: 0, size: 1000 }),
  })

  // Create user mutation
  const createUserMutation = useMutation({
    mutationFn: usersApi.createUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      setIsCreateModalOpen(false)
      toast.success('User created successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to create user')
    },
  })

  // Update user mutation
  const updateUserMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateUserRequest }) =>
      usersApi.updateUser(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      setIsEditModalOpen(false)
      setSelectedUser(null)
      toast.success('User updated successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update user')
    },
  })

  // Delete user mutation
  const deleteUserMutation = useMutation({
    mutationFn: usersApi.deleteUser,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('User deleted successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to delete user')
    },
  })

  // Toggle user activation mutation
  const toggleActivationMutation = useMutation({
    mutationFn: usersApi.toggleUserActivation,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['users'] })
      toast.success('User status updated successfully')
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update user status')
    },
  })

  const handleExport = async (format: 'excel' | 'csv') => {
    try {
      const response = format === 'excel' 
        ? await exportApi.exportUsersExcel()
        : await exportApi.exportUsersCSV()
      
      const blob = new Blob([response.data])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `users.${format === 'excel' ? 'xlsx' : 'csv'}`
      link.click()
      window.URL.revokeObjectURL(url)
      
      toast.success(`Users exported to ${format.toUpperCase()} successfully`)
    } catch (error) {
      toast.error(`Failed to export users to ${format.toUpperCase()}`)
    }
  }

  const handleDelete = (user: User) => {
    if (window.confirm(`Are you sure you want to delete user "${user.username}"?`)) {
      deleteUserMutation.mutate(user.id)
    }
  }

  const handleEdit = (user: User) => {
    setSelectedUser(user)
    setIsEditModalOpen(true)
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Users</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage user accounts and their roles
          </p>
        </div>
        <div className="flex space-x-3">
          <div className="relative">
            <button
              onClick={() => handleExport('excel')}
              className="btn-secondary flex items-center"
            >
              <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
              Export Excel
            </button>
          </div>
          <div className="relative">
            <button
              onClick={() => handleExport('csv')}
              className="btn-secondary flex items-center"
            >
              <ArrowDownTrayIcon className="h-4 w-4 mr-2" />
              Export CSV
            </button>
          </div>
          <button
            onClick={() => setIsCreateModalOpen(true)}
            className="btn-primary flex items-center"
          >
            <PlusIcon className="h-4 w-4 mr-2" />
            Add User
          </button>
        </div>
      </div>

      {/* Search */}
      <div className="flex items-center space-x-4">
        <div className="relative flex-1 max-w-md">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
          <input
            type="text"
            placeholder="Search users..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10 input-field"
          />
        </div>
      </div>

      {/* Users Table */}
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
                    <th className="table-header">User</th>
                    <th className="table-header">Email</th>
                    <th className="table-header">Roles</th>
                    <th className="table-header">Status</th>
                    <th className="table-header">Created</th>
                    <th className="table-header">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {usersData?.data.content.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="table-cell">
                        <div className="flex items-center">
                          <div className="flex-shrink-0 h-10 w-10">
                            <div className="h-10 w-10 rounded-full bg-gray-300 flex items-center justify-center">
                              <UserIcon className="h-6 w-6 text-gray-600" />
                            </div>
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-medium text-gray-900">
                              {user.firstName} {user.lastName}
                            </div>
                            <div className="text-sm text-gray-500">
                              @{user.username}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">{user.email}</div>
                        <div className="text-sm text-gray-500">
                          {user.emailVerified ? (
                            <span className="text-green-600">Verified</span>
                          ) : (
                            <span className="text-red-600">Not verified</span>
                          )}
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="flex flex-wrap gap-1">
                          {user.roles.map((role) => (
                            <span
                              key={role}
                              className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                            >
                              {role}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="table-cell">
                        <button
                          onClick={() => toggleActivationMutation.mutate(user.id)}
                          className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                            user.isActive
                              ? 'bg-green-100 text-green-800 hover:bg-green-200'
                              : 'bg-red-100 text-red-800 hover:bg-red-200'
                          }`}
                        >
                          {user.isActive ? 'Active' : 'Inactive'}
                        </button>
                      </td>
                      <td className="table-cell">
                        <div className="text-sm text-gray-900">
                          {new Date(user.createdAt).toLocaleDateString()}
                        </div>
                      </td>
                      <td className="table-cell">
                        <div className="flex space-x-2">
                          <button
                            onClick={() => handleEdit(user)}
                            className="text-primary-600 hover:text-primary-900"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(user)}
                            className="text-red-600 hover:text-red-900"
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
            
            {usersData && (
              <Pagination
                currentPage={page}
                totalPages={usersData.data.totalPages}
                totalElements={usersData.data.totalElements}
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

      {/* Create User Modal */}
      <CreateUserModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={(data) => createUserMutation.mutate(data)}
        roles={rolesData?.data.content || []}
        loading={createUserMutation.isPending}
      />

      {/* Edit User Modal */}
      {selectedUser && (
        <EditUserModal
          isOpen={isEditModalOpen}
          onClose={() => {
            setIsEditModalOpen(false)
            setSelectedUser(null)
          }}
          onSubmit={(data) => updateUserMutation.mutate({ id: selectedUser.id, data })}
          user={selectedUser}
          roles={rolesData?.data.content || []}
          loading={updateUserMutation.isPending}
        />
      )}
    </div>
  )
}

// Create User Modal Component
interface CreateUserModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: CreateUserRequest) => void
  roles: any[]
  loading: boolean
}

const CreateUserModal: React.FC<CreateUserModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  roles,
  loading,
}) => {
  const { register, handleSubmit, formState: { errors }, reset } = useForm<CreateUserRequest>()

  const handleFormSubmit = (data: CreateUserRequest) => {
    onSubmit(data)
    reset()
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Create New User" maxWidth="lg">
      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Username</label>
            <input
              {...register('username', { required: 'Username is required' })}
              type="text"
              className="mt-1 input-field"
            />
            {errors.username && (
              <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input
              {...register('email', { 
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address'
                }
              })}
              type="email"
              className="mt-1 input-field"
            />
            {errors.email && (
              <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
            )}
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Password</label>
          <input
            {...register('password', { 
              required: 'Password is required',
              minLength: {
                value: 6,
                message: 'Password must be at least 6 characters'
              }
            })}
            type="password"
            className="mt-1 input-field"
          />
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>
          )}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">First Name</label>
            <input
              {...register('firstName')}
              type="text"
              className="mt-1 input-field"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Last Name</label>
            <input
              {...register('lastName')}
              type="text"
              className="mt-1 input-field"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Roles</label>
          <select
            {...register('roleIds')}
            multiple
            className="mt-1 input-field"
            size={4}
          >
            {roles.map((role) => (
              <option key={role.id} value={role.id}>
                {role.name}
              </option>
            ))}
          </select>
          <p className="mt-1 text-sm text-gray-500">Hold Ctrl/Cmd to select multiple roles</p>
        </div>

        <div className="flex items-center">
          <input
            {...register('isActive')}
            type="checkbox"
            defaultChecked
            className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label className="ml-2 block text-sm text-gray-900">
            Active user
          </label>
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
            Create User
          </button>
        </div>
      </form>
    </Modal>
  )
}

// Edit User Modal Component
interface EditUserModalProps {
  isOpen: boolean
  onClose: () => void
  onSubmit: (data: UpdateUserRequest) => void
  user: User
  roles: any[]
  loading: boolean
}

const EditUserModal: React.FC<EditUserModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  user,
  roles,
  loading,
}) => {
  const { register, handleSubmit, formState: { errors } } = useForm<UpdateUserRequest>({
    defaultValues: {
      username: user.username,
      email: user.email,
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      isActive: user.isActive,
    }
  })

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Edit User" maxWidth="lg">
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Username</label>
            <input
              {...register('username', { required: 'Username is required' })}
              type="text"
              className="mt-1 input-field"
            />
            {errors.username && (
              <p className="mt-1 text-sm text-red-600">{errors.username.message}</p>
            )}
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Email</label>
            <input
              {...register('email', { 
                required: 'Email is required',
                pattern: {
                  value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                  message: 'Invalid email address'
                }
              })}
              type="email"
              className="mt-1 input-field"
            />
            {errors.email && (
              <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
            )}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">First Name</label>
            <input
              {...register('firstName')}
              type="text"
              className="mt-1 input-field"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Last Name</label>
            <input
              {...register('lastName')}
              type="text"
              className="mt-1 input-field"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Roles</label>
          <select
            {...register('roleIds')}
            multiple
            className="mt-1 input-field"
            size={4}
          >
            {roles.map((role) => (
              <option key={role.id} value={role.id}>
                {role.name}
              </option>
            ))}
          </select>
          <p className="mt-1 text-sm text-gray-500">Hold Ctrl/Cmd to select multiple roles</p>
        </div>

        <div className="flex items-center">
          <input
            {...register('isActive')}
            type="checkbox"
            className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
          />
          <label className="ml-2 block text-sm text-gray-900">
            Active user
          </label>
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
            Update User
          </button>
        </div>
      </form>
    </Modal>
  )
}

export default Users