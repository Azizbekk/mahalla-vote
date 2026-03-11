package com.azyu.mahallavote.domain;

import com.azyu.mahallavote.domain.enumeration.ProjectSettingStatus;
import com.azyu.mahallavote.domain.enumeration.ProjectSettingValueType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_setting")
public class ProjectSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_app_setting")
    @SequenceGenerator(name = "seq_app_setting", sequenceName = "seq_app_setting", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "setting_key", length = 100, nullable = false, unique = true)
    private String settingKey;

    @NotNull
    @Column(name = "setting_value", columnDefinition = "TEXT", nullable = false)
    private String settingValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false)
    private ProjectSettingValueType valueType = ProjectSettingValueType.STRING;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectSettingStatus status = ProjectSettingStatus.ACTIVE;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate = LocalDateTime.now();

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectSetting)) return false;
        return id != null && id.equals(((ProjectSetting) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "ProjectSetting{" + "id=" + id + ", settingKey='" + settingKey + '\'' + ", valueType=" + valueType + ", status=" + status + '}'
        );
    }
}
