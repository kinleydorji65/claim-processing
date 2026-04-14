package com.claim.claim_processing.master.entities.claim;

import com.claim.claim_processing.master.entities.contribution.SchemeMaster;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CLAIM_ELIGIBILITY_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEligibilityMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "RULE_NAME", nullable = false, length = 200)
    private String ruleName;

    @Column(name = "CLAIM_CATEGORY_CODE", length = 50)
    private String claimCategoryCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CLAIM_CIRCUMSTANCE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_CIRCUMSTANCE")
    )
    private ClaimCircumstanceMaster claimCircumstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "CESSATION_TYPE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_CESSATION_TYPE")
    )
    private CessationTypeMaster cessationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "SCHEME_TYPE_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_CLAIM_ELIGIBILITY_SCHEME_TYPE")
    )
    private SchemeMaster schemeType;

    @Column(name = "MIN_CONTRIBUTION_MONTHS")
    private Integer minContributionMonths;

    @Column(name = "MAX_CONTRIBUTION_MONTHS")
    private Integer maxContributionMonths;

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
        if (this.isActive == null) {
            this.isActive = "Y";
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}