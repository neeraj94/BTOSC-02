package com.rbac.controller;

import com.rbac.dto.role.RoleResponse;
import com.rbac.dto.user.UserResponse;
import com.rbac.service.ExportService;
import com.rbac.service.RoleService;
import com.rbac.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Data export APIs")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private ExportService exportService;

    @GetMapping("/users/excel")
    @Operation(summary = "Export users to Excel", description = "Export all users data to Excel format")
    @PreAuthorize("hasAuthority('user.export')")
    public ResponseEntity<byte[]> exportUsersToExcel() throws IOException {
        Page<UserResponse> usersPage = userService.getAllUsers(Pageable.unpaged());
        List<UserResponse> users = usersPage.getContent();
        
        byte[] excelData = exportService.exportUsersToExcel(users);
        
        String filename = "users_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/users/csv")
    @Operation(summary = "Export users to CSV", description = "Export all users data to CSV format")
    @PreAuthorize("hasAuthority('user.export')")
    public ResponseEntity<String> exportUsersToCSV() {
        Page<UserResponse> usersPage = userService.getAllUsers(Pageable.unpaged());
        List<UserResponse> users = usersPage.getContent();
        
        String csvData = exportService.exportUsersToCSV(users);
        
        String filename = "users_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }

    @GetMapping("/roles/excel")
    @Operation(summary = "Export roles to Excel", description = "Export all roles data to Excel format")
    @PreAuthorize("hasAuthority('role.export')")
    public ResponseEntity<byte[]> exportRolesToExcel() throws IOException {
        Page<RoleResponse> rolesPage = roleService.getAllRoles(Pageable.unpaged());
        List<RoleResponse> roles = rolesPage.getContent();
        
        byte[] excelData = exportService.exportRolesToExcel(roles);
        
        String filename = "roles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

    @GetMapping("/roles/csv")
    @Operation(summary = "Export roles to CSV", description = "Export all roles data to CSV format")
    @PreAuthorize("hasAuthority('role.export')")
    public ResponseEntity<String> exportRolesToCSV() {
        Page<RoleResponse> rolesPage = roleService.getAllRoles(Pageable.unpaged());
        List<RoleResponse> roles = rolesPage.getContent();
        
        String csvData = exportService.exportRolesToCSV(roles);
        
        String filename = "roles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }
}
package com.rbac.controller;

import com.rbac.dto.common.ApiResponse;
import com.rbac.dto.role.RoleResponse;
import com.rbac.dto.user.UserResponse;
import com.rbac.service.ExportService;
import com.rbac.service.RoleService;
import com.rbac.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/export")
@Tag(name = "Export", description = "Data export APIs")
@SecurityRequirement(name = "bearerAuth")
public class ExportController {
    
    @Autowired
    private ExportService exportService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;

    @GetMapping("/users/excel")
    @Operation(summary = "Export users to Excel", description = "Export all users data to Excel file")
    @PreAuthorize("hasAuthority('user.export')")
    public ResponseEntity<?> exportUsersToExcel() {
        try {
            // Get all users (you might want to add pagination limits for large datasets)
            Pageable pageable = PageRequest.of(0, 10000); // Reasonable limit
            List<UserResponse> users = userService.getUsersWithFilters(null, null, null, pageable).getContent();
            
            byte[] excelData = exportService.exportUsersToExcel(users);
            
            String filename = "users_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
                    
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to export users: " + e.getMessage()));
        }
    }

    @GetMapping("/roles/excel")
    @Operation(summary = "Export roles to Excel", description = "Export all roles data to Excel file")
    @PreAuthorize("hasAuthority('role.export')")
    public ResponseEntity<?> exportRolesToExcel() {
        try {
            // Get all roles
            Pageable pageable = PageRequest.of(0, 10000); // Reasonable limit
            List<RoleResponse> roles = roleService.getRolesWithFilters(null, null, pageable).getContent();
            
            byte[] excelData = exportService.exportRolesToExcel(roles);
            
            String filename = "roles_export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
                    
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to export roles: " + e.getMessage()));
        }
    }
}
