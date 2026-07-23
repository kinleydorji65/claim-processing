package com.claim.claim_processing.application.mapper.claimDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.request.GeneralSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimSpecialCaseResponse.SpecialCaseComponentBalanceResponseDTO;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCase;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCaseComponentDetail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class GeneralSpecialCaseMapper {

        public GeneralSpecialCaseResponse mapToGeneralSpecialCaseResponse(ClaimDetail claimDetail,
            ClaimSpecialCase specialCase, ClaimBankDetail bankDetail) {
        
        GeneralSpecialCaseResponse generalSpecialCaseResponse = GeneralSpecialCaseResponse.builder()
                .id(claimDetail.getId())

                .claimTypeId(claimDetail.getClaimType() != null ? claimDetail.getClaimType().getId() : null)
                .claimTypeName(claimDetail.getClaimType() != null ? claimDetail.getClaimType().getName() : null)

                .submissionChannelId(
                        claimDetail.getSubmissionChannel() != null ? claimDetail.getSubmissionChannel().getId() : null)
                .submissionChannelName(
                        claimDetail.getSubmissionChannel() != null ? claimDetail.getSubmissionChannel().getName()
                                : null)

                .schemeTypeId(claimDetail.getSchemeType() != null ? claimDetail.getSchemeType().getId() : null)
                .schemeTypeName(claimDetail.getSchemeType() != null ? claimDetail.getSchemeType().getName() : null)

                .memberCategoryId(
                        claimDetail.getMemberCategory() != null ? claimDetail.getMemberCategory().getCategoryId()
                                : null)
                .memberCategoryName(
                        claimDetail.getMemberCategory() != null ? claimDetail.getMemberCategory().getCategoryName()
                                : null)

                .employmentType(claimDetail.getEmploymentType())
                .memberCode(claimDetail.getMemberCode())
                .nppfNumber(claimDetail.getNppfNumber())
                .agencyCode(claimDetail.getAgencyCode())
                .officeId(claimDetail.getOfficeId() != null ? claimDetail.getOfficeId() : null)

                .applicationDate(claimDetail.getApplicationDate())
                .email(claimDetail.getEmail())
                .contactNo(claimDetail.getContactNo())
                .pfStartDate(claimDetail.getPfStartDate())
                .pfEndDate(claimDetail.getPfEndDate())
                .pensionStartDate(claimDetail.getPensionStartDate())
                .pensionEndDate(claimDetail.getPensionEndDate())

                .isLoanApplied(claimDetail.getIsLoanApplied())
                .isRentalApplied(claimDetail.getIsRentalApplied())

                .onBehalfOfMember(claimDetail.getOnBehalfOfMember())
                .initiatedBy(claimDetail.getInitiatedBy())
                .remarks(claimDetail.getRemarks())

                .isSpecialCase(claimDetail.getIsSpecialCase())
                .isActive(claimDetail.getIsActive())
                .currencyCode(claimDetail.getCurrencyCode())

                .currentStageId(claimDetail.getCurrentStage() != null ? claimDetail.getCurrentStage().getId() : null)
                .currentStageName(
                        claimDetail.getCurrentStage() != null ? claimDetail.getCurrentStage().getName() : null)

                .statusId(claimDetail.getStatus() != null ? claimDetail.getStatus().getStatusId() : null)
                .statusName(claimDetail.getStatus() != null ? claimDetail.getStatus().getStatusName() : null)
                .createdBy(claimDetail.getCreatedBy())
                .createdAt(claimDetail.getCreatedAt() != null ? claimDetail.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(claimDetail.getUpdatedBy())
                .updatedAt(claimDetail.getUpdatedAt() != null ? claimDetail.getUpdatedAt().toLocalDateTime() : null)
                .bankDetail(mapToBankDetailResponse(bankDetail, claimDetail))
                .specialCaseDetail(mapToSpecialCaseResponse(specialCase, claimDetail))  // ✅ Components are inside this
                .build();
        
        return generalSpecialCaseResponse;
    }

    private ClaimApplicationBankResponseDto mapToBankDetailResponse(ClaimBankDetail bankDetail,
            ClaimDetail claimDetail) {
        if (bankDetail == null) {
            return null;
        }
        return ClaimApplicationBankResponseDto.builder()
                .id(bankDetail.getId())
                .beneficiaryIdentifier(claimDetail.getIdentityNumber())
                .accountNumber(bankDetail.getAccountNumber())
                .bankTypeId(bankDetail.getBankType().getBankTypeId())
                .bankTypeName(bankDetail.getBankType().getBankTypeName())
                .ifscOrRoutingCode(bankDetail.getIfscOrRoutingCode())
                .createdBy(bankDetail.getCreatedBy())
                .createdAt(bankDetail.getCreatedAt() != null ? bankDetail.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(bankDetail.getUpdatedBy())
                .updatedAt(bankDetail.getUpdatedAt() != null ? bankDetail.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }

    private ClaimSpecialCaseResponse mapToSpecialCaseResponse(ClaimSpecialCase specialCase, ClaimDetail claimDetail) {
        if (specialCase == null) {
            return null;
        }
        
        return ClaimSpecialCaseResponse.builder()
                .id(specialCase.getId())
                .claimDetailId(claimDetail.getId())
                .caseReasonId(specialCase.getCaseReasonId())


                // Approval Information
                .approvedBy(specialCase.getApprovedBy())
                .approvedDate(specialCase.getApprovedDate())
                // Audit Information
                .isActive(specialCase.getIsActive())
                .createdBy(specialCase.getCreatedBy())
                .createdAt(specialCase.getCreatedAt() != null ? specialCase.getCreatedAt() : null)
                .updatedBy(specialCase.getUpdatedBy())
                .updatedAt(specialCase.getUpdatedAt() != null ? specialCase.getUpdatedAt() : null)

                // ✅ Components are inside specialCaseDetail
                .components(mapToComponentBalanceResponseDTOs(specialCase))
                .build();
    }

    /**
     * Map ClaimSpecialCase component details to SpecialCaseComponentBalanceResponseDTO list
     */
    private List<SpecialCaseComponentBalanceResponseDTO> mapToComponentBalanceResponseDTOs(ClaimSpecialCase specialCase) {
        if (specialCase == null) {
            return new ArrayList<>();
        }

        List<ClaimSpecialCaseComponentDetail> componentDetails = specialCase.getComponentDetails();
        if (componentDetails == null || componentDetails.isEmpty()) {
            log.debug("No component details found for special case: {}", specialCase.getId());
            return new ArrayList<>();
        }

        return componentDetails.stream()
                .filter(detail -> detail != null && "Y".equalsIgnoreCase(detail.getIsActive()))
                .map(this::mapToComponentBalanceResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map ClaimSpecialCaseComponentDetail to SpecialCaseComponentBalanceResponseDTO
     */
    private SpecialCaseComponentBalanceResponseDTO mapToComponentBalanceResponseDTO(ClaimSpecialCaseComponentDetail detail) {
        if (detail == null) {
            return null;
        }
        return SpecialCaseComponentBalanceResponseDTO.builder()
                .id(detail.getId())
                .amount(detail.getAmount())
                .code(detail.getComponentMaster().getCode())
                .name(detail.getComponentMaster().getName())
                .build();
    }
}