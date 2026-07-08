package com.claim.claim_processing.application.mapper.claimDetail;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.claim.claim_processing.application.DTO.response.application.ClaimApplicationBankResponseDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.DTO.response.application.GeneralSpecialCaseApplicationResponseDTO;
import com.claim.claim_processing.application.entity.claimDetail.ClaimBankDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimDetail;
import com.claim.claim_processing.application.entity.claimDetail.ClaimSpecialCase;

@Mapper(componentModel = "spring")
public interface ClaimSpecialCaseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true) // FK ignored - set in service
    @Mapping(target = "caseType", source = "caseType")
    @Mapping(target = "currentBenefitType", source = "currentBenefitType")
    @Mapping(target = "requestedBenefitType", source = "requestedBenefitType")
    @Mapping(target = "isActive", ignore = true) // Set in entity @PrePersist
    @Mapping(target = "createdBy", ignore = true) // Set in service
    @Mapping(target = "createdAt", ignore = true) // Set in entity @PrePersist
    @Mapping(target = "updatedBy", ignore = true) // Set in service
    @Mapping(target = "updatedAt", ignore = true) // Set in entity @PreUpdate
    ClaimSpecialCase toEntity(ClaimSpecialCaseApplicationResponseDto dto);


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

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimDetail toClaimDetailEntity(GeneralSpecialCaseApplicationResponseDTO response);


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
}
