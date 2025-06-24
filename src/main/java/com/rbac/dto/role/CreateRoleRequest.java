package com.rbac.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class CreateRoleRequest {
    @NotBlank
    @Size(max = 60)
    private String name;

    @Size(max = 255)
    private String description;

    private Set<Long> permissionIds;

    public CreateRoleRequest() {}

    public CreateRoleRequest(String name, String description, Set<Long> permissionIds) {
        this.name = name;
        this.description = description;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(Set<Long> permissionIds) { this.permissionIds = permissionIds; }
}