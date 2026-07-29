package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;

import org.springframework.stereotype.Component;

@Component
public class LegalRecoveryResponseMapper {

    public LegalRecoveryResponseDto toResponse(LegalRecoveryDetail entity) {
        if (entity == null) {
            return null;
        }

        return LegalRecoveryResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getId()
                        : null)
                .claimApplicationNumber(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getApplicationNumber()
                        : null)

                .claimDetailId(entity.getClaimDetail() != null
                        ? entity.getClaimDetail().getId()
                        : null)

                .judgementNumber(entity.getJudgementNumber())

                .payeeTypeId(entity.getPayeeType() != null
                        ? entity.getPayeeType().getId()
                        : null)
                .payeeTypeName(entity.getPayeeType() != null
                        ? entity.getPayeeType().getName()
                        : null)

                .judgementDate(entity.getJudgementDate())

                .dzongkhagId(entity.getDzongkhag().getDzongkhagId())
                .dzongkhagName(entity.getDzongkhag().getDzongkhagName())
                .convictedOrder(entity.getConvictedOrder())
                .isConvicted(entity.getIsConvicted())
                .payToMember(entity.getPayToMember())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }
}
