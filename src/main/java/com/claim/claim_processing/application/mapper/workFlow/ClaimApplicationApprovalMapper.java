package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.common.mapper.common.DecisionMapper;
import com.claim.claim_processing.common.mapper.others.StatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationApprovalMapper {

    private final DecisionMapper decisionMapper;
    private final StatusMapper statusMapper;

    public ClaimApplicationApprovalResponseDto toResponse(
            ClaimApplicationApproval entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationApprovalResponseDto.builder()
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

                .approvalLevel(entity.getApprovalLevel())

                .approvalStatus(
                        entity.getApprovalStatus() != null
                                ? statusMapper.toDto(entity.getApprovalStatus())
                                : null
                )

                .approvalDecision(
                        entity.getApprovalDecision() != null
                                ? decisionMapper.toResponseDto(entity.getApprovalDecision())
                                : null
                )

                .approvedAmount(entity.getApprovedAmount())
                .approvedPfAmount(entity.getApprovedPfAmount())
                .approvedPensionAmount(entity.getApprovedPensionAmount())
                .approvedWithdrawalAmount(entity.getApprovedWithdrawalAmount())
                .approvedRefundAmount(entity.getApprovedRefundAmount())
                .approvedDeductionAmount(entity.getApprovedDeductionAmount())
                .finalNetPayableAmount(entity.getFinalNetPayableAmount())

                .requiresFinanceAction(entity.getRequiresFinanceAction())
                .requiresManualReview(entity.getRequiresManualReview())

                .approvalReason(entity.getApprovalReason())
                .returnedReason(entity.getReturnedReason())
                .rejectedReason(entity.getRejectedReason())

                .approverRemarks(entity.getApproverRemarks())
                .approvedBy(entity.getApprovedBy())
                .approvedByRole(entity.getApprovedByRole())

                .approvedAt(
                        entity.getApprovedAt() != null
                                ? entity.getApprovedAt().toLocalDateTime()
                                : null
                )

                .isActive(entity.getIsActive())

                .createdBy(entity.getCreatedBy())

                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
                                : null
                )

                .updatedBy(entity.getUpdatedBy())

                .updatedAt(
                        entity.getUpdatedAt() != null
                                ? entity.getUpdatedAt().toLocalDateTime()
                                : null
                )

                .build();
    }
}