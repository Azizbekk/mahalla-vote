package com.azyu.mahallavote.service.dto;

import com.azyu.mahallavote.domain.LocalizedName;
import com.azyu.mahallavote.domain.enumeration.I18nMessageStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

public class I18nMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 500)
    private String shortCode;

    @NotNull
    private LocalizedName localizedName;

    private String name;

    @NotNull
    private I18nMessageStatus status;

    private Long sortOrder;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalizedName getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(LocalizedName localizedName) {
        this.localizedName = localizedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public I18nMessageStatus getStatus() {
        return status;
    }

    public void setStatus(I18nMessageStatus status) {
        this.status = status;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
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
            "I18nMessageDTO{" + "id=" + id + ", shortCode='" + shortCode + '\'' + ", status=" + status + ", sortOrder=" + sortOrder + '}'
        );
    }
}
