package com.claim.claim_processing.claim.DTO.application.response.payment;

import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationPaymentResponseDto {

    private Long id;

    // -------------------------------
    // BASIC
    // -------------------------------
    private Long claimApplicationId;
    private Long calculationSummaryId;

    private String paymentReferenceNumber;
    private String paymentBatchNumber;

    // -------------------------------
    // PAYMENT STATUS
    // -------------------------------
    private Long paymentStatusId;
    private String paymentStatusName;

    // -------------------------------
    // PAYMENT MODE
    // -------------------------------
    private Long paymentModeId;
    private String paymentModeName;

    // -------------------------------
    // PAYEE
    // -------------------------------
    private Long payeeTypeId;
    private String payeeTypeName;

    private String payeeName;

    // -------------------------------
    // BANK
    // -------------------------------
    private Long bankDetailId;
    private String bankAccountNumber;
    private String bankName;

    // -------------------------------
    // AMOUNTS
    // -------------------------------
    private BigDecimal approvedAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netPayableAmount;

    private String currencyCode;

    // -------------------------------
    // PAYMENT TIMELINE
    // -------------------------------
    private LocalDate paymentDate;

    private String paymentInitiatedBy;
    private Timestamp paymentInitiatedAt;

    private String paymentProcessedBy;
    private Timestamp paymentProcessedAt;

    // -------------------------------
    // FAILURE / RETRY
    // -------------------------------
    private String paymentFailureReason;
    private Integer retryCount;

    private String financeRemarks;

    // -------------------------------
    // REVERSAL
    // -------------------------------
    private ActivityEnum isReversalRequired;

    private Long reversalStatusId;
    private String reversalStatusName;

    // -------------------------------
    // AUDIT
    // -------------------------------
    private ActivityEnum isActive;

    private String createdBy;
    private Timestamp createdAt;

    private String updatedBy;
    private Timestamp updatedAt;
}
