package com.claim.claim_processing.claim.entity.calculation;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "CLAIM_APPLICATION_RULE_EVALUATION", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationRuleEvaluation {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CALCULATION_SUMMARY_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CARE_CALC_SUMMARY"))
        private ClaimApplicationCalculationSummary calculationSummary;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CARE_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "RULE_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CARE_RULE_TYPE"))
        private RuleTypeMaster ruleType;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "EVALUATION_STAGE_ID", foreignKey = @ForeignKey(name = "FK_CARE_EVAL_STAGE"))
        private StageMaster evaluationStage;

        @Column(name = "EVALUATION_TRIGGER_TYPE_ID")
        private Long evaluationTriggerTypeId;

        @Column(name = "IS_RULE_MATCHED", length = 1)
        @Enumerated(EnumType.STRING)
        @Builder.Default
        private ActivityEnum isRuleMatched = ActivityEnum.N;

        @Column(name = "IS_RULE_APPLIED", length = 1)
        @Enumerated(EnumType.STRING)
        @Builder.Default
        private ActivityEnum isRuleApplied = ActivityEnum.N;

        @Column(name = "IS_MANUAL_OVERRIDE", length = 1)
        @Enumerated(EnumType.STRING)
        @Builder.Default
        private ActivityEnum isManualOverride = ActivityEnum.N;

        @Column(name = "OVERRIDE_REASON", length = 1000)
        private String overrideReason;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "EVALUATION_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CARE_EVAL_STATUS"))
        private ReviewStatusMaster evaluationStatus;

        @Column(name = "EVALUATION_RESULT_CODE", length = 100)
        private String evaluationResultCode;

        @Column(name = "EVALUATION_RESULT_MESSAGE", length = 2000)
        private String evaluationResultMessage;

        @Lob
        @Column(name = "INPUT_SNAPSHOT_JSON")
        private String inputSnapshotJson;

        @Lob
        @Column(name = "OUTPUT_SNAPSHOT_JSON")
        private String outputSnapshotJson;

        @Column(name = "RULE_FORMULA", length = 2000)
        private String ruleFormula;

        @Lob
        @Column(name = "RULE_PARAMETERS_JSON")
        private String ruleParametersJson;

        @Column(name = "EFFECTIVE_FROM")
        private LocalDate effectiveFrom;

        @Column(name = "EFFECTIVE_TO")
        private LocalDate effectiveTo;

        @Column(name = "EVALUATED_BY", length = 100)
        private String evaluatedBy;

        @Column(name = "EVALUATED_AT")
        private Timestamp evaluatedAt;

        @Column(name = "REMARKS", length = 1000)
        private String remarks;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

        @Column(name = "CREATED_BY", length = 100)
        private String createdBy;

        @Column(name = "CREATED_AT", insertable = false, updatable = false)
        private Timestamp createdAt;

        @Column(name = "UPDATED_BY", length = 100)
        private String updatedBy;

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