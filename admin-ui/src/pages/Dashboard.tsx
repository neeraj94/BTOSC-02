import React from 'react'
import { useQuery } from '@tanstack/react-query'
import { usersApi, rolesApi, permissionsApi } from '../services/api'
import { 
  UsersIcon, 
  ShieldCheckIcon, 
  KeyIcon,
  ChartBarIcon 
} from '@heroicons/react/24/outline'
import LoadingSpinner from '../components/LoadingSpinner'

const Dashboard: React.FC = () => {
  const { data: usersData, isLoading: usersLoading } = useQuery({
    queryKey: ['users', { page: 0, size: 1 }],
    queryFn: () => usersApi.getUsers({ page: 0, size: 1 }),
  })

  const { data: rolesData, isLoading: rolesLoading } = useQuery({
    queryKey: ['roles', { page: 0, size: 1 }],
    queryFn: () => rolesApi.getRoles({ page: 0, size: 1 }),
  })

  const { data: permissionsData, isLoading: permissionsLoading } = useQuery({
    queryKey: ['permissions', { page: 0, size: 1 }],
    queryFn: () => permissionsApi.getPermissions({ page: 0, size: 1 }),
  })

  const stats = [
    {
      name: 'Total Users',
      value: usersData?.data.totalElements || 0,
      icon: UsersIcon,
      color: 'bg-blue-500',
      loading: usersLoading,
    },
    {
      name: 'Total Roles',
      value: rolesData?.data.totalElements || 0,
      icon: ShieldCheckIcon,
      color: 'bg-green-500',
      loading: rolesLoading,
    },
    {
      name: 'Total Permissions',
      value: permissionsData?.data.totalElements || 0,
      icon: KeyIcon,
      color: 'bg-purple-500',
      loading: permissionsLoading,
    },
    {
      name: 'Active Sessions',
      value: 1,
      icon: ChartBarIcon,
      color: 'bg-orange-500',
      loading: false,
    },
  ]

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Welcome to the RBAC Admin Panel. Here's an overview of your system.
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <div key={stat.name} className="card">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <stat.icon className="h-6 w-6 text-white" />
                </div>
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    {stat.name}
                  </dt>
                  <dd className="text-lg font-medium text-gray-900">
                    {stat.loading ? (
                      <LoadingSpinner size="sm" />
                    ) : (
                      stat.value.toLocaleString()
                    )}
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">System Overview</h3>
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">System Status</span>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Online
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Database</span>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Connected
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Authentication</span>
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                Active
              </span>
            </div>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
          <div className="space-y-3">
            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md border border-gray-200">
              Create New User
            </button>
            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md border border-gray-200">
              Create New Role
            </button>
            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md border border-gray-200">
              View System Logs
            </button>
            <button className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md border border-gray-200">
              Export Data
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard