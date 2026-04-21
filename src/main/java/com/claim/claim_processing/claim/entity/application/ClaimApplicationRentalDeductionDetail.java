package com.claim.claim_processing.claim.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import com.claim.claim_processing.common.entities.common.ReviewStatusMaster;

@Entity
@Table(
        name = "CLAIM_APPLICATION_RENTAL_DEDUCTION_DETAIL",
        schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationRentalDeductionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One rental deduction detail belongs to one deduction detail
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "DEDUCTION_DETAIL_ID",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "FK_CARDD_DEDUCTION_DETAIL")
    )
    private ClaimApplicationDeductionDetail deductionDetail;

    @Column(name = "PROPERTY_REFERENCE_ID", length = 100)
    private String propertyReferenceId;

    @Column(name = "TENANT_OR_UNIT_REFERENCE", length = 100)
    private String tenantOrUnitReference;

    @Column(name = "RENTAL_PERIOD_FROM")
    private LocalDate rentalPeriodFrom;

    @Column(name = "RENTAL_PERIOD_TO")
    private LocalDate rentalPeriodTo;

    @Column(name = "MONTHLY_RENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal monthlyRentAmount;

    @Column(name = "TOTAL_RENT_DUE", precision = 15, scale = 2)
    private BigDecimal totalRentDue;

    @Column(name = "TOTAL_RENT_OUTSTANDING", precision = 15, scale = 2)
    private BigDecimal totalRentOutstanding;

    @Column(name = "ADJUSTED_RENT_AMOUNT", precision = 15, scale = 2)
    private BigDecimal adjustedRentAmount;

    @Column(name = "LANDLORD_OR_AGENCY_CODE", length = 100)
    private String landlordOrAgencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "RENT_CLEARANCE_STATUS_ID",
            foreignKey = @ForeignKey(name = "FK_CARDD_RENT_CLEARANCE_STATUS")
    )
    private ReviewStatusMaster rentClearanceStatus;

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
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

}