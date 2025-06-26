
package com.rbac.controller;

import com.rbac.dto.common.ApiResponse;
import com.rbac.dto.role.CreateRoleRequest;
import com.rbac.dto.role.RoleResponse;
import com.rbac.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "Role management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role", description = "Create a new role")
    @PreAuthorize("hasAuthority('role.create')")
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ResponseEntity.ok(ApiResponse.success("Role created successfully", role));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update role", description = "Update an existing role")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Long id, @Valid @RequestBody CreateRoleRequest request) {
        RoleResponse role = roleService.updateRole(id, request);
        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", role));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve role details by ID")
    @PreAuthorize("hasAuthority('role.read')")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.success(role));
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve all roles with pagination and filtering")
    @PreAuthorize("hasAuthority('role.read')")
    public ResponseEntity<ApiResponse<Page<RoleResponse>>> getAllRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<RoleResponse> roles = roleService.getRolesWithFilters(name, description, pageable);
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @GetMapping("/non-system")
    @Operation(summary = "Get non-system roles", description = "Retrieve all non-system roles")
    @PreAuthorize("hasAuthority('role.read')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getNonSystemRoles() {
        List<RoleResponse> roles = roleService.getNonSystemRoles();
        return ResponseEntity.ok(ApiResponse.success(roles));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Delete a role")
    @PreAuthorize("hasAuthority('role.delete')")
    public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully"));
    }

    @PostMapping("/{id}/permissions")
    @Operation(summary = "Add permissions to role", description = "Add permissions to an existing role")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<ApiResponse<RoleResponse>> addPermissionsToRole(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.addPermissionsToRole(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success("Permissions added to role successfully", role));
    }

    @DeleteMapping("/{id}/permissions")
    @Operation(summary = "Remove permissions from role", description = "Remove permissions from an existing role")
    @PreAuthorize("hasAuthority('role.update')")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermissionsFromRole(@PathVariable Long id, @RequestBody Set<Long> permissionIds) {
        RoleResponse role = roleService.removePermissionsFromRole(id, permissionIds);
        return ResponseEntity.ok(ApiResponse.success("Permissions removed from role successfully", role));
    }
}
