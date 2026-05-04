package com.claim.claim_processing.common.entities.claim;
import com.claim.claim_processing.common.entities.common.RuleTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_VESTING_RULE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimVestingRuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "MEMBER_CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            foreignKey = @ForeignKey(name = "FK_VESTING_RULE_CATEGORY")
    )
    private AgencyCategory category;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "REFUND_TYPE", length = 50)
    private String refundType;

    @Column(name = "MIN_VESTING_MONTHS")
    private Integer minVestingMonths;

    @Column(name = "MAX_VESTING_MONTHS")
    private Integer maxVestingMonths;

    @Column(name = "COMPARISON_TYPE", length = 30)
    private String comparisonType;

    @Column(name = "PAYOUT_RESULT", length = 50)
    private String payoutResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CUTOFF_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_VESTING_RULE_CUTOFF")
    )
    private ClaimVestingCutoffMaster cutoff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "REFUND_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_REFUND_TYPE_TYPE"))
    private VestingRefundType refund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "RULE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_VESTING_RULE_TYPE")
    )
    private RuleTypeMaster ruleType;


    @Column(name = "REMARKS", length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private ActivityEnum isActive = ActivityEnum.Y;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

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