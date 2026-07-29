package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplicationBankDetail;

@Component
public class ClaimApplicationBankResponseMapper {

    public ClaimApplicationBankResponseDto toResponse(
            ClaimApplicationBankDetail entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationBankResponseDto.builder()
                .id(entity.getId())

                // .claimApplicationId(
                //         entity.getClaimApplication() != null
                //                 ? entity.getClaimApplication().getId()
                //                 : null
                // )

                .beneficiaryIdentifier(entity.getBeneficiaryIdentifier() != null ? entity.getBeneficiaryIdentifier() : null)

                .claimantTypeId(
                        entity.getClaimantType() != null
                                ? entity.getClaimantType().getId()
                                : null
                )
                .relationTypeId(entity.getRelationType() != null ? entity.getRelationType().getRelationTypeId() : null)
                .relationTypeName(entity.getRelationType() != null ? entity.getRelationType().getRelationTypeName() : null)
                .claimantTypeName(
                        entity.getClaimantType() != null
                                ? entity.getClaimantType().getName()
                                : null
                )

                .bankTypeId(
                        entity.getBankType() != null
                                ? entity.getBankType().getBankTypeId()
                                : null
                )
                .bankTypeName(
                        entity.getBankType() != null
                                ? entity.getBankType().getBankTypeName()
                                : null
                )

                .accountNumber(entity.getAccountNumber())
                .accountHolderName(entity.getAccountHolderName())
                .ifscOrRoutingCode(entity.getIfscOrRoutingCode())

                .isDefaultBank(entity.getIsDefaultBank())

                .verifiedBy(entity.getVerifiedBy())
                .verifiedAt(
                        entity.getVerifiedAt() != null
                                ? entity.getVerifiedAt().toLocalDateTime()
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
