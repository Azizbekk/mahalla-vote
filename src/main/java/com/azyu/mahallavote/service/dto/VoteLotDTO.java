package com.azyu.mahallavote.service.dto;

import com.azyu.mahallavote.domain.enumeration.VoteLotStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

public class VoteLotDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 500)
    private String name;

    @NotNull
    @Size(max = 1000)
    private String openBudgetUrl;

    @NotNull
    private Integer targetVoteCount;

    private Integer currentVoteCount;

    @NotNull
    private VoteLotStatus status;

    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenBudgetUrl() {
        return openBudgetUrl;
    }

    public void setOpenBudgetUrl(String openBudgetUrl) {
        this.openBudgetUrl = openBudgetUrl;
    }

    public Integer getTargetVoteCount() {
        return targetVoteCount;
    }

    public void setTargetVoteCount(Integer targetVoteCount) {
        this.targetVoteCount = targetVoteCount;
    }

    public Integer getCurrentVoteCount() {
        return currentVoteCount;
    }

    public void setCurrentVoteCount(Integer currentVoteCount) {
        this.currentVoteCount = currentVoteCount;
    }

    public VoteLotStatus getStatus() {
        return status;
    }

    public void setStatus(VoteLotStatus status) {
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
            "VoteLotDTO{" +
            "id=" +
            id +
            ", name='" +
            name +
            '\'' +
            ", targetVoteCount=" +
            targetVoteCount +
            ", currentVoteCount=" +
            currentVoteCount +
            ", status=" +
            status +
            '}'
        );
    }
}
