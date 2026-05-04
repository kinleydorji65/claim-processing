package com.claim.claim_processing.common.entities.partial;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.entities.others.agency.agencyRelated.AgencyCategory;

@Entity
@Table(name = "PARTIAL_WITHDRAWAL_RULE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialWithdrawalRuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // MANY RULES → ONE CATEGORY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PARTIAL_WITHDRAWAL_RULE_CATEGORY")
    )
    private AgencyCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "REASON_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PARTIAL_WITHDRAWAL_RULE_REASON")
    )
    private PartialWithdrawalReasonMaster reason;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ACCUMULATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ACCUMULATION_TYPE")
    )
    private PartialWithdrawalAccumulationMaster accumulation;

    @Column(name = "MAX_WITHDRAWAL_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal maxWithdrawalPercentage;

    // Boolean mapped to Y/N
    @Convert(converter = YesNoConverter.class)
    @Column(name = "PF_ACCUMULATION")
    private Boolean pfAccumulation;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "TOTAL_ACCUMULATION_VALUE")
    private Boolean totalAccumulationValue;

    @Column(name = "NUMBER_OF_CONTRIBUTION_MONTHS")
    private Integer numberOfContributionMonths;

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

    // ✅ Enforce logic at application level
    @PrePersist
    public void prePersist() {
        handleDefaults();
        validateAccumulation();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        validateAccumulation();
    }

    private void handleDefaults() {
        if (isActive == null) {
            isActive = ActivityEnum.Y;
        }
    }

    private void validateAccumulation() {
        if (Boolean.TRUE.equals(pfAccumulation)) {
            totalAccumulationValue = false;
        } else if (Boolean.TRUE.equals(totalAccumulationValue)) {
            pfAccumulation = false;
        }
    }
}