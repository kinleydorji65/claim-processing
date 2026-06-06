package com.claim.claim_processing.application.DTO.request.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimApplicationPaymentRequestDto {

    private Long claimApplicationId;
    private Long calculationSummaryId;

    private String paymentReferenceNumber;
    private String paymentBatchNumber;

    private Long paymentStatusId;
    private Long paymentModeId;
    private Long payeeTypeId;

    private String payeeName;
    private Long selectedBankDetailId;

    private BigDecimal approvedAmount;
    private BigDecimal deductionAmount;
    private BigDecimal netPayableAmount;

    private String currencyCode;
    private LocalDate paymentDate;

    private String paymentInitiatedBy;
    private String paymentProcessedBy;

    private String paymentFailureReason;
    private Integer retryCount;
    private String financeRemarks;

    private Long reversalStatusId;

    private String createdBy;
}