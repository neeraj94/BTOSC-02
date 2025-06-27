
package com.rbac.dto.settings;

import com.rbac.entity.settings.SettingField.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class SettingFieldDto {
    
    private Long id;
    
    @NotBlank(message = "Field key is required")
    @Size(max = 100, message = "Field key must not exceed 100 characters")
    private String fieldKey;
    
    @NotBlank(message = "Field label is required")
    @Size(max = 255, message = "Field label must not exceed 255 characters")
    private String label;
    
    private String fieldValue;
    private FieldType fieldType;
    private Boolean isRequired = false;
    private String options;
    private Long subCategoryId;
    private String subCategoryName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public SettingFieldDto() {}
    
    public SettingFieldDto(Long id, String fieldKey, String label, String fieldValue, 
                          FieldType fieldType, Boolean isRequired, String description) {
        this.id = id;
        this.fieldKey = fieldKey;
        this.label = label;
        this.fieldValue = fieldValue;
        this.fieldType = fieldType;
        this.isRequired = isRequired;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFieldKey() {
        return fieldKey;
    }
    
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
    
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
    
    public FieldType getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public String getOptions() {
        return options;
    }
    
    public void setOptions(String options) {
        this.options = options;
    }
    
    public Long getSubCategoryId() {
        return subCategoryId;
    }
    
    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
    
    public String getSubCategoryName() {
        return subCategoryName;
    }
    
    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
