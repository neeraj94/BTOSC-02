package com.rbac.controller;

import com.rbac.dto.common.ApiResponse;
import com.rbac.dto.permission.PermissionResponse;
import com.rbac.dto.permission.UserPermissionOverrideRequest;
import com.rbac.entity.Permission;
import com.rbac.entity.UserPermissionOverride;
import com.rbac.repository.PermissionRepository;
import com.rbac.service.PermissionService;
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
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permission Management", description = "Permission management APIs")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieve all permissions with pagination and filtering")
    @PreAuthorize("hasAuthority('permission.read')")
    public ResponseEntity<ApiResponse<Page<PermissionResponse>>> getAllPermissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "module") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String name) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermissionResponse> permissions = permissionService.getPermissionsWithFilters(module, name, pageable)
                .map(PermissionResponse::new);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @GetMapping("/modules")
    @Operation(summary = "Get all modules", description = "Retrieve all permission modules")
    @PreAuthorize("hasAuthority('permission.read')")
    public ResponseEntity<ApiResponse<List<String>>> getAllModules() {
        List<String> modules = permissionService.getAllModules();
        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @GetMapping("/grouped")
    @Operation(summary = "Get permissions grouped by module", description = "Retrieve permissions grouped by module")
    @PreAuthorize("hasAuthority('permission.read')")
    public ResponseEntity<ApiResponse<Map<String, List<PermissionResponse>>>> getPermissionsGroupedByModule() {
        Map<String, List<Permission>> groupedPermissions = permissionService.getPermissionsGroupedByModule();
        Map<String, List<PermissionResponse>> response = groupedPermissions.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(PermissionResponse::new)
                                .collect(Collectors.toList())
                ));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user effective permissions", description = "Get effective permissions for a user")
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<ApiResponse<List<String>>> getUserEffectivePermissions(@PathVariable Long userId) {
        List<String> permissions = permissionService.getUserEffectivePermissions(userId);
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @GetMapping("/user/{userId}/overrides")
    @Operation(summary = "Get user permission overrides", description = "Get permission overrides for a user")
    @PreAuthorize("hasAuthority('user.read')")
    public ResponseEntity<ApiResponse<List<UserPermissionOverride>>> getUserPermissionOverrides(@PathVariable Long userId) {
        List<UserPermissionOverride> overrides = permissionService.getUserPermissionOverrides(userId);
        return ResponseEntity.ok(ApiResponse.success(overrides));
    }

    @PostMapping("/user/overrides")
    @Operation(summary = "Set user permission overrides", description = "Set permission overrides for a specific user")
    public ResponseEntity<ApiResponse<Object>> setUserPermissionOverrides(
            @RequestParam Long userId,
            @RequestBody List<UserPermissionOverrideRequest> overrides) {
        permissionService.setUserPermissionOverrides(userId, overrides);
        return ResponseEntity.ok(ApiResponse.success("User permission overrides set successfully"));
    }

    @PostMapping("/user/overrides/add")
    @Operation(summary = "Add user permission overrides", description = "Add permission overrides for a specific user (accumulative)")
    public ResponseEntity<ApiResponse<Object>> addUserPermissionOverrides(
            @RequestParam Long userId,
            @RequestBody List<UserPermissionOverrideRequest> overrides) {
        permissionService.addUserPermissionOverrides(userId, overrides);
        return ResponseEntity.ok(ApiResponse.success("User permission overrides added successfully"));
    }

    @GetMapping("/check/{userId}/{permissionKey}")
    @Operation(summary = "Check user permission", description = "Check if user has specific permission")
    @PreAuthorize("hasAuthority('permission.read')")
    public ResponseEntity<ApiResponse<Boolean>> checkUserPermission(
            @PathVariable Long userId,
            @PathVariable String permissionKey) {
        boolean hasPermission = permissionService.hasPermission(userId, permissionKey);
        return ResponseEntity.ok(ApiResponse.success("Permission check completed", hasPermission));
    }

    @GetMapping("/user/{userId}/dashboard-modules")
    @Operation(summary = "Get user dashboard modules", description = "Get available dashboard modules for user based on permissions")
    @PreAuthorize("hasAuthority('permission.read') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<String>>> getUserDashboardModules(@PathVariable Long userId) {
        List<String> modules = permissionService.getUserDashboardModules(userId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard modules retrieved successfully", modules));
    }
}