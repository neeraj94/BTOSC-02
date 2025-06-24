package com.rbac.service;

import com.rbac.dto.role.CreateRoleRequest;
import com.rbac.dto.role.RoleResponse;
import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.repository.PermissionRepository;
import com.rbac.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public RoleResponse createRole(CreateRoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role name already exists!");
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setIsSystemRole(false);

        // Assign permissions
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role savedRole = roleRepository.save(role);
        return new RoleResponse(savedRole);
    }

    public RoleResponse updateRole(Long roleId, CreateRoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getIsSystemRole()) {
            throw new RuntimeException("Cannot modify system roles");
        }

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new RuntimeException("Role name already exists!");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Update permissions
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (Long permissionId : request.getPermissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
                permissions.add(permission);
            }
            role.setPermissions(permissions);
        }

        Role updatedRole = roleRepository.save(role);
        return new RoleResponse(updatedRole);
    }

    public RoleResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return new RoleResponse(role);
    }

    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable)
                .map(RoleResponse::new);
    }

    public Page<RoleResponse> getRolesWithFilters(String name, String description, Pageable pageable) {
        return roleRepository.findWithFilters(name, description, pageable)
                .map(RoleResponse::new);
    }

    public List<RoleResponse> getNonSystemRoles() {
        return roleRepository.findByIsSystemRole(false, Pageable.unpaged())
                .stream()
                .map(RoleResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getIsSystemRole()) {
            throw new RuntimeException("Cannot delete system roles");
        }

        if (!role.getUsers().isEmpty()) {
            throw new RuntimeException("Cannot delete role that is assigned to users");
        }

        roleRepository.delete(role);
    }

    public RoleResponse addPermissionsToRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> newPermissions = new HashSet<>(role.getPermissions());
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
            newPermissions.add(permission);
        }

        role.setPermissions(newPermissions);
        Role updatedRole = roleRepository.save(role);
        return new RoleResponse(updatedRole);
    }

    public RoleResponse removePermissionsFromRole(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Permission> updatedPermissions = new HashSet<>(role.getPermissions());
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionId));
            updatedPermissions.remove(permission);
        }

        role.setPermissions(updatedPermissions);
        Role updatedRole = roleRepository.save(role);
        return new RoleResponse(updatedRole);
    }
}