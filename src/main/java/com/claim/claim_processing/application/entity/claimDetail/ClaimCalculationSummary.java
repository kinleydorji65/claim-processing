package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CLAIM_CALCULATION_SUMMARY", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimCalculationSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_ID", nullable = true)
    private ClaimDetail claimDetail;

    @Column(name = "CALCULATION_EFFECTIVE_DATE")
    private LocalDate calculationEffectiveDate;

    @Column(name = "FINAL_PAYABLE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal finalPayableAmount;

    @Column(name = "TOTAL_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "IS_PF_ELIGIBLE", length = 1)
    @Builder.Default
    private String isPfEligible = "N";

    @Column(name = "IS_PENSION_ELIGIBLE", length = 1)
    @Builder.Default
    private String isPensionEligible = "N";

    @Column(name = "TOTAL_CONTRIBUTION_MONTH", precision = 15, scale = 2)
    private Integer totalContributionMonth;

    @Column(name = "TOTAL_NON_CONTRIBUTION_MONTH", precision = 15, scale = 2)
    private Integer totalNonContributionMonth;

    @Column(name = "TOTAL_PF_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalPfAmount;

    @Column(name = "TOTAL_PENSION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalPensionAmount;

    @Column(name = "TOTAL_PF_INTEREST", precision = 15, scale = 2)
    private BigDecimal totalPfInterest;

    @Column(name = "TOTAL_PENSION_INTEREST", precision = 15, scale = 2)
    private BigDecimal totalPensionInterest;

    @Column(name = "BENEFIT_TYPE", length = 2000)
    private String recommendedBenefitType;

    // ================================================================
    // EXCESS SERVICE FIELDS
    // ================================================================

    @Column(name = "EXCESS_OPENING_BALANCE", precision = 15, scale = 2)
    private BigDecimal excessOpeningBalance;

    @Column(name = "EXCESS_SERVICE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal excessServiceAmount;

    @Column(name = "EXCESS_CUTOFF_DATE")
    private LocalDate excessCutoffDate;

    @Column(name = "EXCESS_START_DATE")
    private LocalDate excessStartDate;

    @Column(name = "EXCESS_END_DATE")
    private LocalDate excessEndDate;

    @Column(name = "EXCESS_TOTAL_CONTRIBUTIONS", precision = 15, scale = 2)
    private BigDecimal excessTotalContributions;

    @Column(name = "EXCESS_TOTAL_INTEREST", precision = 15, scale = 2)
    private BigDecimal excessTotalInterest;

    @Column(name = "EXCESS_EOL_MONTHS")
    private Integer excessEolMonths;

    @Column(name = "CREATED_BY", length = 100)
    private String createdBy;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "UPDATED_BY", length = 100)
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "calculationSummary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClaimRuleEvaluation> ruleEvaluations = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // ================================================================
    // HELPER METHODS
    // ================================================================

    public boolean hasExcessService() {
        return excessServiceAmount != null 
                && excessServiceAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getExcessClosingBalance() {
        if (excessOpeningBalance == null) return BigDecimal.ZERO;
        BigDecimal total = excessOpeningBalance;
        if (excessTotalContributions != null) total = total.add(excessTotalContributions);
        if (excessTotalInterest != null) total = total.add(excessTotalInterest);
        return total;
    }
}