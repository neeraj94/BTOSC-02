package com.rbac.service;

import com.rbac.exception.ResourceNotFoundException;
import com.rbac.exception.ResourceAlreadyExistsException;
import com.rbac.exception.InvalidOperationException;

import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.rbac.entity.UserPermissionOverride;
import com.rbac.repository.PermissionRepository;
import com.rbac.repository.UserPermissionOverrideRepository;
import com.rbac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PermissionService {
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPermissionOverrideRepository userPermissionOverrideRepository;

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Page<Permission> getPermissionsWithFilters(String module, String name, Pageable pageable) {
        return permissionRepository.findWithFilters(module, name, pageable);
    }

    public List<String> getAllModules() {
        return permissionRepository.findAllModules();
    }

    public Map<String, List<Permission>> getPermissionsGroupedByModule() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream()
                .collect(Collectors.groupingBy(Permission::getModule));
    }

    public List<String> getUserEffectivePermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get permissions from roles
        Set<String> rolePermissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());

        // Get user permission overrides
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(userId);
        
        Set<String> effectivePermissions = new HashSet<>(rolePermissions);

        for (UserPermissionOverride override : overrides) {
            String permissionKey = override.getPermission().getPermissionKey();
            
            if (override.getOverrideType() == UserPermissionOverride.OverrideType.GRANT) {
                effectivePermissions.add(permissionKey);
            } else if (override.getOverrideType() == UserPermissionOverride.OverrideType.DENY) {
                effectivePermissions.remove(permissionKey);
            }
        }

        return new ArrayList<>(effectivePermissions);
    }

    public void setUserPermissionOverrides(Long userId, List<com.rbac.dto.permission.UserPermissionOverrideRequest> overrideRequests) {
        // Remove existing overrides
        userPermissionOverrideRepository.deleteByUserId(userId);

        // Add new overrides
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        for (com.rbac.dto.permission.UserPermissionOverrideRequest request : overrideRequests) {
            Permission permission = permissionRepository.findById(request.getPermissionId())
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + request.getPermissionId()));
            
            UserPermissionOverride override = new UserPermissionOverride();
            override.setUser(user);
            override.setPermission(permission);
            override.setOverrideType(request.getOverrideType());
            
            userPermissionOverrideRepository.save(override);
        }
    }

    public List<UserPermissionOverride> getUserPermissionOverrides(Long userId) {
        return userPermissionOverrideRepository.findByUserId(userId);
    }

    public boolean hasPermission(Long userId, String permissionKey) {
        List<String> userPermissions = getUserEffectivePermissions(userId);
        return userPermissions.contains(permissionKey);
    }

    public List<String> getUserDashboardModules(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get permissions from roles
        Set<String> rolePermissionKeys = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());

        // Get user permission overrides
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(userId);
        
        Set<String> effectivePermissionKeys = new HashSet<>(rolePermissionKeys);

        for (UserPermissionOverride override : overrides) {
            String permissionKey = override.getPermission().getPermissionKey();
            
            if (override.getOverrideType() == UserPermissionOverride.OverrideType.GRANT) {
                effectivePermissionKeys.add(permissionKey);
            } else if (override.getOverrideType() == UserPermissionOverride.OverrideType.DENY) {
                effectivePermissionKeys.remove(permissionKey);
            }
        }

        // Get modules for effective permissions
        List<Permission> allPermissions = permissionRepository.findAll();
        Set<String> availableModules = allPermissions.stream()
                .filter(permission -> effectivePermissionKeys.contains(permission.getPermissionKey()))
                .map(Permission::getModule)
                .filter(module -> module != null && !module.trim().isEmpty())
                .collect(Collectors.toSet());

        return new ArrayList<>(availableModules);
    }

    public Map<String, List<String>> getUserDashboardModulesWithPermissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get permissions from roles
        Set<String> rolePermissionKeys = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getPermissionKey)
                .collect(Collectors.toSet());

        // Get user permission overrides
        List<UserPermissionOverride> overrides = userPermissionOverrideRepository.findByUserId(userId);
        
        Set<String> effectivePermissionKeys = new HashSet<>(rolePermissionKeys);

        for (UserPermissionOverride override : overrides) {
            String permissionKey = override.getPermission().getPermissionKey();
            
            if (override.getOverrideType() == UserPermissionOverride.OverrideType.GRANT) {
                effectivePermissionKeys.add(permissionKey);
            } else if (override.getOverrideType() == UserPermissionOverride.OverrideType.DENY) {
                effectivePermissionKeys.remove(permissionKey);
            }
        }

        // Get permissions for effective permission keys and group by module
        List<Permission> allPermissions = permissionRepository.findAll();
        Map<String, List<String>> modulePermissions = allPermissions.stream()
                .filter(permission -> effectivePermissionKeys.contains(permission.getPermissionKey()))
                .filter(permission -> permission.getModule() != null && !permission.getModule().trim().isEmpty())
                .collect(Collectors.groupingBy(
                    Permission::getModule,
                    Collectors.mapping(Permission::getPermissionKey, Collectors.toList())
                ));

        return modulePermissions;
    }

    public Map<String, Object> addUserPermissionOverrides(Long userId, List<com.rbac.dto.permission.UserPermissionOverrideRequest> overrideRequests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newOverrides = 0;
        int updatedOverrides = 0;
        int duplicateOverrides = 0;

        for (com.rbac.dto.permission.UserPermissionOverrideRequest request : overrideRequests) {
            Permission permission = permissionRepository.findById(request.getPermissionId())
                    .orElseThrow(() -> new RuntimeException("Permission not found with id: " + request.getPermissionId()));
            
            // Check if override already exists for this permission
            Optional<UserPermissionOverride> existingOverride = userPermissionOverrideRepository
                    .findByUserIdAndPermissionId(userId, request.getPermissionId());
            
            if (existingOverride.isPresent()) {
                // Check if the override type is the same
                if (existingOverride.get().getOverrideType() == request.getOverrideType()) {
                    duplicateOverrides++;
                } else {
                    // Update existing override with different type
                    existingOverride.get().setOverrideType(request.getOverrideType());
                    userPermissionOverrideRepository.save(existingOverride.get());
                    updatedOverrides++;
                }
            } else {
                // Create new override
                UserPermissionOverride override = new UserPermissionOverride();
                override.setUser(user);
                override.setPermission(permission);
                override.setOverrideType(request.getOverrideType());
                userPermissionOverrideRepository.save(override);
                newOverrides++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("newOverrides", newOverrides);
        result.put("updatedOverrides", updatedOverrides);
        result.put("duplicateOverrides", duplicateOverrides);
        
        String message;
        if (duplicateOverrides > 0 && newOverrides == 0 && updatedOverrides == 0) {
            message = "All permission overrides already exist with the same type";
        } else if (duplicateOverrides > 0) {
            message = String.format("Added %d new, updated %d existing, %d duplicate overrides found", 
                newOverrides, updatedOverrides, duplicateOverrides);
        } else {
            message = String.format("Added %d new and updated %d existing permission overrides", 
                newOverrides, updatedOverrides);
        }
        result.put("message", message);
        
        return result;
    }
}