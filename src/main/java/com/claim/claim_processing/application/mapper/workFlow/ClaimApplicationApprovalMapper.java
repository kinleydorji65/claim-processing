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
                .remarks(entity.getRemarks())
                .approvedBy(entity.getApprovedBy())

                .approvedAt(
                        entity.getApprovedAt() != null
                                ? entity.getApprovedAt().toLocalDateTime()
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