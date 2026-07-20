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
                .applicationNumber(entity.getClaimApplication().getApplicationNumber())
                .outstandingAmount(entity.getOutstandingAmount()) // ✅ No semicolon
                .verifiedDeductedAmount(entity.getVerifiedDeductedAmount()) // ✅ No semicolon
                .approvedDeductedAmount(entity.getApprovedDeductedAmount()) // ✅ No semicolon
                .deductedAmount(entity.getDeductedAmount()) // ✅ No semicolon
                .createdBy(entity.getCreatedBy())

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
                .deductionDetailId(item.getDeductionDetail().getId())
                .deductionCategory(item.getDeductionCategory()) // LOAN / RENTAL / TAX / OTHE()R
                .outstandingAmount(item.getOutstandingAmount())
                .deductedAmount(item.getDeductedAmount())
                .remainingAmount(item.getRemainingAmount())
                .remarks(item.getRemarks())
                .createdBy(item.getCreatedBy())
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
