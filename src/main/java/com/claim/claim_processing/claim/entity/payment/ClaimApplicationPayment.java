package com.claim.claim_processing.claim.entity.payment;

import com.claim.claim_processing.claim.entity.application.ClaimApplication;
import com.claim.claim_processing.claim.entity.application.ClaimApplicationBankDetail;
import com.claim.claim_processing.claim.entity.calculation.ClaimApplicationCalculationSummary;
import com.claim.claim_processing.common.entities.beneficiaryMaster.ClaimantTypeMaster;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentModeMaster;
import com.claim.claim_processing.common.entities.paymentMaster.PaymentStatusMaster;
import com.claim.claim_processing.common.entities.statusMaster.ReversalStatusMaster;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "CLAIM_APPLICATION_PAYMENT", schema = "PPFMS_CLAIMS_WORKFLOW_SERVICE_SCHEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationPayment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CLAIM_APPLICATION_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CAP_CLAIM_APP"))
        private ClaimApplication claimApplication;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "CALCULATION_SUMMARY_ID", foreignKey = @ForeignKey(name = "FK_CAP_CALC_SUMMARY"))
        private ClaimApplicationCalculationSummary calculationSummary;

        @Column(name = "PAYMENT_REFERENCE_NUMBER", length = 100)
        private String paymentReferenceNumber;

        @Column(name = "PAYMENT_BATCH_NUMBER", length = 100)
        private String paymentBatchNumber;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYMENT_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAP_PAYMENT_STATUS"))
        private PaymentStatusMaster paymentStatus;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYMENT_MODE_ID", foreignKey = @ForeignKey(name = "FK_CAP_PAYMENT_MODE"))
        private PaymentModeMaster paymentMode;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "PAYEE_TYPE_ID", foreignKey = @ForeignKey(name = "FK_CAP_PAYEE_TYPE"))
        private ClaimantTypeMaster payeeType;

        @Column(name = "PAYEE_NAME", length = 200)
        private String payeeName;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "SELECTED_BANK_DETAIL_ID", foreignKey = @ForeignKey(name = "FK_CAP_BANK_DETAIL"))
        private ClaimApplicationBankDetail selectedBankDetail;

        @Column(name = "APPROVED_AMOUNT", precision = 15, scale = 2)
        private BigDecimal approvedAmount;

        @Column(name = "DEDUCTION_AMOUNT", precision = 15, scale = 2)
        private BigDecimal deductionAmount;

        @Column(name = "NET_PAYABLE_AMOUNT", precision = 15, scale = 2)
        private BigDecimal netPayableAmount;

        @Column(name = "CURRENCY_CODE", length = 10)
        private String currencyCode;

        @Column(name = "PAYMENT_DATE")
        private LocalDate paymentDate;

        @Column(name = "PAYMENT_INITIATED_BY", length = 100)
        private String paymentInitiatedBy;

        @Column(name = "PAYMENT_INITIATED_AT")
        private Timestamp paymentInitiatedAt;

        @Column(name = "PAYMENT_PROCESSED_BY", length = 100)
        private String paymentProcessedBy;

        @Column(name = "PAYMENT_PROCESSED_AT")
        private Timestamp paymentProcessedAt;

        @Column(name = "PAYMENT_FAILURE_REASON", length = 1000)
        private String paymentFailureReason;

        @Column(name = "RETRY_COUNT")
        private Integer retryCount;

        @Column(name = "FINANCE_REMARKS", length = 2000)
        private String financeRemarks;

        @Column(name = "IS_REVERSAL_REQUIRED", length = 1)
        @Enumerated(EnumType.STRING)
        @Builder.Default
        private ActivityEnum isReversalRequired = ActivityEnum.N;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "REVERSAL_STATUS_ID", foreignKey = @ForeignKey(name = "FK_CAP_REVERSAL_STATUS"))
        private ReversalStatusMaster reversalStatus;

        @Enumerated(EnumType.STRING)
        @Column(name = "IS_ACTIVE", length = 1)
        @Builder.Default
        private ActivityEnum isActive = ActivityEnum.Y;

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