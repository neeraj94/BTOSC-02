
package com.rbac.dto.setting;

import jakarta.validation.constraints.Pattern;

public class UpdateSettingRequest {
    private String value;

    @Pattern(regexp = "^(string|boolean|number|json|array|html|css|js|richtext|color|url|email|date|datetime)$", 
             message = "Type must be one of: string, boolean, number, json, array, html, css, js, richtext, color, url, email, date, datetime")
    private String type;

    // Constructors
    public UpdateSettingRequest() {}

    public UpdateSettingRequest(String value, String type) {
        this.value = value;
        this.type = type;
    }

    // Getters and setters
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
