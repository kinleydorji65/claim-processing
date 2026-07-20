package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationVerificationMapper {
    private final StatusMasterRepository statusMasterRepository;
    public ClaimApplicationVerificationResponseDto toResponse(
            ClaimApplicationVerification entity
    ) {

        if (entity == null) {
            return null;
        }

        return ClaimApplicationVerificationResponseDto.builder()
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
                .verificationStatusId(entity.getStatus().getStatusId())

                .verificationStatusName(
                        getStatusMaster(
                                entity.getStatus().getStatusId()
                        ).getStatusName()
                )

                .remarks(
                        entity.getRemarks()
                )

                .verifiedBy(
                        entity.getVerifiedBy()
                )
                .rejectedBy(entity.getRejectedBy())

                .verifiedAt(
                        entity.getVerifiedAt() != null
                                ? entity.getVerifiedAt().toLocalDateTime()
                                : null
                )

                .createdBy(
                        entity.getCreatedBy()
                )

                .createdAt(
                        entity.getCreatedAt() != null
                                ? entity.getCreatedAt().toLocalDateTime()
                                : null
                )

                .updatedBy(
                        entity.getUpdatedBy()
                )

                .updatedAt(
                        entity.getUpdatedAt() != null
                                ? entity.getUpdatedAt().toLocalDateTime()
                                : null
                )

                .build();
    }

    private StatusMaster getStatusMaster(Long statusId){
        return statusMasterRepository.findById(statusId).orElseThrow(() -> ClaimException.notFound("StatusMaster not found for id: " + statusId));
    }
}