package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_TDS_TAX_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationTdsTaxDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One TDS tax detail belongs to one deduction detail
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DEDUCTION_DETAIL_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_CATTD_DEDUCTION_DETAIL")
    )
    private ClaimApplicationDeductionDetail deductionDetail;

    @Column(name = "TAXABLE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal taxableAmount;

    @Column(name = "TAX_RATE", precision = 5, scale = 2)
    private BigDecimal taxRate;

    @Column(name = "TAX_AMOUNT", precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "TAX_RULE_CODE", length = 100)
    private String taxRuleCode;

    @Column(name = "TAX_SECTION_CODE", length = 100)
    private String taxSectionCode;

    @Column(name = "PAN_OR_TAX_PAYER_ID", length = 100)
    private String panOrTaxPayerId;

    @Column(name = "FISCAL_YEAR", length = 20)
    private String fiscalYear;

    @Column(name = "TAX_DEDUCTION_DATE")
    private LocalDate taxDeductionDate;

    @Column(name = "TDS_CERTIFICATE_NUMBER", length = 100)
    private String tdsCertificateNumber;

    @Column(name = "CHALLAN_REFERENCE_NUMBER", length = 100)
    private String challanReferenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "TAX_DEPOSIT_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CATTD_TAX_DEPOSIT_STATUS")
    )
    private ReviewStatusMaster taxDepositStatus;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

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