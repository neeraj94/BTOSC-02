package com.rbac.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String permissionKey;

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @Size(max = 100)
    private String module;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    private Set<UserPermissionOverride> userOverrides = new HashSet<>();

    // Constructors
    public Permission() {}

    public Permission(String permissionKey, String name, String description, String module) {
        this.permissionKey = permissionKey;
        this.name = name;
        this.description = description;
        this.module = module;
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

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public Set<UserPermissionOverride> getUserOverrides() { return userOverrides; }
    public void setUserOverrides(Set<UserPermissionOverride> userOverrides) { this.userOverrides = userOverrides; }
}