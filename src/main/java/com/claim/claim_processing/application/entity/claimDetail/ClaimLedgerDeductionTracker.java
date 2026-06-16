package com.claim.claim_processing.application.entity.claimDetail;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;

@Entity
@Table(name = "CLAIM_LEDGER_DEDUCTION_TRACKER", schema = "PPFMS_CLAIM_PROCESSING_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimLedgerDeductionTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_ID", nullable = false, unique = true)
    private ClaimDetail claimDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIM_TYPE_ID", nullable = false)
    private ClaimTypeMaster claimType;

    @Column(name = "NPPF_NUMBER", nullable = false, length = 100)
    private String nppfNumber;

    @Column(name = "TOTAL_PF_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalPfAmount;

    @Column(name = "TOTAL_PC_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalPcAmount;

    @Column(name = "TOTAL_DEDUCTION_AMOUNT", precision = 15, scale = 2)
    private BigDecimal totalDeductionAmount;

    @Column(name = "BALANCE_AMOUNT", precision = 15, scale = 2)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "IS_COMPLETED", length = 1)
    @Builder.Default
    private ActivityEnum isCompleted = ActivityEnum.N;

    @Column(name = "COMPLETED_AT")
    private Timestamp completedAt;

    @Column(name = "COMPLETED_BY", length = 100)
    private String completedBy;

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

        if (totalPfAmount == null) {
            totalPfAmount = BigDecimal.ZERO;
        }

        if (totalPcAmount == null) {
            totalPcAmount = BigDecimal.ZERO;
        }

        if (totalDeductionAmount == null) {
            totalDeductionAmount = BigDecimal.ZERO;
        }

        if (isCompleted == null) {
            isCompleted = ActivityEnum.N;
        }

        if (isActive == null) {
            isActive = ActivityEnum.Y;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
        if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            isCompleted = ActivityEnum.Y;

            if (completedAt == null) {
                completedAt = new Timestamp(System.currentTimeMillis());
            }
        } else {
            isCompleted = ActivityEnum.N;
        }
    }
}
