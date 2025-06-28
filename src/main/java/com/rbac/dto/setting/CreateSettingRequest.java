
package com.rbac.dto.setting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateSettingRequest {
    @NotBlank(message = "Key is required")
    private String key;

    private String value;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(string|boolean|number|json|array|html|css|js|richtext|color|url|email|date|datetime)$", 
             message = "Type must be one of: string, boolean, number, json, array, html, css, js, richtext, color, url, email, date, datetime")
    private String type;

    // Constructors
    public CreateSettingRequest() {}

    public CreateSettingRequest(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    // Getters and setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
