package com.claim.claim_processing.application.mapper.claimDetail;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionItemResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationDeductionResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationForfeitedComponentResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralClaimResponse;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationComponentDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationCalculationSummaryResponseDto;
import com.claim.claim_processing.application.DTO.response.calculation.ClaimApplicationRuleEvaluationListDto;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimCalculationSummary;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDeductionItem;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimForfeitedComponent;
import com.claim.claim_processing.application.entity.claimDetail.ClaimRuleEvaluation;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AllClaimDetailMapper {
    
    // Main mapping methods
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "submissionChannel", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "memberCategory", ignore = true)
    @Mapping(target = "specialCaseAuthority", ignore = true)
    @Mapping(target = "currentStage", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "action", ignore = true)

    @Mapping(target = "normalClaimDetail", ignore = true)
    @Mapping(target = "partialWithdrawalDetail", ignore = true)
    @Mapping(target = "beneficiarySettlementDetail", ignore = true)
    // @Mapping(target = "legalRecoveryDetail", ignore = true)

    @Mapping(target = "bankDetails", ignore = true)
    @Mapping(target = "deductionDetail", ignore = true)
    @Mapping(target = "calculationSummary", ignore = true)
    @Mapping(target = "forfeitedComponents", ignore = true)

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToTimestamp")
    ClaimDetail toEntity(GeneralClaimResponse response);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "claimantType", ignore = true)
    @Mapping(target = "bankType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "beneficiaryIdentifier", source = "beneficiaryIdentifier")
    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "accountHolderName", source = "accountHolderName")
    @Mapping(target = "ifscOrRoutingCode", source = "ifscOrRoutingCode")
    @Mapping(target = "isDefaultBank", source = "isDefaultBank")
    @Mapping(target = "verifiedBy", source = "verifiedBy")
    @Mapping(target = "verifiedAt", ignore = true) // Assuming verification timestamp is set in the entity/service layer
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimBankDetail toBankDetailEntity(ClaimApplicationBankResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleEvaluation", ignore = true)
    @Mapping(target = "componentMaster", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "isDeduction", source = "isDeduction", defaultValue = "N")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimCalculationComponent toCalculationComponentEntity(ClaimApplicationCalculationComponentDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "calculationStage", ignore = true)
    @Mapping(target = "calculationStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ruleEvaluations", ignore = true)
    @Mapping(target = "calculationEffectiveDate", source = "calculationEffectiveDate")
    @Mapping(target = "finalPayableAmount", source = "finalPayableAmount")
    @Mapping(target = "actualAmountCalculated", source = "actualAmountCalculated")
    @Mapping(target = "isPfEligible", source = "isPfEligible", defaultValue = "N")
    @Mapping(target = "isPensionEligible", source = "isPensionEligible", defaultValue = "N")
    @Mapping(target = "totalContributionMonth", source = "totalContributionMonth")
    @Mapping(target = "recommendedBenefitType", source = "recommendedBenefitType")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimCalculationSummary toCalculationSummaryEntity(ClaimApplicationCalculationSummaryResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "deductionReviewStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deductionItems", ignore = true)
    @Mapping(target = "outstandingAmount", source = "outstandingAmount")
    @Mapping(target = "systemDeductedAmount", source = "systemDeductedAmount")
    @Mapping(target = "verifiedDeductedAmount", source = "verifiedDeductedAmount")
    @Mapping(target = "approvedDeductedAmount", source = "approvedDeductedAmount")
    @Mapping(target = "deductedAmount", source = "deductedAmount")
    @Mapping(target = "isAutoApplied", source = "isAutoApplied", defaultValue = "N")
    @Mapping(target = "isManualOverride", source = "isManualOverride", defaultValue = "N")
    @Mapping(target = "overrideReason", source = "overrideReason")
    @Mapping(target = "remarks", source = "remarks")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimDeductionDetail toDeductionDetailEntity(ClaimApplicationDeductionResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deductionDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deductionCategory", source = "deductionCategory")
    @Mapping(target = "referenceNumber", source = "referenceNumber")
    @Mapping(target = "referenceName", source = "referenceName")
    @Mapping(target = "outstandingAmount", source = "outstandingAmount")
    @Mapping(target = "deductedAmount", source = "deductedAmount")
    @Mapping(target = "remainingAmount", source = "remainingAmount")
    @Mapping(target = "priorityOrder", source = "priorityOrder")
    @Mapping(target = "remarks", source = "remarks")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimDeductionItem toDeductionItemEntity(ClaimApplicationDeductionItemResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "componentCode", source = "componentCode")
    @Mapping(target = "componentName", source = "componentName")
    @Mapping(target = "componentType", source = "componentType")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "ruleCode", source = "ruleCode")
    @Mapping(target = "subClaimCode", source = "subClaimCode")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    ClaimForfeitedComponent toForfeitedComponentEntity(ClaimApplicationForfeitedComponentResponseDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subRule", ignore = true)
    @Mapping(target = "isRuleApplied", source = "isRuleApplied")
    @Mapping(target = "resultMessage", source = "resultMessage")
    @Mapping(target = "evaluatedAt", source = "evaluatedAt", qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "evaluatedBy", source = "evaluatedBy")
    @Mapping(target = "remarks", source = "remarks")
    @Mapping(target = "isActive", source = "isActive", defaultValue = "Y")
    ClaimRuleEvaluation toRuleEvaluationEntity(ClaimApplicationRuleEvaluationListDto dto);

    @Named("localDateTimeToTimestamp")
    static Timestamp localDateTimeToTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }
}
