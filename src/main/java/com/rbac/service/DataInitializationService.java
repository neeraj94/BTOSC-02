package com.rbac.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.entity.Setting;
import com.rbac.entity.User;
import com.rbac.repository.PermissionRepository;
import com.rbac.repository.RoleRepository;
import com.rbac.repository.SettingRepository;
import com.rbac.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
public class DataInitializationService {
    private static final Logger log = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.superadmin.username}")
    private String superAdminUsername;

    @Value("${app.superadmin.password}")
    private String superAdminPassword;

    @Value("${app.superadmin.email}")
    private String superAdminEmail;

    @PostConstruct
    @Transactional
    public void initializeData() {
        log.info("Starting data initialization...");

        try {
            initializePermissions();
            initializeRoles();
            initializeSuperAdmin();

            // Create default settings
            createDefaultSettings();

            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during data initialization: ", e);
        }
    }

    private void initializePermissions() throws IOException {
        if (permissionRepository.count() == 0) {
            log.info("Initializing permissions from JSON file...");

            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("permissions.json");
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            JsonNode permissionsNode = rootNode.get("permissions");

            for (JsonNode permissionNode : permissionsNode) {
                String key = permissionNode.get("id").asText();
                String name = permissionNode.get("name").asText();
                String description = permissionNode.get("description").asText();
                String module = permissionNode.get("module").asText();

                Permission permission = new Permission(key, name, description, module);
                permissionRepository.save(permission);
            }

            log.info("Permissions initialized: {} permissions loaded", permissionsNode.size());
        }
    }

    private void initializeRoles() throws IOException {
        if (roleRepository.count() == 0) {
            log.info("Initializing default roles...");

            // Create Super Admin Role with all permissions
            Role superAdminRole = new Role("SUPER_ADMIN", "Super Administrator with full access");
            superAdminRole.setIsSystemRole(true);
            Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            superAdminRole.setPermissions(allPermissions);
            roleRepository.save(superAdminRole);

            // Create Customer Role with limited permissions
            Role customerRole = new Role("CUSTOMER", "Default customer role");
            customerRole.setIsSystemRole(true);
            Set<Permission> customerPermissions = getCustomerPermissions();
            customerRole.setPermissions(customerPermissions);
            roleRepository.save(customerRole);

            log.info("Default roles created: SUPER_ADMIN and CUSTOMER");
        }
    }

    private Set<Permission> getCustomerPermissions() throws IOException {
        Set<Permission> customerPermissions = new HashSet<>();

        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("customer-permissions.json");
        JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
        JsonNode permissionsNode = rootNode.get("permissions");

        for (JsonNode permissionNode : permissionsNode) {
            String key = permissionNode.get("id").asText();
            Permission permission = permissionRepository.findByPermissionKey(key).orElse(null);
            if (permission != null) {
                customerPermissions.add(permission);
            }
        }

        return customerPermissions;
    }

    private void initializeSuperAdmin() {
        if (!userRepository.existsByUsername(superAdminUsername)) {
            log.info("Creating Super Admin user...");

            User superAdmin = new User();
            superAdmin.setUsername(superAdminUsername);
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setIsActive(true);
            superAdmin.setEmailVerified(true);

            Role superAdminRole = roleRepository.findByName("SUPER_ADMIN").orElse(null);
            if (superAdminRole != null) {
                Set<Role> roles = new HashSet<>();
                roles.add(superAdminRole);
                superAdmin.setRoles(roles);
            }

            userRepository.save(superAdmin);
            log.info("Super Admin user created with username: {}", superAdmin.getUsername());

        // Create default settings
        createDefaultSettings();

        log.info("Data initialization completed successfully.");
    }

    private void createDefaultSettings() {
        log.info("Creating default settings...");

        // Check if settings already exist
        if (settingRepository.count() > 0) {
            log.info("Settings already exist, skipping default settings creation.");
            return;
        }

        // Create default application settings
        Setting[] defaultSettings = {
            createSetting("app_name", "RBAC Management System", "string"),
            createSetting("app_version", "1.0.0", "string"),
            createSetting("company_name", "Your Company Name", "string"),
            createSetting("admin_email", "admin@example.com", "email"),
            createSetting("max_login_attempts", "5", "number"),
            createSetting("session_timeout", "30", "number"),
            createSetting("enable_email_notifications", "true", "boolean"),
            createSetting("maintenance_mode", "false", "boolean"),
            createSetting("theme_color", "#007bff", "color"),
            createSetting("api_rate_limit", "100", "number")
        };

        for (Setting setting : defaultSettings) {
            settingRepository.save(setting);
        }

        log.info("Default settings created successfully.");
    }

    private Setting createSetting(String key, String value, String type) {
        Setting setting = new Setting();
        setting.setKey(key);
        setting.setValue(value);
        setting.setType(type);
        setting.setCreatedBy("system");
        setting.setUpdatedBy("system");
        return setting;
    }
}