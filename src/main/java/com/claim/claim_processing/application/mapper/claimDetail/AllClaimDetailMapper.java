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
    @Mapping(target = "currentStage", ignore = true)
    @Mapping(target = "status", ignore = true)

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
    @Mapping(target = "verifiedAt", ignore = true)
    ClaimBankDetail toBankDetailEntity(ClaimApplicationBankResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleEvaluation", ignore = true)
    @Mapping(target = "componentMaster", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimCalculationComponent toCalculationComponentEntity(ClaimApplicationCalculationComponentDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "ruleEvaluations", ignore = true)
    @Mapping(target = "finalPayableAmount", source = "finalPayableAmount")
    ClaimCalculationSummary toCalculationSummaryEntity(ClaimApplicationCalculationSummaryResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deductionItems", ignore = true)
    ClaimDeductionDetail toDeductionDetailEntity(ClaimApplicationDeductionResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deductionDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimDeductionItem toDeductionItemEntity(ClaimApplicationDeductionItemResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimForfeitedComponent toForfeitedComponentEntity(ClaimApplicationForfeitedComponentResponseDto dto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subRule", ignore = true)
    ClaimRuleEvaluation toRuleEvaluationEntity(ClaimApplicationRuleEvaluationListDto dto);

    @Named("localDateTimeToTimestamp")
    static Timestamp localDateTimeToTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }
}
