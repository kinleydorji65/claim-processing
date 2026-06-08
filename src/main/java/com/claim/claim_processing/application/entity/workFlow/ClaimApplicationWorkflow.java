package com.claim.claim_processing.application.entity.workFlow;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.ActionMaster;
import com.claim.claim_processing.common.entities.common.NppfOfficeMaster;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_WORKFLOW",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CAW_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_STAGE_ID")
    private StageMaster fromStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_STAGE_ID")
    private StageMaster toStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_STATUS_ID")
    private StatusMaster fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_STATUS_ID")
    private StatusMaster toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTION_ID")
    private ActionMaster action;

    @Column(name = "REASON", length = 1000)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFICE_ID")
    private NppfOfficeMaster office;

    @Column(name = "ACTION_BY", length = 100)
    private String actionBy;

    @Column(name = "ACTION_AT")
    private Timestamp actionAt;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (actionAt == null) {
            actionAt = now;
        }

        createdAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        actionAt = new Timestamp(System.currentTimeMillis());
    }
}