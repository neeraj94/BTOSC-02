package com.rbac.dto.auth;

import java.util.List;
import java.util.Map;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private List<String> dashboardModules;
    private Map<String, List<String>> dashboardModulesWithPermissions;

    public JwtResponse(String accessToken, Long id, String username, String email, List<String> roles, List<String> permissions, List<String> dashboardModules, Map<String, List<String>> dashboardModulesWithPermissions) {
        this.token = accessToken;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
        this.dashboardModules = dashboardModules;
        this.dashboardModulesWithPermissions = dashboardModulesWithPermissions;
    }

    public String getAccessToken() { return token; }
    public void setAccessToken(String accessToken) { this.token = accessToken; }

    public String getTokenType() { return type; }
    public void setTokenType(String tokenType) { this.type = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getDashboardModules() {
        return dashboardModules;
    }

    public void setDashboardModules(List<String> dashboardModules) {
        this.dashboardModules = dashboardModules;
    }

    public Map<String, List<String>> getDashboardModulesWithPermissions() {
        return dashboardModulesWithPermissions;
    }

    public void setDashboardModulesWithPermissions(Map<String, List<String>> dashboardModulesWithPermissions) {
        this.dashboardModulesWithPermissions = dashboardModulesWithPermissions;
    }
}