package com.claim.claim_processing.application.mapper.claimApplicationOtherResponse;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;

@Component
public class NormalClaimResponseMapper {

    public NormalClaimResponseDto toResponse(NormalClaimDetail entity) {
        if (entity == null) {
            return null;
        }

        return NormalClaimResponseDto.builder()
                .id(entity.getId())

                .claimApplicationId(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getId()
                        : null)
                .applicationNumber(entity.getClaimApplication() != null
                        ? entity.getClaimApplication().getApplicationNumber()
                        : null)

                .cessationTypeId(entity.getCessationType() != null
                        ? entity.getCessationType().getId()
                        : null)
                .cessationTypeName(entity.getCessationType() != null
                        ? entity.getCessationType().getName()
                        : null)

                .payeeTypeId(entity.getPayeeType() != null
                        ? entity.getPayeeType().getId()
                        : null)
                .payeeTypeName(entity.getPayeeType() != null
                        ? entity.getPayeeType().getName()
                        : null)

                .terminationReasonTypeId(entity.getTerminationReasonType() != null
                        ? entity.getTerminationReasonType().getId()
                        : null)
                .terminationReasonTypeName(entity.getTerminationReasonType() != null
                        ? entity.getTerminationReasonType().getName()
                        : null)

                .dateOfTermination(entity.getDateOfTermination())
                .pfJoiningDate(entity.getPfJoiningDate())
                .pensionJoiningDate(entity.getPensionJoiningDate())
                .relievingOrderDate(entity.getRelievingOrderDate())
                .cessationEffectiveDate(entity.getCessationEffectiveDate())
                .exitDate(entity.getExitDate())
                .dateOfServiceJoining(entity.getDateOfServiceJoining())

                .terminatedBy(entity.getTerminatedBy())
                .terminationRemarks(entity.getTerminationRemarks())
                .relievingOrderNumber(entity.getRelievingOrderNumber())
                .relievingReferenceNumber(entity.getRelievingReferenceNumber())
                .lastPayMonth(entity.getLastPayMonth())
                .finalBasicSalary(entity.getFinalBasicSalary())
                .nonContributionMonths(entity.getNonContributionMonths())
                .remarks(entity.getRemarks())

                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }
}
