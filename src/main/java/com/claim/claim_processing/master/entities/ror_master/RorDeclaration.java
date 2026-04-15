package com.claim.claim_processing.master.entities.ror_master;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ROR_DECLARATION", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RorDeclaration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DECLARATION_CODE", nullable = false, unique = true, length = 50)
    private String declarationCode;

    @Column(name = "FINANCIAL_YEAR", nullable = false, length = 20)
    private String financialYear;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "DECLARATION_TYPE_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ROR_DECL_TYPE")
    )
    private RorDeclarationTypeMaster declarationType;

    @Column(name = "SCHEME_TYPE_ID")
    private Long schemeTypeId;

    @Column(name = "MEMBER_CATEGORY_ID", length = 50)
    private String memberCategoryId;

    @Column(name = "SURPLUS_AMOUNT", precision = 18, scale = 2)
    private BigDecimal surplusAmount;

    @Column(name = "TOTAL_FUND_BALANCE", precision = 18, scale = 2)
    private BigDecimal totalFundBalance;

    @Column(name = "DECLARED_ROR_PERCENT", nullable = false, precision = 8, scale = 4)
    private BigDecimal declaredRorPercent;

    @Column(name = "DECLARATION_DATE", nullable = false)
    private LocalDate declarationDate;

    @Column(name = "EFFECTIVE_FROM")
    private LocalDate effectiveFrom;

    @Column(name = "EFFECTIVE_TO")
    private LocalDate effectiveTo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "APPROVAL_STATUS_ID",
            referencedColumnName = "ID",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ROR_APPROVAL_STATUS")
    )
    private RorApprovalStatusMaster approvalStatus;

    @Column(name = "APPROVED_BY", length = 100)
    private String approvedBy;

    @Column(name = "APPROVED_AT")
    private LocalDateTime approvedAt;

    @Column(name = "REMARKS", length = 500)
    private String remarks;

    @Column(name = "IS_ACTIVE", nullable = false, length = 1)
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
