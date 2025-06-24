package com.rbac.dto.permission;

import com.rbac.entity.UserPermissionOverride;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UserPermissionOverrideRequest {
    @NotNull
    private Long userId;

    private List<PermissionOverride> overrides;

    public static class PermissionOverride {
        @NotNull
        private Long permissionId;

        @NotNull
        private UserPermissionOverride.OverrideType overrideType;

        public PermissionOverride() {}

        public PermissionOverride(Long permissionId, UserPermissionOverride.OverrideType overrideType) {
            this.permissionId = permissionId;
            this.overrideType = overrideType;
        }

        public Long getPermissionId() { return permissionId; }
        public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

        public UserPermissionOverride.OverrideType getOverrideType() { return overrideType; }
        public void setOverrideType(UserPermissionOverride.OverrideType overrideType) { this.overrideType = overrideType; }
    }

    public UserPermissionOverrideRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public List<PermissionOverride> getOverrides() { return overrides; }
    public void setOverrides(List<PermissionOverride> overrides) { this.overrides = overrides; }
}