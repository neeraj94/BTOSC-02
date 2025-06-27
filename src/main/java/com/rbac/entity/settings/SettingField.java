
package com.rbac.entity.settings;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@Entity
@Table(name = "setting_fields")
public class SettingField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Field key is required")
    @Size(max = 100, message = "Field key must not exceed 100 characters")
    @Column(name = "field_key", nullable = false)
    private String fieldKey;
    
    @NotBlank(message = "Field label is required")
    @Size(max = 255, message = "Field label must not exceed 255 characters")
    @Column(nullable = false)
    private String label;
    
    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    @Column(columnDefinition = "JSON")
    private String options;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", nullable = false)
    @JsonIgnore
    private SettingSubCategory subCategory;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum FieldType {
        text, textarea, radio, dropdown, multiselect, checkbox, bool
    }
    
    // Constructors
    public SettingField() {}
    
    public SettingField(String fieldKey, String label, String fieldValue, FieldType fieldType, 
                       Boolean isRequired, SettingSubCategory subCategory, String description) {
        this.fieldKey = fieldKey;
        this.label = label;
        this.fieldValue = fieldValue;
        this.fieldType = fieldType;
        this.isRequired = isRequired;
        this.subCategory = subCategory;
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
    
    public SettingSubCategory getSubCategory() {
        return subCategory;
    }
    
    public void setSubCategory(SettingSubCategory subCategory) {
        this.subCategory = subCategory;
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
