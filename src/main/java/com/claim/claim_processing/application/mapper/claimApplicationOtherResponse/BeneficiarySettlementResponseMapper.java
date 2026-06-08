package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;

@Component
public class BeneficiarySettlementResponseMapper {

    public BeneficiarySettlementResponseDto toResponse(
            BeneficiarySettlementDetail entity
    ) {

        if (entity == null) {
            return null;
        }

        return BeneficiarySettlementResponseDto.builder()
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

                .cessationTypeId(
                        entity.getCessationType() != null
                                ? entity.getCessationType().getId()
                                : null
                )

                .cessationTypeName(
                        entity.getCessationType() != null
                                ? entity.getCessationType().getName()
                                : null
                )

                .dateOfDeath(entity.getDateOfDeath())
                .lastContributionDate(entity.getLastContributionDate())

                .beneficiaryClaimantDetails(
                        entity.getClaimantDetails() == null
                                ? List.of()
                                : entity.getClaimantDetails().stream()
                                        .map(this::mapClaimant)
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

    private BeneficiaryClaimantResponseDto mapClaimant(
            BeneficiaryClaimantDetail entity
    ) {

        if (entity == null) {
            return null;
        }

        return BeneficiaryClaimantResponseDto.builder()

                .id(entity.getId())

                .beneficiarySettlementDetailId(
                        entity.getBeneficiarySettlementDetail() != null
                                ? entity.getBeneficiarySettlementDetail().getId()
                                : null
                )

                .nomineeId(
                        entity.getNominee() != null
                                ? entity.getNominee().getId()
                                : null
                )

                .dependentId(
                        entity.getDependent() != null
                                ? entity.getDependent().getId()
                                : null
                )

                .claimantTypeId(
                        entity.getClaimantType() != null
                                ? entity.getClaimantType().getId()
                                : null
                )

                .claimantTypeName(
                        entity.getClaimantType() != null
                                ? entity.getClaimantType().getName()
                                : null
                )

                .payeeTypeId(
                        entity.getPayeeType() != null
                                ? entity.getPayeeType().getId()
                                : null
                )

                .payeeTypeName(
                        entity.getPayeeType() != null
                                ? entity.getPayeeType().getName()
                                : null
                )

                .relationshipTypeId(
                        entity.getRelationshipType() != null
                                ? entity.getRelationshipType().getRelationTypeId()
                                : null
                )

                .relationshipTypeName(
                        entity.getRelationshipType() != null
                                ? entity.getRelationshipType().getRelationTypeName()
                                : null
                )

                .beneficiaryIdentifier(entity.getBeneficiaryIdentifier())
                .beneficiaryName(entity.getBeneficiaryName())
                .dateOfBirth(entity.getDateOfBirth())
                .beneficiarySharePercentage(entity.getBeneficiarySharePercentage())

                .isMemberFamily(entity.getIsMemberFamily())
                .isMinor(entity.getIsMinor())

                .guardianName(entity.getGuardianName())
                .guardianIdentifier(entity.getGuardianIdentifier())

                .benefitAmount(entity.getBenefitAmount())

                .remarks(entity.getRemarks())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())

                .build();
    }
}