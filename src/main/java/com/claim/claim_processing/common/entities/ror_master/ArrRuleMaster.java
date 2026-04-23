package com.claim.claim_processing.common.entities.ror_master;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ARR_RULE_MASTER", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrRuleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "RULE_CODE", nullable = false, unique = true, length = 50)
    private String ruleCode;

    @Column(name = "RULE_NAME", nullable = false, length = 200)
    private String ruleName;

    @Column(name = "SCHEME_TYPE_ID")
    private Long schemeTypeId;

    @Column(name = "MEMBER_CATEGORY_ID", length = 50)
    private String memberCategoryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CREDIT_METHOD_ID", referencedColumnName = "ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ARR_CREDIT_METHOD"))
    private CreditMethodMaster creditMethod;

    @Column(name = "INCLUDE_PF_MC", nullable = false, length = 1)
    @Builder.Default
    private String includePfMc = "N";

    @Column(name = "INCLUDE_PF_EC", nullable = false, length = 1)
    @Builder.Default
    private String includePfEc = "N";

    @Column(name = "INCLUDE_PF_GC", nullable = false, length = 1)
    @Builder.Default
    private String includePfGc = "N";

    @Column(name = "INCLUDE_PF_VC", nullable = false, length = 1)
    @Builder.Default
    private String includePfVc = "N";

    @Column(name = "INCLUDE_PF_INTEREST", nullable = false, length = 1)
    @Builder.Default
    private String includePfInterest = "N";

    @Column(name = "INCLUDE_PC_MC", nullable = false, length = 1)
    @Builder.Default
    private String includePcMc = "N";

    @Column(name = "INCLUDE_PC_EC", nullable = false, length = 1)
    @Builder.Default
    private String includePcEc = "N";

    @Column(name = "INCLUDE_PC_INTEREST", nullable = false, length = 1)
    @Builder.Default
    private String includePcInterest = "N";

    @Column(name = "IS_PENSION_BALANCE_INCLUDED", nullable = false, length = 1)
    @Builder.Default
    private String isPensionBalanceIncluded = "N";

    @Column(name = "FORMULA_EXPRESSION", length = 1000)
    private String formulaExpression;

    @Column(name = "ROUNDING_METHOD_CODE", length = 50)
    private String roundingMethodCode;

    @Column(name = "PRIORITY_ORDER")
    @Builder.Default
    private Integer priorityOrder = 1;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @Column(name = "REMARKS", length = 500)
    private String remarks;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
    @Builder.Default
    private String isActive = "Y";

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
        if (this.includePfMc == null)
            this.includePfMc = "N";
        if (this.includePfEc == null)
            this.includePfEc = "N";
        if (this.includePfGc == null)
            this.includePfGc = "N";
        if (this.includePfVc == null)
            this.includePfVc = "N";
        if (this.includePfInterest == null)
            this.includePfInterest = "N";
        if (this.includePcMc == null)
            this.includePcMc = "N";
        if (this.includePcEc == null)
            this.includePcEc = "N";
        if (this.includePcInterest == null)
            this.includePcInterest = "N";
        if (this.isPensionBalanceIncluded == null)
            this.isPensionBalanceIncluded = "N";
        if (this.priorityOrder == null)
            this.priorityOrder = 1;
        if (this.isActive == null)
            this.isActive = "Y";
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
