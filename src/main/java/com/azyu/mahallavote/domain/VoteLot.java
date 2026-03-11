package com.azyu.mahallavote.domain;

import com.azyu.mahallavote.domain.enumeration.VoteLotStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_vote_lot")
public class VoteLot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_app_vote_lot")
    @SequenceGenerator(name = "seq_app_vote_lot", sequenceName = "seq_app_vote_lot", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 500)
    @Column(name = "name", length = 500, nullable = false)
    private String name;

    @NotNull
    @Size(max = 1000)
    @Column(name = "open_budget_url", length = 1000, nullable = false)
    private String openBudgetUrl;

    @NotNull
    @Column(name = "target_vote_count", nullable = false)
    private Integer targetVoteCount;

    @Column(name = "current_vote_count")
    private Integer currentVoteCount = 0;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoteLotStatus status = VoteLotStatus.INACTIVE;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoteLot)) return false;
        return id != null && id.equals(((VoteLot) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return (
            "VoteLot{" +
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
