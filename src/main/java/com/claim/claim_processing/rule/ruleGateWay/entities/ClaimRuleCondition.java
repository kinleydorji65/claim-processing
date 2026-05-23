package com.claim.claim_processing.rule.ruleGateWay.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.partial.PartialWithdrawalAccumulationMaster;

@Entity
@Table(name = "CLAIM_RULE_CONDITION", schema = "PPFMS_MASTER_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimRuleCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID", nullable = false)
    private ClaimRuleMaster rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCUMULATION_ID", nullable = false)
    private PartialWithdrawalAccumulationMaster accumulation;

    @Column(name = "SCHEME_TYPE_ID")
    private Long schemeTypeId;

    @Column(name = "MIN_MONTHS")
    private Long minMonths;

    @Column(name = "MAX_MONTHS")
    private Long maxMonths;

    @Column(name = "PRIORITY_ORDER")
    private Long priorityOrder;

    @Column(name = "WITHDRAWAL_PERCENTAGE")
    private Double withdrawalPercentage;

    @Column(name = "COMPARISON_TYPE", length = 30)
    private String comparisonType;

    @Column(name = "TOTAL_NUMBER_CONTRIBUTION", length = 30)
    private Double totalNumberContribution;

    @Column(name = "IS_ACTIVE", length = 1)
    private String isActive;

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
            this.isActive = "Y";
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}