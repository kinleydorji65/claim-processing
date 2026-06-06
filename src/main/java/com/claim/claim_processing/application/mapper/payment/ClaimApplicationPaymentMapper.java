package com.claim.claim_processing.application.mapper.payment;

import com.claim.claim_processing.application.DTO.response.payment.ClaimApplicationPaymentResponseDto;
import com.claim.claim_processing.application.entity.payment.ClaimApplicationPayment;
import com.claim.claim_processing.common.DTO.response.others.StatusMasterResponseDto;
import com.claim.claim_processing.common.mapper.beneficiary.ClaimantTypeMapper;
import com.claim.claim_processing.common.mapper.others.StatusMapper;
import com.claim.claim_processing.common.mapper.payment.PaymentModeMasterMapper;
import com.claim.claim_processing.common.mapper.payment.PaymentStatusMasterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationPaymentMapper {

    private final PaymentStatusMasterMapper paymentStatusMapper;
    private final PaymentModeMasterMapper paymentModeMapper;
    private final ClaimantTypeMapper claimantTypeMapper;
    private final StatusMapper statusMapper;

    public ClaimApplicationPaymentResponseDto toResponse(
            ClaimApplicationPayment entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationPaymentResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getId()
                                : null
                )

                .applicationNumber(
                        entity.getClaimApplication() != null
                                ? entity.getClaimApplication().getApplicationNumber()
                                : null
                )

                .calculationSummaryId(
                        entity.getCalculationSummary() != null
                                ? entity.getCalculationSummary().getId()
                                : null
                )

                .paymentReferenceNumber(entity.getPaymentReferenceNumber())
                .paymentBatchNumber(entity.getPaymentBatchNumber())

                .paymentStatus(
                        entity.getPaymentStatus() != null
                                ? paymentStatusMapper.toResponseDto(entity.getPaymentStatus())
                                : null
                )

                .paymentMode(
                        entity.getPaymentMode() != null
                                ? paymentModeMapper.toResponseDto(entity.getPaymentMode())
                                : null
                )

                .payeeType(
                        entity.getPayeeType() != null
                                ? claimantTypeMapper.toResponseDto(entity.getPayeeType())
                                : null
                )

                .payeeName(entity.getPayeeName())

                .selectedBankDetailId(
                        entity.getSelectedBankDetail() != null
                                ? entity.getSelectedBankDetail().getId()
                                : null
                )

                .approvedAmount(entity.getApprovedAmount())
                .deductionAmount(entity.getDeductionAmount())
                .netPayableAmount(entity.getNetPayableAmount())

                .currencyCode(entity.getCurrencyCode())

                .paymentDate(entity.getPaymentDate())

                .paymentInitiatedBy(entity.getPaymentInitiatedBy())
                .paymentInitiatedAt(entity.getPaymentInitiatedAt())

                .paymentProcessedBy(entity.getPaymentProcessedBy())
                .paymentProcessedAt(entity.getPaymentProcessedAt())

                .paymentFailureReason(entity.getPaymentFailureReason())
                .retryCount(entity.getRetryCount())

                .financeRemarks(entity.getFinanceRemarks())

                .isReversalRequired(entity.getIsReversalRequired())

                .reversalStatus(
                        entity.getReversalStatus() != null
                                ? StatusMasterResponseDto.builder()
                                .statusId(entity.getReversalStatus().getStatusId())
                                .statusName(entity.getReversalStatus().getStatusName())
                                .build()
                                : null
                )

                .isActive(entity.getIsActive())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())

                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }
}