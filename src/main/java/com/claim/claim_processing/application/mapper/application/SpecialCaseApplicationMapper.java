package com.claim.claim_processing.application.mapper.application;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundReasonMaster;
import com.claim.claim_processing.common.repository.specialCase.SpecialCaseRefundReasonMasterRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpecialCaseApplicationMapper {
    private final SpecialCaseRefundReasonMasterRepository specialCaseRefundReasonMasterRepository;

    public GeneralSpecialCaseApplicationResponseDTO toResponse(ClaimApplication entity,
            ClaimSpecialCaseApplication specialCaseApplication) {
        if (entity == null) {
            return null;
        }

        return GeneralSpecialCaseApplicationResponseDTO
                .builder()
                .id(entity.getId())
                .applicationNumber(entity.getApplicationNumber())
                .claimTypeId(entity.getClaimType() != null ? entity.getClaimType().getId() : null)
                .claimTypeName(entity.getClaimType() != null ? entity.getClaimType().getName() : null)
                .submissionChannelId(
                        entity.getSubmissionChannel() != null ? entity.getSubmissionChannel().getId() : null)
                .submissionChannelName(
                        entity.getSubmissionChannel() != null ? entity.getSubmissionChannel().getName() : null)
                .schemeTypeId(entity.getSchemeType() != null ? entity.getSchemeType().getId() : null)
                .schemeTypeName(entity.getSchemeType() != null ? entity.getSchemeType().getName() : null)
                .memberCategoryId(
                        entity.getMemberCategory() != null ? entity.getMemberCategory().getCategoryId() : null)
                .memberCategoryName(
                        entity.getMemberCategory() != null ? entity.getMemberCategory().getCategoryName() : null)
                .employmentType(entity.getEmploymentType())
                .memberCode(entity.getMemberCode())
                .nppfNumber(entity.getNppfNumber())
                .agencyCode(entity.getAgencyCode())
                .officeId(entity.getOfficeId() != null ? entity.getOfficeId() : null)
                .applicationDate(entity.getApplicationDate())
                
                .email(entity.getEmail())
                .contactNo(entity.getContactNo())
                .pfStartDate(entity.getPfStartDate())
                .pfEndDate(entity.getPfEndDate())
                .pensionStartDate(entity.getPensionStartDate())
                .pensionEndDate(entity.getPensionEndDate())
                
                .isLoanApplied(entity.getIsLoanApplied())
                .isRentalApplied(entity.getIsRentalApplied())
                .onBehalfOfMember(entity.getOnBehalfOfMember())
                .initiatedBy(entity.getInitiatedBy())
                .remarks(entity.getRemarks())
                .isSpecialCase(entity.getIsSpecialCase())
                .isActive(entity.getIsActive())
                .claimedBy(entity.getClaimedBy())
                .unClaimedBy(entity.getUnClaimedBy())
                .currencyCode(entity.getCurrencyCode())
                .currentStageId(entity.getCurrentStage() != null ? entity.getCurrentStage().getId() : null)
                .currentStageName(entity.getCurrentStage() != null ? entity.getCurrentStage().getName() : null)
                .statusId(entity.getStatus() != null ? entity.getStatus().getStatusId() : null)
                .statusName(entity.getStatus() != null ? entity.getStatus().getStatusName() : null)
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toLocalDateTime() : null)
                .claimSpecialCaseApplicationResponseDto(buildSpecialCaseResponse(specialCaseApplication))
                .build();
    }

    /**
     * Build Special Case Response from Special Case Application Entity
     * Maps all fields from the updated entity structure
     */
    private ClaimSpecialCaseApplicationResponseDto buildSpecialCaseResponse(
            ClaimSpecialCaseApplication specialCaseApplication) {
        
        if (specialCaseApplication == null) {
            return null;
        }

        ClaimSpecialCaseApplicationResponseDto responseDto = new ClaimSpecialCaseApplicationResponseDto();

        // Primary Key
        responseDto.setId(specialCaseApplication.getId());

        // Claim Application Reference
        responseDto.setClaimApplicationId(specialCaseApplication.getClaimApplication() != null ? 
                specialCaseApplication.getClaimApplication().getId() : null);

        responseDto.setCaseReasonName(null);


        // Approval Information
        responseDto.setApprovedBy(specialCaseApplication.getApprovedBy());
        responseDto.setApprovedDate(specialCaseApplication.getApprovedDate());

        // Audit Information
        responseDto.setIsActive(specialCaseApplication.getIsActive());
        responseDto.setCreatedBy(specialCaseApplication.getCreatedBy());
        responseDto.setCreatedAt(specialCaseApplication.getCreatedAt());
        responseDto.setUpdatedBy(specialCaseApplication.getUpdatedBy());
        responseDto.setUpdatedAt(specialCaseApplication.getUpdatedAt());

        return responseDto;
    }

    /**
     * Build Special Case Response with only the preview/calculation fields
     * Used for preview before application creation
     */
    public ClaimSpecialCaseApplicationResponseDto buildPreviewResponse(
            ClaimSpecialCaseApplication specialCaseApplication) {
        
        if (specialCaseApplication == null) {
            return null;
        }

        ClaimSpecialCaseApplicationResponseDto responseDto = new ClaimSpecialCaseApplicationResponseDto();


        return responseDto;
    }

    /**
     * Update Special Case Entity from Request DTO
     * Only updatable fields are set
     */
    public void updateEntityFromDto(ClaimSpecialCaseApplication entity, 
            ClaimSpecialCaseApplicationRequestDto dto) {
        if (entity == null || dto == null) {
            return;
        }

        SpecialCaseRefundReasonMaster caseReason = specialCaseRefundReasonMasterRepository.findById(dto.getCaseReasonId()).orElse(null);

        // Only update fields that are allowed to be updated
        if (dto.getCaseReasonId() != null) {
            entity.setSpecialCaseRefundReasonMaster(caseReason);
        }
        if (dto.getUpdatedBy() != null) {
            entity.setUpdatedBy(dto.getUpdatedBy());
        }
    }
}