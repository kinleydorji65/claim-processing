package com.claim.claim_processing.master.entities.partial;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "RULE_NAME", nullable = false, length = 200)
    private String ruleName;

    @Column(name = "WITHDRAWAL_CATEGORY", length = 50)
    private String withdrawalCategory;

    @Column(name = "MAX_WITHDRAWAL_PERCENTAGE", precision = 5, scale = 2)
    private BigDecimal maxWithdrawalPercentage;

    @Column(name = "MIN_PF_ACCUMULATION", precision = 18, scale = 2)
    private BigDecimal minPfAccumulation;

    @Column(name = "MIN_TOTAL_ACCUMULATION", precision = 18, scale = 2)
    private BigDecimal minTotalAccumulation;

    @Column(name = "MIN_CONTRIBUTION_MONTHS")
    private Integer minContributionMonths;

    @Column(name = "UNEMPLOYMENT_MIN_MONTHS")
    private Integer unemploymentMinMonths;

    @Column(name = "ACTIVE_EMPLOYMENT_ALLOWED", length = 1)
    private String activeEmploymentAllowed = "N";

    @Column(name = "DUPLICATE_ACTIVE_REQUEST_ALLOWED", length = 1)
    private String duplicateActiveRequestAllowed = "N";

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    private String isActive = "Y";

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
        if (isActive == null) {
            isActive = "Y";
        }
        if (activeEmploymentAllowed == null) {
            activeEmploymentAllowed = "N";
        }
        if (duplicateActiveRequestAllowed == null) {
            duplicateActiveRequestAllowed = "N";
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}