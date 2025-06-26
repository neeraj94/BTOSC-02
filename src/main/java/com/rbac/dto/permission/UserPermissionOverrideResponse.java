
package com.rbac.dto.permission;

import com.rbac.entity.UserPermissionOverride;

public class UserPermissionOverrideResponse {
    private Long id;
    private Long userId;
    private String username;
    private PermissionResponse permission;
    private UserPermissionOverride.OverrideType overrideType;

    public UserPermissionOverrideResponse(UserPermissionOverride override) {
        this.id = override.getId();
        this.userId = override.getUser().getId();
        this.username = override.getUser().getUsername();
        this.permission = new PermissionResponse(override.getPermission());
        this.overrideType = override.getOverrideType();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public PermissionResponse getPermission() { return permission; }
    public void setPermission(PermissionResponse permission) { this.permission = permission; }

    public UserPermissionOverride.OverrideType getOverrideType() { return overrideType; }
    public void setOverrideType(UserPermissionOverride.OverrideType overrideType) { this.overrideType = overrideType; }
}
