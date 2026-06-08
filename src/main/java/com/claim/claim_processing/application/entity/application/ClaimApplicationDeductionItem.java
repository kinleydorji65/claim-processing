package com.claim.claim_processing.application.entity.application;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CLAIM_APPLICATION_DEDUCTION_ITEM",
        schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationDeductionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEDUCTION_DETAIL_ID", nullable = false)
    private ClaimApplicationDeductionDetail deductionDetail;

    @Column(name = "DEDUCTION_CATEGORY", length = 50)
    private String deductionCategory; // LOAN / RENTAL / TAX / OTHER

    @Column(name = "REFERENCE_NUMBER", length = 100)
    private String referenceNumber; // loan no, property no, tax ref

    @Column(name = "REFERENCE_NAME", length = 200)
    private String referenceName; // Housing Loan, Rental, TDS

    @Column(name = "OUTSTANDING_AMOUNT", precision = 15, scale = 2)
    private BigDecimal outstandingAmount;

    @Column(name = "DEDUCTED_AMOUNT", precision = 15, scale = 2)
    private BigDecimal deductedAmount;

    @Column(name = "REMAINING_AMOUNT", precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "PRIORITY_ORDER")
    private Integer priorityOrder;

    @Column(name = "REMARKS", length = 1000)
    private String remarks;

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
