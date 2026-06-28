package com.claim.claim_processing.application.mapper.application;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.SpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpecialCaseApplicationMapper {

    public SpecialCaseApplicationResponseDTO toResponse(ClaimApplication entity,
            ClaimSpecialCaseApplication specialCaseApplication) {
        if (entity == null) {
            return null;
        }

        return SpecialCaseApplicationResponseDTO
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
                .pfJoiningDate(entity.getPfJoiningDate())
                .pensionJoiningDate(entity.getPensionJoiningDate())
                .onBehalfOfMember(entity.getOnBehalfOfMember())
                .initiatedBy(entity.getInitiatedBy())
                .remarks(entity.getRemarks())
                .isSpecialCase(entity.getIsSpecialCase())
                .isActive(entity.getIsActive())
                .specialCaseAuthorityId(
                        entity.getSpecialCaseAuthority() != null ? entity.getSpecialCaseAuthority().getId() : null)
                .specialCaseAuthorityName(
                        entity.getSpecialCaseAuthority() != null ? entity.getSpecialCaseAuthority().getName() : null)
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

    private ClaimSpecialCaseApplicationResponseDto buildSpecialCaseResponse(
            ClaimSpecialCaseApplication specialCaseApplication) {
        ClaimSpecialCaseApplicationResponseDto responseDto = new ClaimSpecialCaseApplicationResponseDto();

        if (specialCaseApplication == null) {
            return null;
        }

        // Basic Information
        responseDto.setId(specialCaseApplication.getId());

        // Member Information
        responseDto.setMemberCode(specialCaseApplication.getMemberCode());
        responseDto.setNppfNumber(specialCaseApplication.getNppfNumber());
        responseDto.setIdentityNumber(specialCaseApplication.getIdentityNumber());

        // Agency Information
        responseDto.setAgencyCategoryId(specialCaseApplication.getAgencyCategoryId());
        responseDto.setAgencyCode(specialCaseApplication.getAgencyCode());

        // Special Case Information
        responseDto.setCaseType(specialCaseApplication.getCaseType());
        responseDto.setCaseReason(specialCaseApplication.getCaseReason());

        // Amount Details
        responseDto.setRequestedAmount(specialCaseApplication.getRequestedAmount());
        responseDto.setApprovedAmount(specialCaseApplication.getApprovedAmount());

        // Pension Conversion
        responseDto.setCurrentBenefitType(specialCaseApplication.getCurrentBenefitType());
        responseDto.setRequestedBenefitType(specialCaseApplication.getRequestedBenefitType());

        // Forfeited Repayment
        responseDto.setForfeitedComponentCodes(specialCaseApplication.getForfeitedComponentCodes());

        // Approval Information
        responseDto.setRequestDate(specialCaseApplication.getRequestDate());
        responseDto.setRequestedBy(specialCaseApplication.getRequestedBy());
        responseDto.setApprovedBy(specialCaseApplication.getApprovedBy());
        responseDto.setApprovedDate(specialCaseApplication.getApprovedDate());
        responseDto.setApprovalReference(specialCaseApplication.getApprovalReference());
        responseDto.setRejectionReason(specialCaseApplication.getRejectionReason());

        // Processing Information
        responseDto.setProcessedBy(specialCaseApplication.getProcessedBy());
        responseDto.setProcessedDate(specialCaseApplication.getProcessedDate());

        // Reserve Account
        responseDto.setReserveAccountId(specialCaseApplication.getReserveAccountId());

        // Audit Information
        responseDto.setIsActive(specialCaseApplication.getIsActive());
        responseDto.setCreatedBy(specialCaseApplication.getCreatedBy());
        responseDto.setCreatedAt(specialCaseApplication.getCreatedAt());
        responseDto.setUpdatedBy(specialCaseApplication.getUpdatedBy());
        responseDto.setUpdatedAt(specialCaseApplication.getUpdatedAt());

        return responseDto;
    }
}
