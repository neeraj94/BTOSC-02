import React, { useState, useEffect } from 'react';
import { Plus, Search, Download, Trash2, Edit, Copy, Eye } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Role, PaginatedResponse } from '../../types';
import { roleApi } from '../../services/api';
import Table from '../common/Table';
import Button from '../common/Button';
import Pagination from '../common/Pagination';
import ConfirmDialog from '../common/ConfirmDialog';
import Dropdown from '../common/Dropdown';
import { ToastContainer } from '../common/Toast';
import { useToast } from '../../hooks/useToast';

const RolesList: React.FC = () => {
  const [roles, setRoles] = useState<PaginatedResponse<Role>>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size: 20,
    first: true,
    last: true
  });
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('id');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [selectedRoles, setSelectedRoles] = useState<number[]>([]);
  const [deleteConfirm, setDeleteConfirm] = useState<{ isOpen: boolean; roleId?: number; roleName?: string }>({
    isOpen: false
  });
  const [bulkDeleteConfirm, setBulkDeleteConfirm] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const { toasts, removeToast, success, error } = useToast();

  useEffect(() => {
    loadRoles();
  }, [roles.number, roles.size, sortBy, sortDirection]);

  const loadRoles = async () => {
    try {
      setLoading(true);
      const response = await roleApi.getAllRoles(roles.number, roles.size, sortBy, sortDirection);
      setRoles(response);
    } catch (err) {
      console.error('Failed to load roles:', err);
      error('Failed to load roles', 'Please try again later');
    } finally {
      setLoading(false);
    }
  };

  const handleSort = (key: string) => {
    if (sortBy === key) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(key);
      setSortDirection('asc');
    }
  };

  const handlePageChange = (page: number) => {
    setRoles(prev => ({ ...prev, number: page }));
  };

  const handlePageSizeChange = (size: number) => {
    setRoles(prev => ({ ...prev, size, number: 0 }));
  };

  const handleSelectRole = (roleId: number) => {
    setSelectedRoles(prev => 
      prev.includes(roleId) 
        ? prev.filter(id => id !== roleId)
        : [...prev, roleId]
    );
  };

  const handleSelectAll = () => {
    if (selectedRoles.length === roles.content.length) {
      setSelectedRoles([]);
    } else {
      setSelectedRoles(roles.content.map(role => role.id));
    }
  };

  const handleDeleteRole = async (roleId: number) => {
    try {
      setDeleteLoading(true);
      await roleApi.deleteRole(roleId);
      success('Role deleted', 'The role has been successfully deleted');
      loadRoles();
      setDeleteConfirm({ isOpen: false });
    } catch (err) {
      console.error('Failed to delete role:', err);
      error('Failed to delete role', 'Please try again later');
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleBulkDelete = async () => {
    try {
      setDeleteLoading(true);
      await Promise.all(selectedRoles.map(id => roleApi.deleteRole(id)));
      success('Roles deleted', `${selectedRoles.length} roles have been successfully deleted`);
      setSelectedRoles([]);
      setBulkDeleteConfirm(false);
      loadRoles();
    } catch (err) {
      console.error('Failed to delete roles:', err);
      error('Failed to delete roles', 'Please try again later');
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleDuplicateRole = (role: Role) => {
    // This would typically navigate to create role with pre-filled data
    success('Feature coming soon', 'Role duplication will be available in the next update');
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) return '1 day ago';
    if (diffDays < 30) return `${diffDays} days ago`;
    if (diffDays < 365) {
      const months = Math.floor(diffDays / 30);
      return months === 1 ? '1 month ago' : `${months} months ago`;
    }
    const years = Math.floor(diffDays / 365);
    return years === 1 ? '1 year ago' : `${years} years ago`;
  };

  const filteredRoles = roles.content.filter(role =>
    role.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (role.description && role.description.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const columns = [
    {
      key: 'select',
      label: '',
      width: '50px',
      render: (_: any, role: Role) => (
        <input
          type="checkbox"
          checked={selectedRoles.includes(role.id)}
          onChange={() => handleSelectRole(role.id)}
          className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
        />
      )
    },
    {
      key: 'id',
      label: 'ID',
      sortable: true,
      width: '80px'
    },
    {
      key: 'name',
      label: 'Name',
      sortable: true,
      render: (value: string, role: Role) => (
        <div className="flex items-center">
          <Link
            to={`/roles/${role.id}/edit`}
            className="font-medium text-blue-600 hover:text-blue-800 transition-colors"
          >
            {value}
          </Link>
          {role.isSystemRole && (
            <span className="ml-2 inline-flex items-center px-2 py-0.5 rounded text-xs font-medium bg-red-100 text-red-800">
              System
            </span>
          )}
        </div>
      )
    },
    {
      key: 'description',
      label: 'Description',
      render: (value: string) => (
        <span className="text-gray-600 max-w-xs truncate block">
          {value || 'No description'}
        </span>
      )
    },
    {
      key: 'permissions',
      label: 'Permissions',
      render: (_: any, role: Role) => (
        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
          {role.permissions.length} permissions
        </span>
      )
    },
    {
      key: 'createdAt',
      label: 'Created',
      sortable: true,
      render: (value: string) => (
        <span className="text-gray-500 text-sm">
          {formatDate(value)}
        </span>
      )
    },
    {
      key: 'actions',
      label: '',
      width: '120px',
      render: (_: any, role: Role) => (
        <Dropdown
          items={[
            {
              label: 'View Details',
              icon: Eye,
              onClick: () => window.open(`/roles/${role.id}/edit`, '_blank')
            },
            {
              label: 'Edit Role',
              icon: Edit,
              onClick: () => window.location.href = `/roles/${role.id}/edit`
            },
            {
              label: 'Duplicate',
              icon: Copy,
              onClick: () => handleDuplicateRole(role),
              disabled: role.isSystemRole
            },
            {
              label: 'Delete',
              icon: Trash2,
              variant: 'danger',
              onClick: () => setDeleteConfirm({ 
                isOpen: true, 
                roleId: role.id, 
                roleName: role.name 
              }),
              disabled: role.isSystemRole
            }
          ]}
        />
      )
    }
  ];

  return (
    <div className="px-6">
      {/* Toast Container */}
      <ToastContainer toasts={toasts} onRemove={removeToast} />

      {/* Header */}
      <div className="mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Roles</h1>
            <p className="text-gray-600 mt-1">Manage user roles and permissions</p>
          </div>
          <Link to="/roles/create">
            <Button>
              <Plus className="h-4 w-4 mr-2" />
              Create Role
            </Button>
          </Link>
        </div>
      </div>

      {/* Controls */}
      <div className="mb-6 flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder="Search roles..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 w-64"
            />
          </div>
          
          {selectedRoles.length > 0 && (
            <div className="flex items-center space-x-2">
              <span className="text-sm text-gray-600">
                {selectedRoles.length} selected
              </span>
              <Button
                variant="danger"
                size="sm"
                onClick={() => setBulkDeleteConfirm(true)}
              >
                <Trash2 className="h-4 w-4 mr-1" />
                Delete
              </Button>
            </div>
          )}
        </div>

        <Button variant="secondary" size="sm">
          <Download className="h-4 w-4 mr-2" />
          Export
        </Button>
      </div>

      {/* Table */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="px-6 py-3 border-b border-gray-200 bg-gray-50">
          <div className="flex items-center">
            <input
              type="checkbox"
              checked={selectedRoles.length === roles.content.length && roles.content.length > 0}
              onChange={handleSelectAll}
              className="rounded border-gray-300 text-blue-600 focus:ring-blue-500 mr-3"
            />
            <span className="text-sm font-medium text-gray-700">
              Show {roles.size} entries
            </span>
          </div>
        </div>

        <Table
          data={filteredRoles}
          columns={columns}
          sortBy={sortBy}
          sortDirection={sortDirection}
          onSort={handleSort}
          loading={loading}
          emptyMessage="No roles found"
        />

        {!loading && roles.totalElements > 0 && (
          <Pagination
            currentPage={roles.number}
            totalPages={roles.totalPages}
            totalElements={roles.totalElements}
            pageSize={roles.size}
            onPageChange={handlePageChange}
            onSizeChange={handlePageSizeChange}
          />
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={deleteConfirm.isOpen}
        onClose={() => setDeleteConfirm({ isOpen: false })}
        onConfirm={() => deleteConfirm.roleId && handleDeleteRole(deleteConfirm.roleId)}
        title="Delete Role"
        message={`Are you sure you want to delete the role "${deleteConfirm.roleName}"? This action cannot be undone.`}
        confirmText="Delete"
        cancelText="Cancel"
        variant="danger"
        loading={deleteLoading}
      />

      {/* Bulk Delete Confirmation Dialog */}
      <ConfirmDialog
        isOpen={bulkDeleteConfirm}
        onClose={() => setBulkDeleteConfirm(false)}
        onConfirm={handleBulkDelete}
        title="Delete Multiple Roles"
        message={`Are you sure you want to delete ${selectedRoles.length} role(s)? This action cannot be undone.`}
        confirmText="Delete All"
        cancelText="Cancel"
        variant="danger"
        loading={deleteLoading}
      />
    </div>
  );
};

export default RolesList;