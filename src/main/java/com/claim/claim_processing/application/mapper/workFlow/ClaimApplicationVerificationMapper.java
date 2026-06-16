package com.claim.claim_processing.application.mapper.workFlow;

import com.claim.claim_processing.application.DTO.response.workFlow.ClaimApplicationVerificationResponseDto;
import com.claim.claim_processing.application.entity.workFlow.ClaimApplicationVerification;
import com.claim.claim_processing.common.controller.common.RoleMaster;
import com.claim.claim_processing.common.entities.others.StatusMaster;
import com.claim.claim_processing.common.repository.others.RoleMasterRepository;
import com.claim.claim_processing.common.repository.others.StatusMasterRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaimApplicationVerificationMapper {
    private final StatusMasterRepository statusMasterRepository;
    private final RoleMasterRepository roleMasterRepository;

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

                .requiresRecalculation(
                        entity.getRequiresRecalculation()
                )

                .requiresManualReview(
                        entity.getRequiresManualReview()
                )

                .rejectionReason(
                        entity.getRejectionReason()
                )

                .verifierRemarks(
                        entity.getVerifierRemarks()
                )

                .verifiedBy(
                        entity.getVerifiedBy()
                )

                .verifiedByRoleId(
                        entity.getVerifiedByRoleId()
                )
                .verifiedByRoleName(
                        getRoleName(entity.getVerifiedByRoleId())
                )

                .verifiedAt(
                        entity.getVerifiedAt() != null
                                ? entity.getVerifiedAt().toLocalDateTime()
                                : null
                )

                .isActive(
                        entity.getIsActive() != null
                                ? entity.getIsActive().name()
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

    private String getRoleName(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return roleMasterRepository.findById(roleId)
                .map(RoleMaster::getRoleName)
                .orElseThrow(() -> ClaimException.notFound("RoleMaster not found for id: " + roleId));
    }
}