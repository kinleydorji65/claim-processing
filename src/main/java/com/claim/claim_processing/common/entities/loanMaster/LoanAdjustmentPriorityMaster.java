package com.claim.claim_processing.common.entities.loanMaster;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LOAN_ADJUSTMENT_PRIORITY_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA", uniqueConstraints = {
        @UniqueConstraint(name = "UK_LOAN_ADJ_PRIORITY", columnNames = { "LOAN_TYPE_ID" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAdjustmentPriorityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // 🔥 FK → LOAN_TYPE_MASTER
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LOAN_TYPE_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FK_LOAN_ADJ_PRIORITY_LOAN_TYPE"))
    private LoanTypeMaster loanType;

    @Column(name = "PRIORITY_ORDER", nullable = false)
    private Integer priorityOrder;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = ActivityEnum.Y;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}