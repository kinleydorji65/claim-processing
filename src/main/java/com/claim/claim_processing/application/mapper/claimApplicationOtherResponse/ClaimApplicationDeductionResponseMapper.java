package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionDetail;
import com.claim.claim_processing.application.entity.application.ClaimApplicationDeductionItem;

@Component
public class ClaimApplicationDeductionResponseMapper {

    public ClaimApplicationDeductionResponseDto toResponse(
            ClaimApplicationDeductionDetail entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationDeductionResponseDto.builder()
                .id(entity.getId())
                .outstandingAmount(entity.getOutstandingAmount())
                .systemDeductedAmount(entity.getSystemDeductedAmount())
                .verifiedDeductedAmount(entity.getVerifiedDeductedAmount())
                .approvedDeductedAmount(entity.getApprovedDeductedAmount())
                .deductedAmount(entity.getDeductedAmount())

                .deductionReviewStatusId(
                        entity.getDeductionReviewStatus() != null
                                ? entity.getDeductionReviewStatus().getId()
                                : null
                )

                .deductionReviewStatusName(
                        entity.getDeductionReviewStatus() != null
                                ? entity.getDeductionReviewStatus().getName()
                                : null
                )

                .isAutoApplied(entity.getIsAutoApplied())
                .isManualOverride(entity.getIsManualOverride())
                .isActive(entity.getIsActive())

                .overrideReason(entity.getOverrideReason())
                .remarks(entity.getRemarks())

                .deductionItems(
                        entity.getDeductionItems() == null
                                ? List.of()
                                : entity.getDeductionItems()
                                .stream()
                                .map(this::mapItem)
                                .toList()
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

    private ClaimApplicationDeductionItemResponseDto mapItem(
            ClaimApplicationDeductionItem item
    ) {

        return ClaimApplicationDeductionItemResponseDto.builder()
                .id(item.getId())
                .deductionCategory(item.getDeductionCategory())
                .referenceNumber(item.getReferenceNumber())
                .referenceName(item.getReferenceName())
                .outstandingAmount(item.getOutstandingAmount())
                .deductedAmount(item.getDeductedAmount())
                .remainingAmount(item.getRemainingAmount())
                .priorityOrder(item.getPriorityOrder())
                .remarks(item.getRemarks())
                .isActive(item.getIsActive())
                .createdBy(item.getCreatedBy())
                .createdAt(
                        item.getCreatedAt() != null
                                ? item.getCreatedAt().toLocalDateTime()
                                : null
                )
                .updatedBy(item.getUpdatedBy())
                .updatedAt(
                        item.getUpdatedAt() != null
                                ? item.getUpdatedAt().toLocalDateTime()
                                : null
                )
                .build();
    }
}
