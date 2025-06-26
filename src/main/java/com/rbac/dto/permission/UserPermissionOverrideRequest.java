package com.rbac.dto.permission;

import com.rbac.entity.UserPermissionOverride;
import jakarta.validation.constraints.NotNull;

public class UserPermissionOverrideRequest {
    @NotNull
    private Long permissionId;

    @NotNull
    private UserPermissionOverride.OverrideType overrideType;

    public UserPermissionOverrideRequest() {}

    public UserPermissionOverrideRequest(Long permissionId, UserPermissionOverride.OverrideType overrideType) {
        this.permissionId = permissionId;
        this.overrideType = overrideType;
    }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    public UserPermissionOverride.OverrideType getOverrideType() { return overrideType; }
    public void setOverrideType(UserPermissionOverride.OverrideType overrideType) { this.overrideType = overrideType; }
}