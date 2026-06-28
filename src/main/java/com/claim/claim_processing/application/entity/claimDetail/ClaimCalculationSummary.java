package com.claim.claim_processing.application.entity.claimDetail;

import com.claim.claim_processing.common.entities.common.StageMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.others.StatusMaster;
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

    @Column(name = "ACTUAL_AMOUNT_CALCULATED", precision = 15, scale = 2)
    private BigDecimal actualAmountCalculated;

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

    @Column(name = "RECOMMENDED_BENEFIT_TYPE", length = 2000)
    private String recommendedBenefitType;

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
}