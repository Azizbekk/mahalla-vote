package com.azyu.mahallavote.domain;

import com.azyu.mahallavote.domain.enumeration.I18nMessageStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "i18n_message")
public class I18nMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_i18n_message")
    @SequenceGenerator(name = "seq_i18n_message", sequenceName = "seq_i18n_message", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(name = "short_code", length = 500, nullable = false, unique = true)
    private String shortCode;

    @NotNull
    @Convert(converter = LocalizedNameConverter.class)
    @Column(name = "localized_name", columnDefinition = "TEXT", nullable = false)
    private LocalizedName localizedName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private I18nMessageStatus status = I18nMessageStatus.ACTIVE;

    @Column(name = "sort_order")
    private Long sortOrder;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof I18nMessage)) return false;
        return id != null && id.equals(((I18nMessage) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "I18nMessage{" + "id=" + id + ", shortCode='" + shortCode + '\'' + ", status=" + status + ", sortOrder=" + sortOrder + '}';
    }
}
