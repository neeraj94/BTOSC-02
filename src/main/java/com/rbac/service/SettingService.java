
package com.rbac.service;

import com.rbac.dto.common.ApiResponse;
import com.rbac.dto.setting.CreateSettingRequest;
import com.rbac.dto.setting.SettingDto;
import com.rbac.dto.setting.UpdateSettingRequest;
import com.rbac.entity.Setting;
import com.rbac.exception.ResourceAlreadyExistsException;
import com.rbac.exception.ResourceNotFoundException;
import com.rbac.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    public ApiResponse<Map<String, Object>> getAllSettings(int page, int size, String sortBy, 
                                                          String sortDirection, String key, String type) {
        try {
            Sort sort = sortDirection.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Setting> settingPage = settingRepository.findWithFilters(key, type, pageable);
            
            Page<SettingDto> settingDtoPage = settingPage.map(this::convertToDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("settings", settingDtoPage.getContent());
            response.put("currentPage", settingDtoPage.getNumber());
            response.put("totalItems", settingDtoPage.getTotalElements());
            response.put("totalPages", settingDtoPage.getTotalPages());
            response.put("pageSize", settingDtoPage.getSize());
            response.put("hasNext", settingDtoPage.hasNext());
            response.put("hasPrevious", settingDtoPage.hasPrevious());
            
            return new ApiResponse<>(true, "Settings retrieved successfully", response);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving settings: " + e.getMessage(), null);
        }
    }

    public ApiResponse<SettingDto> getSettingById(Long id) {
        try {
            Setting setting = settingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Setting not found with id: " + id));
            
            return new ApiResponse<>(true, "Setting retrieved successfully", convertToDto(setting));
        } catch (ResourceNotFoundException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving setting: " + e.getMessage(), null);
        }
    }

    public ApiResponse<SettingDto> getSettingByKey(String key) {
        try {
            Setting setting = settingRepository.findByKey(key)
                    .orElseThrow(() -> new ResourceNotFoundException("Setting not found with key: " + key));
            
            return new ApiResponse<>(true, "Setting retrieved successfully", convertToDto(setting));
        } catch (ResourceNotFoundException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error retrieving setting: " + e.getMessage(), null);
        }
    }

    public ApiResponse<SettingDto> createSetting(CreateSettingRequest request, String currentUser) {
        try {
            if (settingRepository.existsByKey(request.getKey())) {
                throw new ResourceAlreadyExistsException("Setting with key '" + request.getKey() + "' already exists");
            }

            Setting setting = new Setting();
            setting.setKey(request.getKey());
            setting.setValue(request.getValue());
            setting.setType(request.getType());
            setting.setCreatedBy(currentUser);
            setting.setUpdatedBy(currentUser);

            Setting savedSetting = settingRepository.save(setting);
            return new ApiResponse<>(true, "Setting created successfully", convertToDto(savedSetting));
        } catch (ResourceAlreadyExistsException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error creating setting: " + e.getMessage(), null);
        }
    }

    public ApiResponse<SettingDto> updateSetting(Long id, UpdateSettingRequest request, String currentUser) {
        try {
            Setting setting = settingRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Setting not found with id: " + id));

            // Update value even if it's null (allow explicit null updates)
            setting.setValue(request.getValue());
            
            if (request.getType() != null) {
                setting.setType(request.getType());
            }
            setting.setUpdatedBy(currentUser);

            Setting updatedSetting = settingRepository.save(setting);
            return new ApiResponse<>(true, "Setting updated successfully", convertToDto(updatedSetting));
        } catch (ResourceNotFoundException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error updating setting: " + e.getMessage(), null);
        }
    }

    public ApiResponse<Void> deleteSetting(Long id) {
        try {
            if (!settingRepository.existsById(id)) {
                throw new ResourceNotFoundException("Setting not found with id: " + id);
            }

            settingRepository.deleteById(id);
            return new ApiResponse<>(true, "Setting deleted successfully", null);
        } catch (ResourceNotFoundException e) {
            return new ApiResponse<>(false, e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error deleting setting: " + e.getMessage(), null);
        }
    }

    private SettingDto convertToDto(Setting setting) {
        return new SettingDto(
                setting.getId(),
                setting.getKey(),
                setting.getValue(),
                setting.getType(),
                setting.getCreatedAt(),
                setting.getUpdatedAt(),
                setting.getCreatedBy(),
                setting.getUpdatedBy()
        );
    }
}
