package com.claim.claim_processing.master.entities.partial;

import com.claim.claim_processing.master.entities.others.AgencyCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    // MANY RULES → ONE CATEGORY
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CATEGORY_ID",
            referencedColumnName = "CATEGORY_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PARTIAL_WITHDRAWAL_RULE_CATEGORY")
    )
    private AgencyCategory category;

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

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @Builder.Default
    private String isActive = "Y";

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
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
        validateAccumulation();
    }

    private void handleDefaults() {
        if (isActive == null) {
            isActive = "Y";
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
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