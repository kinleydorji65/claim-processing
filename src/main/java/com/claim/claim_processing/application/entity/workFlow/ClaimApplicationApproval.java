package com.claim.claim_processing.application.entity.workFlow;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(
        name = "CLAIM_APPLICATION_APPROVAL",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_CAA_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "APPROVAL_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CAA_APPROVAL_STATUS")
    )
    private StatusMaster approvalStatus;

    @Column(name = "REMARKS", length = 2000)
    private String remarks;


    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_AT")
    private Timestamp approvedAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CLAIMED_BY", length = 100)
    private String claimedBy;

    @Column(name = "REJECTED_BY", length = 100)
    private String rejectedBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}