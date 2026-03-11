package com.azyu.mahallavote.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_captcha")
public class Captcha implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_app_captcha")
    @SequenceGenerator(name = "seq_app_captcha", sequenceName = "seq_app_captcha", allocationSize = 1)
    private Long id;

    @Column(name = "a_image_hash")
    private String aImageHash;

    @Column(name = "b_image_hash")
    private String bImageHash;

    @Column(name = "x")
    private Long x;

    @Column(name = "y")
    private Long y;

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

    public String getAImageHash() {
        return aImageHash;
    }

    public void setAImageHash(String aImageHash) {
        this.aImageHash = aImageHash;
    }

    public String getBImageHash() {
        return bImageHash;
    }

    public void setBImageHash(String bImageHash) {
        this.bImageHash = bImageHash;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
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
        if (!(o instanceof Captcha)) return false;
        return id != null && id.equals(((Captcha) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Captcha{" + "id=" + id + ", x=" + x + ", y=" + y + '}';
    }
}
