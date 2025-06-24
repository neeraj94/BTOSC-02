package com.rbac.dto.permission;

import com.rbac.entity.Permission;

public class PermissionResponse {
    private Long id;
    private String permissionKey;
    private String name;
    private String description;
    private String module;

    public PermissionResponse() {}

    public PermissionResponse(Permission permission) {
        this.id = permission.getId();
        this.permissionKey = permission.getPermissionKey();
        this.name = permission.getName();
        this.description = permission.getDescription();
        this.module = permission.getModule();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPermissionKey() { return permissionKey; }
    public void setPermissionKey(String permissionKey) { this.permissionKey = permissionKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
}