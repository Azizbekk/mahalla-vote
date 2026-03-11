package com.azyu.mahallavote.service.dto;

import com.azyu.mahallavote.domain.enumeration.ProjectSettingStatus;
import com.azyu.mahallavote.domain.enumeration.ProjectSettingValueType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ProjectSettingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String settingKey;

    @NotNull
    private String settingValue;

    @NotNull
    private ProjectSettingValueType valueType;

    private String description;

    @NotNull
    private ProjectSettingStatus status;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public ProjectSettingValueType getValueType() {
        return valueType;
    }

    public void setValueType(ProjectSettingValueType valueType) {
        this.valueType = valueType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectSettingStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectSettingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return (
            "ProjectSettingDTO{" +
            "id=" +
            id +
            ", settingKey='" +
            settingKey +
            '\'' +
            ", valueType=" +
            valueType +
            ", status=" +
            status +
            '}'
        );
    }
}
