package com.claim.claim_processing.application.entity.calculation;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleMaster;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_APPLICATION_RULE_EVALUATION", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
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
    @JoinColumn(name = "CALCULATION_SUMMARY_ID", nullable = false)
    private ClaimApplicationCalculationSummary calculationSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_TYPE_ID")
    private ClaimRuleMaster rule;

    @Column(name = "RULE_CODE", length = 100)
    private String ruleCode;

    @Column(name = "RULE_NAME", length = 300)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_RULE_MATCHED", length = 1)
    @Builder.Default
    private ActivityEnum isRuleMatched = ActivityEnum.N;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_RULE_APPLIED", length = 1)
    @Builder.Default
    private ActivityEnum isRuleApplied = ActivityEnum.N;

    @Column(name = "RESULT_MESSAGE", length = 2000)
    private String resultMessage;

    @Lob
    @Column(name = "INPUT_SNAPSHOT_JSON")
    private String inputSnapshotJson;

    @Lob
    @Column(name = "OUTPUT_SNAPSHOT_JSON")
    private String outputSnapshotJson;

    @Column(name = "EVALUATED_AT")
    private Timestamp evaluatedAt;

    @Column(name = "EVALUATED_BY", length = 100)
    private String evaluatedBy;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "ruleEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimApplicationCalculationComponent> components = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;

        if (evaluatedAt == null) {
            evaluatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}