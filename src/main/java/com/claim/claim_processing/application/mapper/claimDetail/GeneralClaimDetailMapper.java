package com.claim.claim_processing.application.mapper.claimDetail;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
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
import com.claim.claim_processing.application.entity.detail.NormalClaimDetail;
import com.claim.claim_processing.application.entity.detail.PartialWithdrawalDetail;

@Component
public class GeneralClaimDetailMapper {
    
    public GeneralClaimDetailResponse mapToResponse(ClaimDetail claimDetail) {
    return GeneralClaimDetailResponse.builder()
            .id(claimDetail.getId())
            .claimTypeId(claimDetail.getClaimType().getId())
            .claimTypeName(claimDetail.getClaimType().getName())
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
            .officeId(claimDetail.getOfficeId())
            .applicationDate(claimDetail.getApplicationDate())
            .pfJoiningDate(claimDetail.getPfJoiningDate())
            .pensionJoiningDate(claimDetail.getPensionJoiningDate())
            .onBehalfOfMember(claimDetail.getOnBehalfOfMember())
            .initiatedBy(claimDetail.getInitiatedBy())
            .remarks(claimDetail.getRemarks())
            .isSpecialCase(claimDetail.getIsSpecialCase())
            .isActive(claimDetail.getIsActive())
            .specialCaseAuthorityId(claimDetail.getSpecialCaseAuthority().getId())
            .specialCaseAuthorityName(claimDetail.getSpecialCaseAuthority().getName())
            .currencyCode(claimDetail.getCurrencyCode())
            .currentStageId(claimDetail.getCurrentStage().getId())
            .currentStageName(claimDetail.getCurrentStage().getName())
            .statusId(claimDetail.getStatus().getStatusId())
            .statusName(claimDetail.getStatus().getStatusName())
            .actionId(claimDetail.getAction().getId())
            .actionName(claimDetail.getAction().getName())
            .bankDetails(mapBankDetails(claimDetail.getBankDetails()))
            .deductionDetail(mapDeductionDetail(claimDetail.getDeductionDetail()))
            .calculationSummary(mapCalculationSummary(claimDetail.getCalculationSummary()))
            .forfeitedComponents(mapForfeitedComponents(claimDetail.getForfeitedComponents()))
            .normalClaimDetails(mapNormalClaimDetails(claimDetail.getNormalClaimDetail()))
            .beneficiarySettlementDetails(mapBeneficiarySettlementDetails(claimDetail.getBeneficiarySettlementDetail()))
            .partialWithdrawalDetails(mapPartialWithdrawalDetails(claimDetail.getPartialWithdrawalDetail()))
            .createdBy(claimDetail.getCreatedBy())
            .createdAt(claimDetail.getCreatedAt() != null ? claimDetail.getCreatedAt().toLocalDateTime() : null)
            .updatedBy(claimDetail.getUpdatedBy())
            .updatedAt(claimDetail.getUpdatedAt() != null ? claimDetail.getUpdatedAt().toLocalDateTime() : null)
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
            .claimantTypeId(source.getClaimantType().getId())
            .claimantTypeName(source.getClaimantType().getName())
            .bankTypeId(source.getBankType().getBankTypeId())
            .bankTypeName(source.getBankType().getBankTypeName())
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
    return ClaimDeductionResponseDto.builder()
            .id(source.getId())
            .outstandingAmount(source.getOutstandingAmount())
            .systemDeductedAmount(source.getSystemDeductedAmount())
            .verifiedDeductedAmount(source.getVerifiedDeductedAmount())
            .approvedDeductedAmount(source.getApprovedDeductedAmount())
            .deductedAmount(source.getDeductedAmount())
            .deductionReviewStatusId(source.getDeductionReviewStatus() != null ? source.getDeductionReviewStatus().getId() : null)
            .deductionReviewStatusName(source.getDeductionReviewStatus() != null ? source.getDeductionReviewStatus().getName() : null)
            .isAutoApplied(source.getIsAutoApplied())
            .isManualOverride(source.getIsManualOverride())
            .isActive(source.getIsActive())
            .overrideReason(source.getOverrideReason())
            .remarks(source.getRemarks())
            .deductionItems(mapDeductionItems(source.getDeductionItems()))
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
            .referenceNumber(source.getReferenceNumber())
            .referenceName(source.getReferenceName())
            .outstandingAmount(source.getOutstandingAmount())
            .deductedAmount(source.getDeductedAmount())
            .remainingAmount(source.getRemainingAmount())
            .priorityOrder(source.getPriorityOrder())
            .remarks(source.getRemarks())
            .isActive(source.getIsActive())
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
    return ClaimCalculationSummaryResponseDto.builder()
            .id(source.getId())
            .calculationEffectiveDate(source.getCalculationEffectiveDate())
            .finalPayableAmount(source.getFinalPayableAmount())
            .actualAmountCalculated(source.getActualAmountCalculated())
            .isPfEligible(source.getIsPfEligible())
            .isPensionEligible(source.getIsPensionEligible())
            .totalContributionMonth(source.getTotalContributionMonth())
            .recommendedBenefitType(source.getRecommendedBenefitType())
            .calculationStatusId(source.getCalculationStatus().getStatusId())
            .calculationStatusName(source.getCalculationStatus().getStatusName())
            .isActive(source.getIsActive())
            .ruleEvaluations(mapRuleEvaluations(source.getRuleEvaluations()))
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
    return ClaimRuleEvaluationListDto.builder()
            .id(source.getId())
            .calculationSummaryId(source.getCalculationSummary().getId())
            .subClaimCode(source.getSubRule().getSubClaimCode())
            .subClaimType(source.getSubRule().getSubClaimType())
            .subClaimDesc(source.getSubRule().getSubClaimDesc())
            .ruleCode(source.getSubRule().getRuleType().getCode())
            .isRuleApplied(source.getIsRuleApplied())
            .resultMessage(source.getResultMessage())
            .evaluatedBy(source.getEvaluatedBy())
            .evaluatedAt(source.getEvaluatedAt() != null ? source.getEvaluatedAt().toLocalDateTime() : null)
            .remarks(source.getRemarks())
            .isActive(source.getIsActive())
            .components(mapCalculationComponents(source.getComponents()))
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
            .isDeduction(source.getIsDeduction())
            .notes(source.getNotes())
            .isActive(source.getIsActive())
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
            .isActive(source.getIsActive())
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
            .cessationTypeId(source.getCessationType().getId())
            .cessationTypeName(source.getCessationType().getName())
            .payeeTypeId(source.getPayeeType().getId())
            .payeeTypeName(source.getPayeeType().getName())
            .terminationReasonTypeId(source.getTerminationReasonType().getId())
            .terminationReasonTypeName(source.getTerminationReasonType().getName())
            .dateOfTermination(source.getDateOfTermination())
            .pfJoiningDate(source.getPfJoiningDate())
            .pensionJoiningDate(source.getPensionJoiningDate())
            .relievingOrderDate(source.getRelievingOrderDate())
            .cessationEffectiveDate(source.getCessationEffectiveDate())
            .exitDate(source.getExitDate())
            .dateOfServiceJoining(source.getDateOfServiceJoining())
            .terminatedBy(source.getTerminatedBy())
            .terminationRemarks(source.getTerminationRemarks())
            .relievingOrderNumber(source.getRelievingOrderNumber())
            .relievingReferenceNumber(source.getRelievingReferenceNumber())
            .lastPayMonth(source.getLastPayMonth())
            .finalBasicSalary(source.getFinalBasicSalary())
            .nonContributionMonths(source.getNonContributionMonths())
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
            .payeeTypeId(source.getPayeeType().getId())
            .payeeTypeName(source.getPayeeType().getName())
            .withdrawalReasonId(source.getWithdrawalReason().getId())
            .withdrawalReasonName(source.getWithdrawalReason().getName())
            .requestedWithdrawalAmount(source.getRequestedWithdrawalAmount())
            .actualWithdrawalAmount(source.getActualWithdrawalAmount())
            .unemploymentStartDate(source.getUnemploymentStartDate())
            .disabilityDate(source.getDisabilityDate())
            .unemploymentCauseId(source.getUnemploymentCauseMaster().getId())
            .unemploymentCauseCode(source.getUnemploymentCauseMaster().getCode())
            .unemploymentCauseName(source.getUnemploymentCauseMaster().getName())
            .incidentDate(source.getIncidentDate())
            .placeOfIncident(source.getPlaceOfIncident())
            .businessTypeId(source.getBusinessType().getId())
            .businessTypeName(source.getBusinessType().getName())
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
    return BeneficiarySettlementResponseDto.builder()
            .id(source.getId())
            .beneficiaryClaimantDetails(mapBeneficiaryClaimants(source.getClaimantDetails()))
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
