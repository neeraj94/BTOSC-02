
package com.rbac.controller.settings;

import com.rbac.dto.settings.*;
import com.rbac.service.settings.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings Management", description = "Hierarchical settings management endpoints")
public class SettingsController {
    
    @Autowired
    private SettingsService settingsService;
    
    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('settings.read')")
    @Operation(summary = "Get all setting categories", description = "Retrieve list of all setting categories")
    public ResponseEntity<List<SettingCategoryDto>> getCategories() {
        List<SettingCategoryDto> categories = settingsService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/categories/{categoryId}/subcategories")
    @PreAuthorize("hasAuthority('settings.read')")
    @Operation(summary = "Get subcategories by category", description = "Retrieve all subcategories under a specific category")
    public ResponseEntity<List<SettingSubCategoryDto>> getSubCategories(
            @Parameter(description = "Category ID") @PathVariable Long categoryId) {
        List<SettingSubCategoryDto> subCategories = settingsService.getSubCategoriesByCategory(categoryId);
        return ResponseEntity.ok(subCategories);
    }
    
    @GetMapping("/subcategories/{subCategoryId}/fields")
    @PreAuthorize("hasAuthority('settings.read')")
    @Operation(summary = "Get setting fields by subcategory", description = "Retrieve all setting fields in a specific subcategory")
    public ResponseEntity<List<SettingFieldDto>> getFields(
            @Parameter(description = "SubCategory ID") @PathVariable Long subCategoryId) {
        List<SettingFieldDto> fields = settingsService.getFieldsBySubCategory(subCategoryId);
        return ResponseEntity.ok(fields);
    }
    
    @PutMapping("/subcategories/{subCategoryId}/fields")
    @PreAuthorize("hasAuthority('settings.update')")
    @Operation(summary = "Bulk update setting fields", description = "Update multiple setting field values in a subcategory")
    public ResponseEntity<List<SettingFieldDto>> bulkUpdateFields(
            @Parameter(description = "SubCategory ID") @PathVariable Long subCategoryId,
            @Valid @RequestBody BulkUpdateSettingsRequest request) {
        List<SettingFieldDto> updatedFields = settingsService.bulkUpdateFields(subCategoryId, request.getSettings());
        return ResponseEntity.ok(updatedFields);
    }
    
    @PostMapping("/subcategories/{subCategoryId}/fields")
    @PreAuthorize("hasAuthority('settings.create')")
    @Operation(summary = "Create new setting field", description = "Add a new setting field to a subcategory")
    public ResponseEntity<SettingFieldDto> createField(
            @Parameter(description = "SubCategory ID") @PathVariable Long subCategoryId,
            @Valid @RequestBody SettingFieldDto fieldDto) {
        SettingFieldDto createdField = settingsService.createField(subCategoryId, fieldDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdField);
    }
    
    @DeleteMapping("/fields/{fieldId}")
    @PreAuthorize("hasAuthority('settings.delete')")
    @Operation(summary = "Delete setting field", description = "Remove a setting field")
    public ResponseEntity<Void> deleteField(
            @Parameter(description = "Field ID") @PathVariable Long fieldId) {
        settingsService.deleteField(fieldId);
        return ResponseEntity.noContent().build();
    }
}
