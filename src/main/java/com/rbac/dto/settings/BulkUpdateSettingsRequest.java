
package com.rbac.dto.settings;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class BulkUpdateSettingsRequest {
    
    @NotNull(message = "Settings map cannot be null")
    @NotEmpty(message = "At least one setting must be provided")
    private Map<String, String> settings;
    
    public BulkUpdateSettingsRequest() {}
    
    public BulkUpdateSettingsRequest(Map<String, String> settings) {
        this.settings = settings;
    }
    
    public Map<String, String> getSettings() {
        return settings;
    }
    
    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}
