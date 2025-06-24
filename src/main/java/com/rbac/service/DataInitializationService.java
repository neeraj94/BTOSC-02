package com.rbac.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbac.entity.Permission;
import com.rbac.entity.Role;
import com.rbac.entity.User;
import com.rbac.repository.PermissionRepository;
import com.rbac.repository.RoleRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

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
        logger.info("Starting data initialization...");
        
        try {
            initializePermissions();
            initializeRoles();
            initializeSuperAdmin();
            logger.info("Data initialization completed successfully.");
        } catch (Exception e) {
            logger.error("Error during data initialization: ", e);
        }
    }

    private void initializePermissions() throws IOException {
        if (permissionRepository.count() == 0) {
            logger.info("Initializing permissions from JSON file...");
            
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
            
            logger.info("Permissions initialized: {} permissions loaded", permissionsNode.size());
        }
    }

    private void initializeRoles() throws IOException {
        if (roleRepository.count() == 0) {
            logger.info("Initializing default roles...");
            
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

            logger.info("Default roles created: SUPER_ADMIN and CUSTOMER");
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
            logger.info("Creating Super Admin user...");
            
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
            logger.info("Super Admin user created with username: {}", superAdminUsername);
        }
    }
}