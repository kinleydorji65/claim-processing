package com.claim.claim_processing.claim.entity.workFlow;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.DecisionMaster;
import com.claim.claim_processing.common.entities.common.NppfOfficeMaster;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.WorkflowReasonMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_WORKFLOW",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CAW_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @Column(name = "WORKFLOW_LEVEL")
    private Integer workflowLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "WORKFLOW_STAGE_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_STAGE")
    )
    private StageMaster workflowStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "FROM_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_FROM_STATUS")
    )
    private StatusMaster fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "TO_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_TO_STATUS")
    )
    private StatusMaster toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "ACTION_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_ACTION")
    )
    private ActionMaster action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DECISION_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_DECISION")
    )
    private DecisionMaster decision;

    @Column(name = "RETURN_REASON", length = 1000)
    private String returnReason;

    @Column(name = "REJECTION_REASON", length = 1000)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "APPROVAL_REASON_ID",
            foreignKey = @ForeignKey(name = "FK_CAW_APPROVAL_REASON")
    )
    private WorkflowReasonMaster approvalReason;

    @Column(name = "ACTION_BY", length = 100)
    private String actionBy;

    @Column(name = "ACTION_AT")
    private Timestamp actionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "OFFICE_ID",
            foreignKey = @ForeignKey(name = "FK_CACS_NPPF_OFFICE")
    )
    private NppfOfficeMaster offices;

    @Column(name = "REFERENCE_NUMBER", length = 100)
    private String referenceNumber;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
