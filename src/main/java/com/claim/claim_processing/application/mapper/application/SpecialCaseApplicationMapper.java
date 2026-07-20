package com.claim.claim_processing.application.mapper.application;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.entity.application.ClaimApplication;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;
import com.claim.claim_processing.common.entities.claim.ReserveAccount;
import com.claim.claim_processing.common.entities.pension.PensionDetail;
import com.claim.claim_processing.common.repository.claim.ReserveAccountRepository;
import com.claim.claim_processing.common.repository.pension.PensionDetailRepository;
import com.claim.claim_processing.exceptions.ClaimException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpecialCaseApplicationMapper {
    private final ReserveAccountRepository reserveAccountRepository;

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

        // Special Case Information
        responseDto.setCaseType(specialCaseApplication.getCaseType());
        responseDto.setCaseReasonId(specialCaseApplication.getCaseReasonId());
        responseDto.setCaseReasonName(null);

        // Pension Details (Snapshot)
        responseDto.setPensionType(specialCaseApplication.getPensionType());
        responseDto.setPensionStartDate(specialCaseApplication.getPensionStartDate());
        responseDto.setTotalContributionYears(specialCaseApplication.getTotalContributionYears());
        responseDto.setTotalPensionAmount(specialCaseApplication.getTotalPensionAmount());
        responseDto.setPensionAccountId(specialCaseApplication.getPensionAccount() != null ? 
                specialCaseApplication.getPensionAccount().getId() : null);

        // Pension Conversion
        responseDto.setCurrentBenefitType(specialCaseApplication.getCurrentBenefitType());
        responseDto.setRequestedBenefitType(specialCaseApplication.getRequestedBenefitType());

        // Forfeited Repayment (Snapshot)
        responseDto.setTotalForfeitedAmount(specialCaseApplication.getTotalForfeitedAmount());
        responseDto.setEligibleClaimAmount(specialCaseApplication.getEligibleClaimAmount());
        responseDto.setForfeitedDate(specialCaseApplication.getForfeitedDate());
        responseDto.setComponentCodes(specialCaseApplication.getComponentCodes());

        // Amount Details
        responseDto.setApprovedAmount(specialCaseApplication.getApprovedAmount());

        // Approval Information
        responseDto.setApprovedBy(specialCaseApplication.getApprovedBy());
        responseDto.setApprovedDate(specialCaseApplication.getApprovedDate());
        responseDto.setApprovalReference(specialCaseApplication.getApprovalReference());
        responseDto.setRejectionReason(specialCaseApplication.getRejectionReason());

        // Reserve Account Reference
        responseDto.setReserveAccountId(specialCaseApplication.getReserveAccount() != null ? 
                specialCaseApplication.getReserveAccount().getId() : null);

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

        // Only set preview/calculation fields
        responseDto.setCaseType(specialCaseApplication.getCaseType());
        responseDto.setPensionType(specialCaseApplication.getPensionType());
        responseDto.setPensionStartDate(specialCaseApplication.getPensionStartDate());
        responseDto.setTotalContributionYears(specialCaseApplication.getTotalContributionYears());
        responseDto.setTotalPensionAmount(specialCaseApplication.getTotalPensionAmount());
        responseDto.setCurrentBenefitType(specialCaseApplication.getCurrentBenefitType());
        responseDto.setRequestedBenefitType(specialCaseApplication.getRequestedBenefitType());
        responseDto.setTotalForfeitedAmount(specialCaseApplication.getTotalForfeitedAmount());
        responseDto.setEligibleClaimAmount(specialCaseApplication.getEligibleClaimAmount());
        responseDto.setForfeitedDate(specialCaseApplication.getForfeitedDate());
        responseDto.setComponentCodes(specialCaseApplication.getComponentCodes());
        responseDto.setReserveAccountId(specialCaseApplication.getReserveAccount() != null ? 
                specialCaseApplication.getReserveAccount().getId() : null);

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

        // Only update fields that are allowed to be updated
        if (dto.getCaseReasonId() != null) {
            entity.setCaseReasonId(dto.getCaseReasonId());
        }
        if (dto.getCurrentBenefitType() != null) {
            entity.setCurrentBenefitType(dto.getCurrentBenefitType());
        }
        if (dto.getRequestedBenefitType() != null) {
            entity.setRequestedBenefitType(dto.getRequestedBenefitType());
        }
        if (dto.getRequestedAmount() != null) {
            entity.setRequestedAmount(dto.getRequestedAmount());
        }
        if (dto.getReserveAccountId() != null) {
            ReserveAccount reserveAccount = reserveAccountRepository.findById(dto.getReserveAccountId())
                    .orElseThrow(() -> ClaimException.notFound("Reserve account not found with ID: " + dto.getReserveAccountId()));
            entity.setReserveAccount(reserveAccount);
        }
        if (dto.getUpdatedBy() != null) {
            entity.setUpdatedBy(dto.getUpdatedBy());
        }
    }

    /**
     * Set snapshot data from Pension Detail
     */
    public void setPensionSnapshot(ClaimSpecialCaseApplication entity, PensionDetail pensionDetail) {
        if (entity == null || pensionDetail == null) {
            return;
        }

        entity.setPensionType(pensionDetail.getPensionType());
        entity.setPensionStartDate(pensionDetail.getPensionStartDate());
        entity.setTotalContributionYears(pensionDetail.getTotalContributionYears());
        entity.setTotalPensionAmount(pensionDetail.getTotalPensionFund());
        entity.setPensionAccount(pensionDetail);
    }

    /**
     * Set snapshot data from Reserve Account
     */
    public void setForfeitedSnapshot(ClaimSpecialCaseApplication entity, ReserveAccount reserveAccount) {
        if (entity == null || reserveAccount == null) {
            return;
        }

        BigDecimal totalForfeited = reserveAccount.getForfeitedAmount() != null ? 
                reserveAccount.getForfeitedAmount() : BigDecimal.ZERO;
        
        entity.setTotalForfeitedAmount(totalForfeited);
        entity.setEligibleClaimAmount(calculateEligibleClaimAmount(totalForfeited));
        entity.setForfeitedDate(reserveAccount.getReleaseDate());
        entity.setComponentCodes(reserveAccount.getComponentCodes());
        entity.setReserveAccount(reserveAccount);
    }

    /**
     * Calculate eligible claim amount (80% of total forfeited)
     */
    private BigDecimal calculateEligibleClaimAmount(BigDecimal totalForfeited) {
        if (totalForfeited == null || totalForfeited.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return totalForfeited.multiply(BigDecimal.valueOf(0.8))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}