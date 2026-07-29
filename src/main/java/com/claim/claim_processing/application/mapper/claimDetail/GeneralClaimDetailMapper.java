package com.claim.claim_processing.application.mapper.claimDetail;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimBankResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.ClaimRuleEvaluationListDto;
import com.claim.claim_processing.application.DTO.response.claimDetail.GeneralClaimDetailResponse;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiaryClaimantResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.BeneficiarySettlementResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.LegalRecoveryResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.NormalClaimResponseDto;
import com.claim.claim_processing.application.DTO.response.detail.PartialWithdrawalResponseDto;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationSummary;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionItem;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimForfeitedComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimRuleEvaluation;
import com.claim.claim_processing.application.entity.detail.BeneficiaryClaimantDetail;
import com.claim.claim_processing.application.entity.detail.BeneficiarySettlementDetail;
import com.claim.claim_processing.application.entity.detail.LegalRecoveryDetail;
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;
import com.claim.claim_processing.application.repository.claimDetail.ClaimBankDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimCalculationComponentRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimCalculationSummaryRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDeductionDetailRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimDeductionItemRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimForfeitedComponentRepository;
import com.claim.claim_processing.application.repository.claimDetail.ClaimRuleEvaluationRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiaryClaimantDetailRepository;
import com.claim.claim_processing.application.repository.detail.BeneficiarySettlementDetailRepository;
import com.claim.claim_processing.application.repository.detail.LegalRecoveryDetailRepository;
import com.claim.claim_processing.application.repository.detail.NormalClaimDetailRepository;
import com.claim.claim_processing.application.repository.detail.PartialWithdrawalDetailRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GeneralClaimDetailMapper {
    private final ClaimBankDetailRepository claimBankDetailRepository;
    private final ClaimDeductionDetailRepository claimDeductionDetailRepository;
    private final ClaimDeductionItemRepository claimDeductionItemRepository;
    private final ClaimForfeitedComponentRepository claimForfeitedComponentRepository;
    private final ClaimCalculationComponentRepository claimCalculationComponentRepository;
    private final ClaimRuleEvaluationRepository claimRuleEvaluationRepository;
    private final ClaimCalculationSummaryRepository claimCalculationSummaryRepository;
    private final BeneficiarySettlementDetailRepository beneficiarySettlementDetailRepository;
    private final BeneficiaryClaimantDetailRepository beneficiaryClaimantDetailRepository;
    private final LegalRecoveryDetailRepository legalRecoveryDetailRepository;
    private final NormalClaimDetailRepository normalClaimDetailRepository;
    private final PartialWithdrawalDetailRepository partialWithdrawalDetailRepository;
    
    public GeneralClaimDetailResponse mapToResponse(ClaimDetail claimDetail) {

        List<ClaimBankDetail> bankDetails = claimBankDetailRepository.findByClaimDetail_Id(claimDetail.getId());
        ClaimDeductionDetail deductionDetail = claimDeductionDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        List<ClaimForfeitedComponent> forfeitedComponents = claimForfeitedComponentRepository.findByClaimDetail_Id(claimDetail.getId());
        ClaimCalculationSummary calculationSummary = claimCalculationSummaryRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        NormalClaimDetail normalClaimDetail = normalClaimDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        PartialWithdrawalDetail partialWithdrawalDetail = partialWithdrawalDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        BeneficiarySettlementDetail beneficiarySettlementDetail = beneficiarySettlementDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);
        LegalRecoveryDetail legalRecoveryDetail = legalRecoveryDetailRepository.findByClaimDetail_Id(claimDetail.getId()).orElse(null);

    return GeneralClaimDetailResponse.builder()
            .id(claimDetail.getId())
            .claimTypeId(claimDetail.getClaimType().getId())
            .claimTypeName(claimDetail.getClaimType().getName())
            .applicationNumber(claimDetail.getApplicationNumber())
            .submissionChannelId(claimDetail.getSubmissionChannel().getId())
            .submissionChannelName(claimDetail.getSubmissionChannel().getName())
            .schemeTypeId(claimDetail.getSchemeType().getId())
            .schemeTypeName(claimDetail.getSchemeType().getName())
            .memberCategoryId(claimDetail.getMemberCategory().getCategoryId())
            .memberCategoryName(claimDetail.getMemberCategory().getCategoryName())
            .employmentType(claimDetail.getEmploymentType())
            .memberCode(claimDetail.getMemberCode())
            .nppfNumber(claimDetail.getNppfNumber())
            .agencyCode(claimDetail.getAgencyCode())
            .identityNumber(claimDetail.getIdentityNumber())
            .officeId(claimDetail.getOfficeId())
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
            .currentStageName(claimDetail.getCurrentStage() != null ? claimDetail.getCurrentStage().getName() : null)
            .statusId(claimDetail.getStatus() != null ? claimDetail.getStatus().getStatusId() : null)
            .statusName(claimDetail.getStatus() != null ? claimDetail.getStatus().getStatusName() : null)
            .bankDetails(mapBankDetails(bankDetails))
            .deductionDetail(mapDeductionDetail(deductionDetail))
            .calculationSummary(mapCalculationSummary(calculationSummary))
            .forfeitedComponents(mapForfeitedComponents(forfeitedComponents))
            .normalClaimDetails(mapNormalClaimDetails(normalClaimDetail))
            .beneficiarySettlementDetail(mapBeneficiarySettlementDetails(beneficiarySettlementDetail))
            .partialWithdrawalDetails(mapPartialWithdrawalDetails(partialWithdrawalDetail))
            .legalRecoveryDetail(mapLegalRecoveryDetails(legalRecoveryDetail))
            .createdBy(claimDetail.getCreatedBy())
            .createdAt(claimDetail.getCreatedAt() != null ? claimDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(claimDetail.getUpdatedBy())
            .updatedAt(claimDetail.getUpdatedAt() != null ? claimDetail.getUpdatedAt().toLocalDateTime() : null)
            .build(); 
}

private LegalRecoveryResponseDto mapLegalRecoveryDetails(LegalRecoveryDetail entity) {
    if (entity == null) return null;

    return LegalRecoveryResponseDto.builder()
            .id(entity.getId())

            // ClaimApplication
            .claimApplicationId(
                    entity.getClaimApplication() != null
                            ? entity.getClaimApplication().getId()
                            : null
            )
            .claimApplicationNumber(
                    entity.getClaimApplication() != null
                            ? entity.getClaimApplication().getApplicationNumber()
                            : null
            )

            // ClaimDetail
            .claimDetailId(
                    entity.getClaimDetail() != null
                            ? entity.getClaimDetail().getId()
                            : null
            )

            // PayeeType
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

            // Basic fields
            .judgementNumber(entity.getJudgementNumber())
            .judgementDate(entity.getJudgementDate())
            .dzongkhagId(entity.getDzongkhag().getDzongkhagId())
            .dzongkhagName(entity.getDzongkhag().getDzongkhagName())
            .convictedOrder(entity.getConvictedOrder())
            .isConvicted(entity.getIsConvicted())
            .payToMember(entity.getPayToMember())

            // Audit fields
            .createdBy(entity.getCreatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedBy(entity.getUpdatedBy())
            .updatedAt(entity.getUpdatedAt())

            .build();
}



// Helper mapper methods for nested objects
private List<ClaimBankResponseDto> mapBankDetails(List<ClaimBankDetail> bankDetails) {
    if (bankDetails == null) {
        return null;
    }
    return bankDetails.stream()
            .map(this::mapBankDetail)
            .collect(Collectors.toList());
}

private ClaimBankResponseDto mapBankDetail(ClaimBankDetail source) {
    if (source == null) {
        return null;
    }
    return ClaimBankResponseDto.builder()
            .id(source.getId())
            .beneficiaryIdentifier(source.getBeneficiaryIdentifier())
            .claimantTypeId(source.getClaimantType() != null ? source.getClaimantType().getId() : null)
            .claimantTypeName(source.getClaimantType() != null ? source.getClaimantType().getName() : null)
            .bankTypeId(source.getBankType() != null ? source.getBankType().getBankTypeId() : null)
            .bankTypeName(source.getBankType() != null ? source.getBankType().getBankTypeName() : null)
            .accountNumber(source.getAccountNumber())
            .accountHolderName(source.getAccountHolderName())
            .ifscOrRoutingCode(source.getIfscOrRoutingCode())
            .isDefaultBank(source.getIsDefaultBank())
            .verifiedBy(source.getVerifiedBy())
            .verifiedAt(source.getVerifiedAt() != null
        ? source.getVerifiedAt().toLocalDateTime()
        : null)
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null
        ? source.getCreatedAt().toLocalDateTime()
        : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null
        ? source.getUpdatedAt().toLocalDateTime()
        : null)
            .build();
}

private ClaimDeductionResponseDto mapDeductionDetail(ClaimDeductionDetail source) {
    if (source == null) {
        return null;
    }
    List<ClaimDeductionItem> deductionItems = claimDeductionItemRepository.findByDeductionDetail_Id(source.getId());
    return ClaimDeductionResponseDto.builder()
            .id(source.getId())
            .outstandingAmount(source.getOutstandingAmount())
            .verifiedDeductedAmount(source.getVerifiedDeductedAmount())
            .approvedDeductedAmount(source.getApprovedDeductedAmount())
            .deductedAmount(source.getDeductedAmount())
            .remarks(source.getRemarks())
            .deductionItems(mapDeductionItems(deductionItems))
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private List<ClaimDeductionItemResponseDto> mapDeductionItems(List<ClaimDeductionItem> deductionItems) {
    if (deductionItems == null) {
        return null;
    }
    return deductionItems.stream()
            .map(this::mapDeductionItem)
            .collect(Collectors.toList());
}

private ClaimDeductionItemResponseDto mapDeductionItem(ClaimDeductionItem source) {
    if (source == null) {
        return null;
    }
    return ClaimDeductionItemResponseDto.builder()
            .id(source.getId())
            .deductionCategory(source.getDeductionCategory())
            .outstandingAmount(source.getOutstandingAmount())
            .deductedAmount(source.getDeductedAmount())
            .remainingAmount(source.getRemainingAmount())
            .remarks(source.getRemarks())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private ClaimCalculationSummaryResponseDto mapCalculationSummary(ClaimCalculationSummary source) {
    if (source == null) {
        return null;
    }
    List<ClaimRuleEvaluation> ruleEvaluations = claimRuleEvaluationRepository.findByCalculationSummary_Id(source.getId());
    return ClaimCalculationSummaryResponseDto.builder()
            .id(source.getId())
            .calculationEffectiveDate(source.getCalculationEffectiveDate())
            .finalPayableAmount(source.getFinalPayableAmount())
            .totalAmount(source.getTotalAmount())
            .isPfEligible(source.getIsPfEligible())
            .isPensionEligible(source.getIsPensionEligible())
            .totalContributionMonth(source.getTotalContributionMonth())
            .totalNonContributionMonth(source.getTotalNonContributionMonth())
            .totalPfAmount(source.getTotalPfAmount())
            .totalPensionAmount(source.getTotalPensionAmount())
            .totalPfInterest(source.getTotalPfInterest())
            .totalPensionInterest(source.getTotalPensionInterest())
            .recommendedBenefitType(source.getRecommendedBenefitType())
            
            // ================================================================
            // EXCESS SERVICE FIELDS
            // ================================================================
            .excessOpeningBalance(source.getExcessOpeningBalance())
            .excessServiceAmount(source.getExcessServiceAmount())
            .excessCutoffDate(source.getExcessCutoffDate())
            .excessStartDate(source.getExcessStartDate())
            .excessEndDate(source.getExcessEndDate())
            .excessTotalContributions(source.getExcessTotalContributions())
            .excessTotalInterest(source.getExcessTotalInterest())
            .excessEolMonths(source.getExcessEolMonths())
            
            .ruleEvaluations(mapRuleEvaluations(ruleEvaluations))
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private List<ClaimRuleEvaluationListDto> mapRuleEvaluations(List<ClaimRuleEvaluation> ruleEvaluations) {
    if (ruleEvaluations == null) {
        return null;
    }
    return ruleEvaluations.stream()
            .map(this::mapRuleEvaluation)
            .collect(Collectors.toList());
}

private ClaimRuleEvaluationListDto mapRuleEvaluation(ClaimRuleEvaluation source) {
    if (source == null) {
        return null;
    }
    List<ClaimCalculationComponent> components = claimCalculationComponentRepository.findByRuleEvaluation_Id(source.getId());
    return ClaimRuleEvaluationListDto.builder()
            .id(source.getId())
            .calculationSummaryId(source.getCalculationSummary().getId())
            .subClaimCode(source.getSubRule().getSubClaimCode())
            .subClaimType(source.getSubRule().getSubClaimType())
            .subClaimDesc(source.getSubRule().getSubClaimDesc())
            .ruleCode(source.getSubRule().getRuleType().getCode())
            .isRuleApplied(source.getIsRuleApplied())
            .evaluatedAt(source.getEvaluatedAt() != null ? source.getEvaluatedAt().toLocalDateTime() : null)
            .remarks(source.getRemarks())
            .components(mapCalculationComponents(components))
            .build();
}

private List<ClaimCalculationComponentDto> mapCalculationComponents(List<ClaimCalculationComponent> components) {
    if (components == null) {
        return null;
    }
    return components.stream()
            .map(this::mapCalculationComponent)
            .collect(Collectors.toList());
}

private ClaimCalculationComponentDto mapCalculationComponent(ClaimCalculationComponent source) {
    if (source == null) {
        return null;
    }
    return ClaimCalculationComponentDto.builder()
            .id(source.getId())
            .ruleEvaluationId(source.getRuleEvaluation().getId())
            .componentCode(source.getComponentMaster().getCode())
            .componentName(source.getComponentMaster().getName())
            .amount(source.getAmount())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private List<ClaimForfeitedComponentResponseDto> mapForfeitedComponents(List<ClaimForfeitedComponent> sources) {
    if (sources == null) {
        return null;
    }
    return sources.stream()
            .map(this::mapForfeitedComponent)
            .collect(Collectors.toList());
}

private ClaimForfeitedComponentResponseDto mapForfeitedComponent(ClaimForfeitedComponent source) {
    if (source == null) {
        return null;
    }
    return ClaimForfeitedComponentResponseDto.builder()
            .id(source.getId())
            .componentCode(source.getComponentCode())
            .componentName(source.getComponentName())
            .componentType(source.getComponentType())
            .amount(source.getAmount())
            .ruleCode(source.getRuleCode())
            .subClaimCode(source.getSubClaimCode())
            .reason(source.getReason())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private NormalClaimResponseDto mapNormalClaimDetails(NormalClaimDetail source) {
    if (source == null) {
        return null;
    }
    return NormalClaimResponseDto.builder()
            .id(source.getId())
            .cessationTypeId(source.getCessationType() != null ? source.getCessationType().getId() : null)
            .cessationTypeName(source.getCessationType() != null ? source.getCessationType().getName() : null)
            .payeeTypeId(source.getPayeeType() != null ? source.getPayeeType().getId() : null)
            .payeeTypeName(source.getPayeeType() != null ? source.getPayeeType().getName() : null)
            .terminationReasonTypeId(source.getTerminationReasonType() != null ? source.getTerminationReasonType().getId() : null)
            .terminationReasonTypeName(source.getTerminationReasonType() != null ? source.getTerminationReasonType().getName() : null)
            .cessationEffectiveDate(source.getCessationEffectiveDate())
            .dateOfServiceJoining(source.getDateOfServiceJoining())
            .terminatedBy(source.getTerminatedBy())
            .terminationRemarks(source.getTerminationRemarks())
            .relievingOrderNumber(source.getRelievingOrderNumber())
            .relievingReferenceNumber(source.getRelievingReferenceNumber())
            .lastPayMonth(source.getLastPayMonth())
            .finalBasicSalary(source.getFinalBasicSalary())
            .remarks(source.getRemarks())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private PartialWithdrawalResponseDto mapPartialWithdrawalDetails(PartialWithdrawalDetail source) {
    if (source == null) {
        return null;
    }
    return PartialWithdrawalResponseDto.builder()
            .id(source.getId())
            .payeeTypeId(source.getPayeeType() != null ? source.getPayeeType().getId() : null)
            .payeeTypeName(source.getPayeeType() != null ? source.getPayeeType().getName() : null)
            .withdrawalReasonId(source.getWithdrawalReason() != null ? source.getWithdrawalReason().getId() : null)
            .withdrawalReasonName(source.getWithdrawalReason() != null ? source.getWithdrawalReason().getName() : null)
            .actualWithdrawalAmount(source.getActualWithdrawalAmount())
            .unemploymentStartDate(source.getUnemploymentStartDate())
            .disabilityDate(source.getDisabilityDate())
            .unemploymentCauseId(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getId() : null)
            .unemploymentCauseCode(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getCode() : null)
            .unemploymentCauseName(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getName() : null)
            .incidentDate(source.getIncidentDate())
            .placeOfIncident(source.getPlaceOfIncident())
            .businessTypeId(source.getBusinessType() != null ? source.getBusinessType().getId() : null)
            .businessTypeName(source.getBusinessType() != null ? source.getBusinessType().getName() : null)
            .businessName(source.getBusinessName())
            .proposedInvestmentAmount(source.getProposedInvestmentAmount())
            .housePurchaseType(source.getHousePurchaseType())
            .propertyLocation(source.getPropertyLocation())
            .estimatedCost(source.getEstimatedCost())
            .description(source.getDescription())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private BeneficiarySettlementResponseDto mapBeneficiarySettlementDetails(BeneficiarySettlementDetail source) {
    if (source == null) {
        return null;
    }
    List<BeneficiaryClaimantDetail> claimantDetails = beneficiaryClaimantDetailRepository.findByBeneficiarySettlementDetail_Id(source.getId());
    return BeneficiarySettlementResponseDto.builder()
            .id(source.getId())
            .beneficiaryClaimantDetails(mapBeneficiaryClaimants(claimantDetails))
            .cessationTypeId(source.getCessationType().getId())
            .cessationTypeName(source.getCessationType().getName())
            .dateOfDeath(source.getDateOfDeath())
            .lastContributionDate(source.getLastContributionDate())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
            .build();
}

private List<BeneficiaryClaimantResponseDto> mapBeneficiaryClaimants(List<BeneficiaryClaimantDetail> sources) {
    if (sources == null) {
        return null;
    }
    return sources.stream()
            .map(this::mapBeneficiaryClaimant)
            .collect(Collectors.toList());
}

private BeneficiaryClaimantResponseDto mapBeneficiaryClaimant(BeneficiaryClaimantDetail source) {
    if (source == null) {
        return null;
    }
    return BeneficiaryClaimantResponseDto.builder()
            .id(source.getId())
            .beneficiarySettlementDetailId(source.getBeneficiarySettlementDetail() != null ? source.getBeneficiarySettlementDetail().getId() : null)
            .nomineeId(source.getNominee() != null ? source.getNominee().getId() : null)
            .nomineeFirstName(source.getNominee() != null ? source.getNominee().getFirstName() : null)
            .nomineeMiddleName(source.getNominee() != null ? source.getNominee().getMiddleName() : null)
            .nomineeLastName(source.getNominee() != null ? source.getNominee().getLastName() : null)
            .dependentId(source.getDependent() != null ? source.getDependent().getId() : null)
            .dependentFirstName(source.getDependent() != null ? source.getDependent().getFirstName() : null)
            .dependentMiddleName(source.getDependent() != null ? source.getDependent().getMiddleName() : null)
            .dependentLastName(source.getDependent() != null ? source.getDependent().getLastName() : null)
            .claimantTypeId(source.getClaimantType() != null ? source.getClaimantType().getId() : null)
            .claimantTypeName(source.getClaimantType() != null ? source.getClaimantType().getName() : null)
            .payeeTypeId(source.getPayeeType() != null ? source.getPayeeType().getId() : null)
            .payeeTypeName(source.getPayeeType() != null ? source.getPayeeType().getName() : null)
            .relationshipTypeId(source.getRelationshipType() != null ? source.getRelationshipType().getRelationTypeId() : null)
            .relationshipTypeName(source.getRelationshipType() != null ? source.getRelationshipType().getRelationTypeName() : null)
            .beneficiaryIdentifier(source.getBeneficiaryIdentifier())
            .beneficiaryName(source.getBeneficiaryName())
            .dateOfBirth(source.getDateOfBirth())
            .beneficiarySharePercentage(source.getBeneficiarySharePercentage())
            .isMemberFamily(source.getIsMemberFamily())
            .isMinor(source.getIsMinor())
            .guardianName(source.getGuardianName())
            .guardianIdentifier(source.getGuardianIdentifier()) 
            .benefitAmount(source.getBenefitAmount())
            .remarks(source.getRemarks())
            .createdBy(source.getCreatedBy())
            .createdAt(source.getCreatedAt())
            .updatedBy(source.getUpdatedBy())
            .updatedAt(source.getUpdatedAt())
            .build();
}
}
