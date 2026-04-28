package com.claim.claim_processing.claim.DTO.response.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentModeResponseDto;
import com.claim.claim_processing.common.DTO.response.payment.PaymentStatusResponseDto;
import com.claim.claim_processing.common.DTO.response.statusMaster.ReversalStatusResponseDto;
import com.claim.claim_processing.common.entities.common.activityEnum.ActivityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApplicationPaymentResponseDto {

    private Long id;

    private Long claimApplicationId;
    private Long calculationSummaryId;

    private String paymentReferenceNumber;
    private String paymentBatchNumber;

    private PaymentStatusResponseDto paymentStatus;
    private PaymentModeResponseDto paymentMode;

    private ClaimantTypeResponseDto payeeType;
    private String payeeName;

    // ---------- Bank ----------
    private Long selectedBankDetailId;
    private String bankName;

    // ---------- Amounts ----------
    private BigDecimal approvedAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netPayableAmount;

    private String currencyCode;

    // ---------- Dates ----------
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentInitiationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentCompletionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentReversalDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentCancellationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentFailureDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentRetryDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentRefundDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentReturnDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentInitiatedAt;
    private String paymentInitiatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentProcessedAt;
    private String paymentProcessedBy;

    // ---------- Status ----------
    private String paymentFailureReason;
    private Integer retryCount;

    private String financeRemarks;

    private ActivityEnum isReversalRequired;

    private ReversalStatusResponseDto reversalStatus;

    private ActivityEnum isActive;

    // ---------- Audit ----------
    private String createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private String updatedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
