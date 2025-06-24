package com.rbac.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_permission_overrides",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "permission_id"}))
public class UserPermissionOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OverrideType overrideType;

    public enum OverrideType {
        GRANT,  // Grant permission even if not in role
        DENY    // Deny permission even if in role
    }

    // Constructors
    public UserPermissionOverride() {}

    public UserPermissionOverride(User user, Permission permission, OverrideType overrideType) {
        this.user = user;
        this.permission = permission;
        this.overrideType = overrideType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }

    public OverrideType getOverrideType() { return overrideType; }
    public void setOverrideType(OverrideType overrideType) { this.overrideType = overrideType; }
}