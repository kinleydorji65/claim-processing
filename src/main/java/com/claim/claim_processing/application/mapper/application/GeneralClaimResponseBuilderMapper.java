package com.claim.claim_processing.application.mapper.application;

import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimApplication;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeneralClaimResponseBuilderMapper {

    public GeneralClaimResponse toResponse(ClaimApplication entity) {

        if (entity == null) {
            return null;
        }

        return GeneralClaimResponse.builder()
                .id(entity.getId())
                .applicationNumber(entity.getApplicationNumber())

                .claimTypeId(entity.getClaimType() != null ? entity.getClaimType().getId() : null)
                .claimTypeName(entity.getClaimType() != null ? entity.getClaimType().getName() : null)

                .submissionChannelId(entity.getSubmissionChannel() != null ? entity.getSubmissionChannel().getId() : null)
                .submissionChannelName(entity.getSubmissionChannel() != null ? entity.getSubmissionChannel().getName() : null)

                .schemeTypeId(entity.getSchemeType() != null ? entity.getSchemeType().getId() : null)
                .schemeTypeName(entity.getSchemeType() != null ? entity.getSchemeType().getName() : null)

                .memberCategoryId(entity.getMemberCategory() != null ? entity.getMemberCategory().getCategoryId() : null)
                .memberCategoryName(entity.getMemberCategory() != null ? entity.getMemberCategory().getCategoryName() : null)

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
                .specialCaseAuthorityId(entity.getSpecialCaseAuthority() != null ? entity.getSpecialCaseAuthority().getId() : null)
                .specialCaseAuthorityName(entity.getSpecialCaseAuthority() != null ? entity.getSpecialCaseAuthority().getName() : null)
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

                .build();
    }

    public GeneralClaimResponse attachDetails(
            GeneralClaimResponse response,
            List<ClaimApplicationBankResponseDto> bankDetails,
            ClaimApplicationDeductionResponseDto deductionDetail,
            ClaimApplicationCalculationSummaryResponseDto calculationSummary,
            NormalClaimResponseDto normalClaimDetails,
            PartialWithdrawalResponseDto partialWithdrawalDetails,
            BeneficiarySettlementResponseDto beneficiarySettlementDetails,
            List<ClaimApplicationForfeitedComponentResponseDto> forfeitedComponents
    ) {
        if (response == null) {
            return null;
        }

        response.setBankDetails(bankDetails);
        response.setDeductionDetail(deductionDetail);
        response.setCalculationSummary(calculationSummary);
        response.setNormalClaimDetails(normalClaimDetails);
        response.setPartialWithdrawalDetails(partialWithdrawalDetails);
        response.setBeneficiarySettlementDetails(beneficiarySettlementDetails);
        response.setForfeitedComponents(forfeitedComponents);

        return response;
    }

    
}
