
package com.rbac.controller;

import com.rbac.dto.setting.CreateSettingRequest;
import com.rbac.dto.setting.SettingDto;
import com.rbac.dto.setting.UpdateSettingRequest;
import com.rbac.security.UserPrincipal;
import com.rbac.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings Management", description = "APIs for managing dynamic application settings")
@SecurityRequirement(name = "bearerAuth")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    @PreAuthorize("hasAuthority('settings.view')")
    @Operation(summary = "Get paginated list of settings", description = "Retrieve settings with optional filters by key and type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Settings retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<Map<String, Object>>> getAllSettings(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size,
            
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "desc")
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            
            @Parameter(description = "Filter by key (partial match)")
            @RequestParam(value = "key", required = false) String key,
            
            @Parameter(description = "Filter by type")
            @RequestParam(value = "type", required = false) String type) {
        
        com.rbac.dto.common.ApiResponse<Map<String, Object>> response = settingService.getAllSettings(
                page, size, sortBy, sortDirection, key, type);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('settings.view')")
    @Operation(summary = "Get setting by ID", description = "Retrieve a specific setting by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Setting not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<SettingDto>> getSettingById(
            @Parameter(description = "Setting ID", example = "1")
            @PathVariable Long id) {
        
        com.rbac.dto.common.ApiResponse<SettingDto> response = settingService.getSettingById(id);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{key}/view")
    @PreAuthorize("hasAuthority('settings.view')")
    @Operation(summary = "Get setting by key", description = "Retrieve a specific setting by its key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Setting not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<SettingDto>> getSettingByKey(
            @Parameter(description = "Setting key", example = "store_name")
            @PathVariable String key) {
        
        com.rbac.dto.common.ApiResponse<SettingDto> response = settingService.getSettingByKey(key);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('settings.create')")
    @Operation(summary = "Create new setting", description = "Create a new application setting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Setting created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or setting already exists"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<SettingDto>> createSetting(
            @Valid @RequestBody CreateSettingRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        com.rbac.dto.common.ApiResponse<SettingDto> response = settingService.createSetting(request, userPrincipal.getUsername());
        
        return response.isSuccess() ? 
                ResponseEntity.status(HttpStatus.CREATED).body(response) : 
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('settings.update')")
    @Operation(summary = "Update setting", description = "Update an existing setting's value and/or type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting updated successfully"),
            @ApiResponse(responseCode = "404", description = "Setting not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<SettingDto>> updateSetting(
            @Parameter(description = "Setting ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateSettingRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        com.rbac.dto.common.ApiResponse<SettingDto> response = settingService.updateSetting(id, request, userPrincipal.getUsername());
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('settings.delete')")
    @Operation(summary = "Delete setting", description = "Delete an existing setting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Setting deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Setting not found"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions")
    })
    public ResponseEntity<com.rbac.dto.common.ApiResponse<Void>> deleteSetting(
            @Parameter(description = "Setting ID", example = "1")
            @PathVariable Long id) {
        
        com.rbac.dto.common.ApiResponse<Void> response = settingService.deleteSetting(id);
        
        return response.isSuccess() ? 
                ResponseEntity.ok(response) : 
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
