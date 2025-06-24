package com.rbac.dto.role;

import com.rbac.entity.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isSystemRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> permissions;
    private Integer userCount;

    public RoleResponse() {}

    public RoleResponse(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.isSystemRole = role.getIsSystemRole();
        this.createdAt = role.getCreatedAt();
        this.updatedAt = role.getUpdatedAt();
        this.permissions = role.getPermissions().stream()
                .map(permission -> permission.getPermissionKey())
                .collect(Collectors.toList());
        this.userCount = role.getUsers().size();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsSystemRole() { return isSystemRole; }
    public void setIsSystemRole(Boolean isSystemRole) { this.isSystemRole = isSystemRole; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }

    public Integer getUserCount() { return userCount; }
    public void setUserCount(Integer userCount) { this.userCount = userCount; }
}