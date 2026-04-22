package com.claim.claim_processing.claim.entity.calculation;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.common.entities.status_master.CalculationStatusMaster;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(
        name = "CLAIM_APPLICATION_CALCULATION_COMPONENT",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationCalculationComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CALCULATION_SUMMARY_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CACC_CALC_SUMMARY")
    )
    private ClaimApplicationCalculationSummary calculationSummary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CACC_CLAIM_APP")
    )
    private ClaimApplication claimApplication;

    @Column(name = "BENEFICIARY_IDENTIFIER", length = 100)
    private String beneficiaryIdentifier;

    @Column(name = "COMPONENT_TYPE_ID")
    private Long componentTypeId;

    @Column(name = "COMPONENT_CATEGORY_CODE", length = 50)
    private String componentCategoryCode;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "IS_PENSION_RELATED", length = 1)
    @Builder.Default
    private String isPensionRelated = "N";

    @Column(name = "IS_INTEREST_COMPONENT", length = 1)
    @Builder.Default
    private String isInterestComponent = "N";

    @Column(name = "IS_DEDUCTION_COMPONENT", length = 1)
    @Builder.Default
    private String isDeductionComponent = "N";

    @Column(name = "BASE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal baseAmount;

    @Column(name = "ELIGIBLE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal eligibleAmount;

    @Column(name = "CONTRIBUTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal contributionAmount;

    @Column(name = "BALANCE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal balanceAmount;

    @Column(name = "RATE_APPLIED", precision = 10, scale = 4)
    private BigDecimal rateApplied;

    @Column(name = "RATE_TYPE_CODE", length = 50)
    private String rateTypeCode;

    @Column(name = "PERIOD_FROM")
    private LocalDate periodFrom;

    @Column(name = "PERIOD_TO")
    private LocalDate periodTo;

    @Column(name = "CONTRIBUTION_MONTHS")
    private Integer contributionMonths;

    @Column(name = "INTEREST_AMOUNT", precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "GROSS_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal grossComponentAmount;

    @Column(name = "DEDUCTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal deductionAmount;

    @Column(name = "NET_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal netComponentAmount;

    @Column(name = "VERIFIED_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal verifiedComponentAmount;

    @Column(name = "APPROVED_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal approvedComponentAmount;

    @Column(name = "PAID_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal paidComponentAmount;

    @Column(name = "POSTED_COMPONENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal postedComponentAmount;

    @Column(name = "CALCULATION_FORMULA", length = 2000)
    private String calculationFormula;

    @Column(name = "FORMULA_DESCRIPTION", length = 2000)
    private String formulaDescription;

    @Column(name = "RULE_REFERENCE", length = 500)
    private String ruleReference;

    @Column(name = "CALCULATION_NOTES", length = 2000)
    private String calculationNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CALCULATION_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CACC_CALC_STATUS")
    )
    private CalculationStatusMaster calculationStatus;

    @Column(name = "IS_ACTIVE", length = 1)
    @Builder.Default
    private String isActive = "Y";

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