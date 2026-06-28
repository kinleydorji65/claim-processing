package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationApprovalResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationApproval;
import com.claim.claim_processing.common.mapper.others.StatusMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationApprovalMapper {

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

                .approvalStatus(
                        entity.getApprovalStatus() != null
                                ? statusMapper.toDto(entity.getApprovalStatus())
                                : null
                )

                .approvedAmount(entity.getApprovedAmount())
                .approvedPfAmount(entity.getApprovedPfAmount())
                .approvedPensionAmount(entity.getApprovedPensionAmount())
                .approvedWithdrawalAmount(entity.getApprovedWithdrawalAmount())
                .approvedRefundAmount(entity.getApprovedRefundAmount())
                .approvedDeductionAmount(entity.getApprovedDeductionAmount())
                .finalNetPayableAmount(entity.getFinalNetPayableAmount())

                .requiresManualReview(entity.getRequiresManualReview())

                .remarks(entity.getRemarks())
                .approvedBy(entity.getApprovedBy())
                .roleId(entity.getRoleId())

                .approvedAt(
                        entity.getApprovedAt() != null
                                ? entity.getApprovedAt().toLocalDateTime()
                                : null
                )

                .isActive(
                        entity.getIsActive() != null
                                ? entity.getIsActive().name()
                                : null
                )

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