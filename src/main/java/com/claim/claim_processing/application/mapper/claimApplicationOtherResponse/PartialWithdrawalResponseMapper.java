package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Component
public class PartialWithdrawalResponseMapper {

    public PartialWithdrawalResponseDto toResponse(PartialWithdrawalDetail entity) {
        if (entity == null) {
            return null;
        }

        return PartialWithdrawalResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getId()
                        : null)
                .applicationNumber(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getApplicationNumber()
                        : null)

                .payeeTypeId(entity.getPayeeType() != null
                        ? entity.getPayeeType().getId()
                        : null)
                .payeeTypeName(entity.getPayeeType() != null
                        ? entity.getPayeeType().getName()
                        : null)

                .withdrawalReasonId(entity.getWithdrawalReason() != null
                        ? entity.getWithdrawalReason().getId()
                        : null)
                .withdrawalReasonName(entity.getWithdrawalReason() != null
                        ? entity.getWithdrawalReason().getName()
                        : null)

                .requestedWithdrawalAmount(entity.getRequestedWithdrawalAmount())
                .actualWithdrawalAmount(entity.getActualWithdrawalAmount())

                .unemploymentStartDate(entity.getUnemploymentStartDate())
                .disabilityDate(entity.getDisabilityDate())

                .unemploymentCauseId(entity.getUnemploymentCauseMaster() != null
                        ? entity.getUnemploymentCauseMaster().getId()
                        : null)
                .unemploymentCauseCode(entity.getUnemploymentCauseMaster() != null
                        ? entity.getUnemploymentCauseMaster().getCode()
                        : null)
                .unemploymentCauseName(entity.getUnemploymentCauseMaster() != null
                        ? entity.getUnemploymentCauseMaster().getName()
                        : null)

                .incidentDate(entity.getIncidentDate())
                .placeOfIncident(entity.getPlaceOfIncident())

                .businessTypeId(entity.getBusinessType() != null
                        ? entity.getBusinessType().getId()
                        : null)
                .businessTypeName(entity.getBusinessType() != null
                        ? entity.getBusinessType().getName()
                        : null)

                .businessName(entity.getBusinessName())
                .proposedInvestmentAmount(entity.getProposedInvestmentAmount())

                .housePurchaseType(entity.getHousePurchaseType())
                .propertyLocation(entity.getPropertyLocation())
                .estimatedCost(entity.getEstimatedCost())

                .description(entity.getDescription())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }
}
