
package com.rbac.service.settings;

import com.rbac.dto.settings.*;
import com.rbac.entity.settings.*;
import com.rbac.exception.ResourceNotFoundException;
import com.rbac.exception.ResourceAlreadyExistsException;
import com.rbac.repository.settings.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SettingsService {
    
    @Autowired
    private SettingCategoryRepository categoryRepository;
    
    @Autowired
    private SettingSubCategoryRepository subCategoryRepository;
    
    @Autowired
    private SettingFieldRepository fieldRepository;
    
    // Category operations
    public List<SettingCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertCategoryToDto)
                .collect(Collectors.toList());
    }
    
    // SubCategory operations
    public List<SettingSubCategoryDto> getSubCategoriesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        
        return subCategoryRepository.findByCategoryId(categoryId).stream()
                .map(this::convertSubCategoryToDto)
                .collect(Collectors.toList());
    }
    
    // Field operations
    public List<SettingFieldDto> getFieldsBySubCategory(Long subCategoryId) {
        if (!subCategoryRepository.existsById(subCategoryId)) {
            throw new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId);
        }
        
        return fieldRepository.findBySubCategoryId(subCategoryId).stream()
                .map(this::convertFieldToDto)
                .collect(Collectors.toList());
    }
    
    public List<SettingFieldDto> bulkUpdateFields(Long subCategoryId, Map<String, String> settings) {
        SettingSubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId));
        
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            String fieldKey = entry.getKey();
            String newValue = entry.getValue();
            
            SettingField field = fieldRepository.findByFieldKeyAndSubCategoryId(fieldKey, subCategoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Setting field not found: " + fieldKey));
            
            field.setFieldValue(newValue);
            fieldRepository.save(field);
        }
        
        return getFieldsBySubCategory(subCategoryId);
    }
    
    public SettingFieldDto createField(Long subCategoryId, SettingFieldDto fieldDto) {
        SettingSubCategory subCategory = subCategoryRepository.findById(subCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory not found with id: " + subCategoryId));
        
        if (fieldRepository.existsByFieldKeyAndSubCategoryId(fieldDto.getFieldKey(), subCategoryId)) {
            throw new ResourceAlreadyExistsException("Setting field already exists with key: " + fieldDto.getFieldKey());
        }
        
        SettingField field = new SettingField();
        field.setFieldKey(fieldDto.getFieldKey());
        field.setLabel(fieldDto.getLabel());
        field.setFieldValue(fieldDto.getFieldValue());
        field.setFieldType(fieldDto.getFieldType());
        field.setIsRequired(fieldDto.getIsRequired());
        field.setOptions(fieldDto.getOptions());
        field.setSubCategory(subCategory);
        field.setDescription(fieldDto.getDescription());
        
        SettingField savedField = fieldRepository.save(field);
        return convertFieldToDto(savedField);
    }
    
    public void deleteField(Long fieldId) {
        if (!fieldRepository.existsById(fieldId)) {
            throw new ResourceNotFoundException("Setting field not found with id: " + fieldId);
        }
        fieldRepository.deleteById(fieldId);
    }
    
    // Conversion methods
    private SettingCategoryDto convertCategoryToDto(SettingCategory category) {
        SettingCategoryDto dto = new SettingCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    
    private SettingSubCategoryDto convertSubCategoryToDto(SettingSubCategory subCategory) {
        SettingSubCategoryDto dto = new SettingSubCategoryDto();
        dto.setId(subCategory.getId());
        dto.setName(subCategory.getName());
        dto.setCategoryId(subCategory.getCategory().getId());
        dto.setCategoryName(subCategory.getCategory().getName());
        dto.setDescription(subCategory.getDescription());
        dto.setCreatedAt(subCategory.getCreatedAt());
        dto.setUpdatedAt(subCategory.getUpdatedAt());
        return dto;
    }
    
    private SettingFieldDto convertFieldToDto(SettingField field) {
        SettingFieldDto dto = new SettingFieldDto();
        dto.setId(field.getId());
        dto.setFieldKey(field.getFieldKey());
        dto.setLabel(field.getLabel());
        dto.setFieldValue(field.getFieldValue());
        dto.setFieldType(field.getFieldType());
        dto.setIsRequired(field.getIsRequired());
        dto.setOptions(field.getOptions());
        dto.setSubCategoryId(field.getSubCategory().getId());
        dto.setSubCategoryName(field.getSubCategory().getName());
        dto.setDescription(field.getDescription());
        dto.setCreatedAt(field.getCreatedAt());
        dto.setUpdatedAt(field.getUpdatedAt());
        return dto;
    }
}
