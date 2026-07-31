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
        if (claimDetail == null) {
            return null;
        }

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
                .claimTypeId(claimDetail.getClaimType() != null ? claimDetail.getClaimType().getId() : null)
                .claimTypeName(claimDetail.getClaimType() != null ? claimDetail.getClaimType().getName() : null)
                .applicationNumber(claimDetail.getApplicationNumber() != null ? claimDetail.getApplicationNumber() : null)
                .submissionChannelId(claimDetail.getSubmissionChannel() != null ? claimDetail.getSubmissionChannel().getId() : null)
                .submissionChannelName(claimDetail.getSubmissionChannel() != null ? claimDetail.getSubmissionChannel().getName() : null)
                .schemeTypeId(claimDetail.getSchemeType() != null ? claimDetail.getSchemeType().getId() : null)
                .schemeTypeName(claimDetail.getSchemeType() != null ? claimDetail.getSchemeType().getName() : null)
                .memberCategoryId(claimDetail.getMemberCategory() != null ? claimDetail.getMemberCategory().getCategoryId() : null)
                .memberCategoryName(claimDetail.getMemberCategory() != null ? claimDetail.getMemberCategory().getCategoryName() : null)
                .employmentType(claimDetail.getEmploymentType() != null ? claimDetail.getEmploymentType() : null)
                .memberCode(claimDetail.getMemberCode() != null ? claimDetail.getMemberCode() : null)
                .nppfNumber(claimDetail.getNppfNumber() != null ? claimDetail.getNppfNumber() : null)
                .agencyCode(claimDetail.getAgencyCode() != null ? claimDetail.getAgencyCode() : null)
                .identityNumber(claimDetail.getIdentityNumber() != null ? claimDetail.getIdentityNumber() : null)
                .officeId(claimDetail.getOfficeId() != null ? claimDetail.getOfficeId() : null)
                .applicationDate(claimDetail.getApplicationDate() != null ? claimDetail.getApplicationDate() : null)
                .email(claimDetail.getEmail() != null ? claimDetail.getEmail() : null)
                .contactNo(claimDetail.getContactNo() != null ? claimDetail.getContactNo() : null)
                .pfStartDate(claimDetail.getPfStartDate() != null ? claimDetail.getPfStartDate() : null)
                .pfEndDate(claimDetail.getPfEndDate() != null ? claimDetail.getPfEndDate() : null)
                .pensionStartDate(claimDetail.getPensionStartDate() != null ? claimDetail.getPensionStartDate() : null)
                .pensionEndDate(claimDetail.getPensionEndDate() != null ? claimDetail.getPensionEndDate() : null)
                .isLoanApplied(claimDetail.getIsLoanApplied() != null ? claimDetail.getIsLoanApplied() : null)
                .isRentalApplied(claimDetail.getIsRentalApplied() != null ? claimDetail.getIsRentalApplied() : null)
                .onBehalfOfMember(claimDetail.getOnBehalfOfMember() != null ? claimDetail.getOnBehalfOfMember() : null)
                .initiatedBy(claimDetail.getInitiatedBy() != null ? claimDetail.getInitiatedBy() : null)
                .remarks(claimDetail.getRemarks() != null ? claimDetail.getRemarks() : null)
                .isSpecialCase(claimDetail.getIsSpecialCase() != null ? claimDetail.getIsSpecialCase() : null)
                .isActive(claimDetail.getIsActive() != null ? claimDetail.getIsActive() : null)
                .currencyCode(claimDetail.getCurrencyCode() != null ? claimDetail.getCurrencyCode() : null)
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
                .createdBy(claimDetail.getCreatedBy() != null ? claimDetail.getCreatedBy() : null)
                .createdAt(claimDetail.getCreatedAt() != null ? claimDetail.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(claimDetail.getUpdatedBy() != null ? claimDetail.getUpdatedBy() : null)
                .updatedAt(claimDetail.getUpdatedAt() != null ? claimDetail.getUpdatedAt().toLocalDateTime() : null)
                .build(); 
    }

    private LegalRecoveryResponseDto mapLegalRecoveryDetails(LegalRecoveryDetail entity) {
        if (entity == null) return null;

        return LegalRecoveryResponseDto.builder()
                .id(entity.getId())
                .claimApplicationId(entity.getClaimApplication() != null ? entity.getClaimApplication().getId() : null)
                .claimApplicationNumber(entity.getClaimApplication() != null ? entity.getClaimApplication().getApplicationNumber() : null)
                .claimDetailId(entity.getClaimDetail() != null ? entity.getClaimDetail().getId() : null)
                .payeeTypeId(entity.getPayeeType() != null ? entity.getPayeeType().getId() : null)
                .payeeTypeName(entity.getPayeeType() != null ? entity.getPayeeType().getName() : null)
                .judgementNumber(entity.getJudgementNumber() != null ? entity.getJudgementNumber() : null)
                .judgementDate(entity.getJudgementDate() != null ? entity.getJudgementDate() : null)
                .dzongkhagId(entity.getDzongkhag() != null ? entity.getDzongkhag().getDzongkhagId() : null)
                .dzongkhagName(entity.getDzongkhag() != null ? entity.getDzongkhag().getDzongkhagName() : null)
                .convictedOrder(entity.getConvictedOrder() != null ? entity.getConvictedOrder() : null)
                .isConvicted(entity.getIsConvicted() != null ? entity.getIsConvicted() : null)
                .payToMember(entity.getPayToMember() != null ? entity.getPayToMember() : null)
                .createdBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : null)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt() : null)
                .updatedBy(entity.getUpdatedBy() != null ? entity.getUpdatedBy() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
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
                .beneficiaryIdentifier(source.getBeneficiaryIdentifier() != null ? source.getBeneficiaryIdentifier() : null)
                .claimantTypeId(source.getClaimantType() != null ? source.getClaimantType().getId() : null)
                .claimantTypeName(source.getClaimantType() != null ? source.getClaimantType().getName() : null)
                .bankTypeId(source.getBankType() != null ? source.getBankType().getBankTypeId() : null)
                .bankTypeName(source.getBankType() != null ? source.getBankType().getBankTypeName() : null)
                .accountNumber(source.getAccountNumber() != null ? source.getAccountNumber() : null)
                .accountHolderName(source.getAccountHolderName() != null ? source.getAccountHolderName() : null)
                .ifscOrRoutingCode(source.getIfscOrRoutingCode() != null ? source.getIfscOrRoutingCode() : null)
                .isDefaultBank(source.getIsDefaultBank() != null ? source.getIsDefaultBank() : null)
                .verifiedBy(source.getVerifiedBy() != null ? source.getVerifiedBy() : null)
                .verifiedAt(source.getVerifiedAt() != null ? source.getVerifiedAt().toLocalDateTime() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
                .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt().toLocalDateTime() : null)
                .build();
    }

    private ClaimDeductionResponseDto mapDeductionDetail(ClaimDeductionDetail source) {
        if (source == null) {
            return null;
        }
        List<ClaimDeductionItem> deductionItems = claimDeductionItemRepository.findByDeductionDetail_Id(source.getId());
        return ClaimDeductionResponseDto.builder()
                .id(source.getId())
                .outstandingAmount(source.getOutstandingAmount() != null ? source.getOutstandingAmount() : null)
                .verifiedDeductedAmount(source.getVerifiedDeductedAmount() != null ? source.getVerifiedDeductedAmount() : null)
                .approvedDeductedAmount(source.getApprovedDeductedAmount() != null ? source.getApprovedDeductedAmount() : null)
                .deductedAmount(source.getDeductedAmount() != null ? source.getDeductedAmount() : null)
                .remarks(source.getRemarks() != null ? source.getRemarks() : null)
                .deductionItems(mapDeductionItems(deductionItems))
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .deductionCategory(source.getDeductionCategory() != null ? source.getDeductionCategory() : null)
                .outstandingAmount(source.getOutstandingAmount() != null ? source.getOutstandingAmount() : null)
                .deductedAmount(source.getDeductedAmount() != null ? source.getDeductedAmount() : null)
                .remainingAmount(source.getRemainingAmount() != null ? source.getRemainingAmount() : null)
                .remarks(source.getRemarks() != null ? source.getRemarks() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .calculationEffectiveDate(source.getCalculationEffectiveDate() != null ? source.getCalculationEffectiveDate() : null)
                .finalPayableAmount(source.getFinalPayableAmount() != null ? source.getFinalPayableAmount() : null)
                .totalAmount(source.getTotalAmount() != null ? source.getTotalAmount() : null)
                .isPfEligible(source.getIsPfEligible() != null ? source.getIsPfEligible() : null)
                .isPensionEligible(source.getIsPensionEligible() != null ? source.getIsPensionEligible() : null)
                .totalContributionMonth(source.getTotalContributionMonth() != null ? source.getTotalContributionMonth() : null)
                .totalNonContributionMonth(source.getTotalNonContributionMonth() != null ? source.getTotalNonContributionMonth() : null)
                .totalPfAmount(source.getTotalPfAmount() != null ? source.getTotalPfAmount() : null)
                .totalPensionAmount(source.getTotalPensionAmount() != null ? source.getTotalPensionAmount() : null)
                .totalPfInterest(source.getTotalPfInterest() != null ? source.getTotalPfInterest() : null)
                .totalPensionInterest(source.getTotalPensionInterest() != null ? source.getTotalPensionInterest() : null)
                .recommendedBenefitType(source.getRecommendedBenefitType() != null ? source.getRecommendedBenefitType() : null)
                .excessOpeningBalance(source.getExcessOpeningBalance() != null ? source.getExcessOpeningBalance() : null)
                .excessServiceAmount(source.getExcessServiceAmount() != null ? source.getExcessServiceAmount() : null)
                .excessCutoffDate(source.getExcessCutoffDate() != null ? source.getExcessCutoffDate() : null)
                .excessStartDate(source.getExcessStartDate() != null ? source.getExcessStartDate() : null)
                .excessEndDate(source.getExcessEndDate() != null ? source.getExcessEndDate() : null)
                .excessTotalContributions(source.getExcessTotalContributions() != null ? source.getExcessTotalContributions() : null)
                .excessTotalInterest(source.getExcessTotalInterest() != null ? source.getExcessTotalInterest() : null)
                .excessEolMonths(source.getExcessEolMonths() != null ? source.getExcessEolMonths() : null)
                .ruleEvaluations(mapRuleEvaluations(ruleEvaluations))
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .calculationSummaryId(source.getCalculationSummary() != null ? source.getCalculationSummary().getId() : null)
                .subClaimCode(source.getSubRule() != null ? source.getSubRule().getSubClaimCode() : null)
                .subClaimType(source.getSubRule() != null ? source.getSubRule().getSubClaimType() : null)
                .subClaimDesc(source.getSubRule() != null ? source.getSubRule().getSubClaimDesc() : null)
                .ruleCode(source.getSubRule() != null && source.getSubRule().getRuleType() != null ? source.getSubRule().getRuleType().getCode() : null)
                .isRuleApplied(source.getIsRuleApplied() != null ? source.getIsRuleApplied() : null)
                .evaluatedAt(source.getEvaluatedAt() != null ? source.getEvaluatedAt().toLocalDateTime() : null)
                .remarks(source.getRemarks() != null ? source.getRemarks() : null)
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
                .ruleEvaluationId(source.getRuleEvaluation() != null ? source.getRuleEvaluation().getId() : null)
                .componentCode(source.getComponentMaster() != null ? source.getComponentMaster().getCode() : null)
                .componentName(source.getComponentMaster() != null ? source.getComponentMaster().getName() : null)
                .amount(source.getAmount() != null ? source.getAmount() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .componentCode(source.getComponentCode() != null ? source.getComponentCode() : null)
                .componentName(source.getComponentName() != null ? source.getComponentName() : null)
                .componentType(source.getComponentType() != null ? source.getComponentType() : null)
                .amount(source.getAmount() != null ? source.getAmount() : null)
                .ruleCode(source.getRuleCode() != null ? source.getRuleCode() : null)
                .subClaimCode(source.getSubClaimCode() != null ? source.getSubClaimCode() : null)
                .reason(source.getReason() != null ? source.getReason() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .cessationEffectiveDate(source.getCessationEffectiveDate() != null ? source.getCessationEffectiveDate() : null)
                .dateOfServiceJoining(source.getDateOfServiceJoining() != null ? source.getDateOfServiceJoining() : null)
                .terminatedBy(source.getTerminatedBy() != null ? source.getTerminatedBy() : null)
                .terminationRemarks(source.getTerminationRemarks() != null ? source.getTerminationRemarks() : null)
                .relievingOrderNumber(source.getRelievingOrderNumber() != null ? source.getRelievingOrderNumber() : null)
                .relievingReferenceNumber(source.getRelievingReferenceNumber() != null ? source.getRelievingReferenceNumber() : null)
                .lastPayMonth(source.getLastPayMonth() != null ? source.getLastPayMonth() : null)
                .finalBasicSalary(source.getFinalBasicSalary() != null ? source.getFinalBasicSalary() : null)
                .remarks(source.getRemarks() != null ? source.getRemarks() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .actualWithdrawalAmount(source.getActualWithdrawalAmount() != null ? source.getActualWithdrawalAmount() : null)
                .unemploymentStartDate(source.getUnemploymentStartDate() != null ? source.getUnemploymentStartDate() : null)
                .disabilityDate(source.getDisabilityDate() != null ? source.getDisabilityDate() : null)
                .unemploymentCauseId(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getId() : null)
                .unemploymentCauseCode(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getCode() : null)
                .unemploymentCauseName(source.getUnemploymentCauseMaster() != null ? source.getUnemploymentCauseMaster().getName() : null)
                .incidentDate(source.getIncidentDate() != null ? source.getIncidentDate() : null)
                .placeOfIncident(source.getPlaceOfIncident() != null ? source.getPlaceOfIncident() : null)
                .businessTypeId(source.getBusinessType() != null ? source.getBusinessType().getId() : null)
                .businessTypeName(source.getBusinessType() != null ? source.getBusinessType().getName() : null)
                .businessName(source.getBusinessName() != null ? source.getBusinessName() : null)
                .proposedInvestmentAmount(source.getProposedInvestmentAmount() != null ? source.getProposedInvestmentAmount() : null)
                .housePurchaseType(source.getHousePurchaseType() != null ? source.getHousePurchaseType() : null)
                .propertyLocation(source.getPropertyLocation() != null ? source.getPropertyLocation() : null)
                .estimatedCost(source.getEstimatedCost() != null ? source.getEstimatedCost() : null)
                .description(source.getDescription() != null ? source.getDescription() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .cessationTypeId(source.getCessationType() != null ? source.getCessationType().getId() : null)
                .cessationTypeName(source.getCessationType() != null ? source.getCessationType().getName() : null)
                .dateOfDeath(source.getDateOfDeath() != null ? source.getDateOfDeath() : null)
                .lastContributionDate(source.getLastContributionDate() != null ? source.getLastContributionDate() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt().toLocalDateTime() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
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
                .beneficiaryIdentifier(source.getBeneficiaryIdentifier() != null ? source.getBeneficiaryIdentifier() : null)
                .beneficiaryName(source.getBeneficiaryName() != null ? source.getBeneficiaryName() : null)
                .dateOfBirth(source.getDateOfBirth() != null ? source.getDateOfBirth() : null)
                .beneficiarySharePercentage(source.getBeneficiarySharePercentage() != null ? source.getBeneficiarySharePercentage() : null)
                .isMemberFamily(source.getIsMemberFamily() != null ? source.getIsMemberFamily() : null)
                .isMinor(source.getIsMinor() != null ? source.getIsMinor() : null)
                .guardianName(source.getGuardianName() != null ? source.getGuardianName() : null)
                .guardianIdentifier(source.getGuardianIdentifier() != null ? source.getGuardianIdentifier() : null)
                .benefitAmount(source.getBenefitAmount() != null ? source.getBenefitAmount() : null)
                .remarks(source.getRemarks() != null ? source.getRemarks() : null)
                .createdBy(source.getCreatedBy() != null ? source.getCreatedBy() : null)
                .createdAt(source.getCreatedAt() != null ? source.getCreatedAt() : null)
                .updatedBy(source.getUpdatedBy() != null ? source.getUpdatedBy() : null)
                .updatedAt(source.getUpdatedAt() != null ? source.getUpdatedAt() : null)
                .build();
    }
}