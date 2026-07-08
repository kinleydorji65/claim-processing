package com.claim.claim_processing.application.mapper.application;

import org.mapstruct.*;

import com.claim.claim_processing.application.DTO.request.application.ClaimSpecialCaseApplicationRequestDto;
import com.claim.claim_processing.application.DTO.response.application.ClaimSpecialCaseApplicationResponseDto;
import com.claim.claim_processing.application.entity.application.ClaimSpecialCaseApplication;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimSpecialCaseApplicationMapper {

    /**
     * Convert Entity to Response DTO
     * All fields from entity are mapped to response
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "claimApplicationId", source = "claimApplication.id")
    @Mapping(target = "caseType", source = "caseType")
    @Mapping(target = "caseReasonId", ignore = true) // Set in service from caseReasonId
    @Mapping(target = "pensionType", source = "pensionType")
    @Mapping(target = "pensionStartDate", source = "pensionStartDate")
    @Mapping(target = "totalContributionYears", source = "totalContributionYears")
    @Mapping(target = "totalPensionAmount", source = "totalPensionAmount")
    @Mapping(target = "pensionAccountId", ignore = true) // Set from pension detail in service
    @Mapping(target = "currentBenefitType", source = "currentBenefitType")
    @Mapping(target = "requestedBenefitType", source = "requestedBenefitType")
    @Mapping(target = "totalForfeitedAmount", source = "totalForfeitedAmount")
    @Mapping(target = "eligibleClaimAmount", source = "eligibleClaimAmount")
    @Mapping(target = "forfeitedDate", source = "forfeitedDate")
    @Mapping(target = "componentCodes", source = "componentCodes")
    @Mapping(target = "approvedAmount", source = "approvedAmount")
    @Mapping(target = "approvedBy", source = "approvedBy")
    @Mapping(target = "approvedDate", source = "approvedDate")
    @Mapping(target = "approvalReference", source = "approvalReference")
    @Mapping(target = "rejectionReason", source = "rejectionReason")
    @Mapping(target = "reserveAccountId", ignore = true) // Set from reserve account in service
    @Mapping(target = "isActive", source = "isActive")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ClaimSpecialCaseApplicationResponseDto toResponseDto(ClaimSpecialCaseApplication entity);

    /**
     * Convert Request DTO to Entity (CREATE)
     * Client only provides: caseType, caseReason, currentBenefitType, 
     * requestedBenefitType, requestedAmount, reserveAccountId
     * All other fields are set by the service layer
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true) // FK - Set in service
    @Mapping(target = "pensionAccount", ignore = true) // FK - Set from pension detail in service
    @Mapping(target = "reserveAccount", ignore = true) // FK - Set from reserve account in service
    @Mapping(target = "approvedAmount", ignore = true) // Set during approval
    @Mapping(target = "approvedBy", ignore = true) // Set during approval
    @Mapping(target = "approvedDate", ignore = true) // Set during approval
    @Mapping(target = "approvalReference", ignore = true) // Set during approval
    @Mapping(target = "rejectionReason", ignore = true) // Set during rejection
    @Mapping(target = "createdBy", ignore = true) // Set in service from security context
    @Mapping(target = "createdAt", ignore = true) // Set in entity @PrePersist
    @Mapping(target = "updatedBy", ignore = true) // Set in service
    @Mapping(target = "updatedAt", ignore = true) // Set in entity @PreUpdate
    // ALL OTHER FIELDS WILL BE MAPPED AUTOMATICALLY!
    ClaimSpecialCaseApplication toEntity(ClaimSpecialCaseApplicationRequestDto dto);

    /**
     * Update existing entity from Request DTO (PARTIAL UPDATE)
     * Client can only update: caseReason, currentBenefitType, 
     * requestedBenefitType, requestedAmount, reserveAccountId
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimApplication", ignore = true) // Cannot update
    @Mapping(target = "caseType", ignore = true) // Cannot update
    @Mapping(target = "caseReasonId", ignore = true) // Set in service from caseReasonId
    @Mapping(target = "pensionType", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "pensionStartDate", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "totalContributionYears", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "totalPensionAmount", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "pensionAccount", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "currentBenefitType", source = "currentBenefitType")
    @Mapping(target = "requestedBenefitType", source = "requestedBenefitType")
    @Mapping(target = "totalForfeitedAmount", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "eligibleClaimAmount", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "forfeitedDate", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "componentCodes", ignore = true) // Cannot update (snapshot)
    @Mapping(target = "requestedAmount", source = "requestedAmount")
    @Mapping(target = "approvedAmount", ignore = true) // Set during approval
    @Mapping(target = "approvedBy", ignore = true) // Set during approval
    @Mapping(target = "approvedDate", ignore = true) // Set during approval
    @Mapping(target = "approvalReference", ignore = true) // Set during approval
    @Mapping(target = "rejectionReason", ignore = true) // Set during rejection
    @Mapping(target = "reserveAccount", ignore = true) // Set from reserve account in service
    @Mapping(target = "isActive", ignore = true) // Cannot update
    @Mapping(target = "createdBy", ignore = true) // Cannot update
    @Mapping(target = "createdAt", ignore = true) // Cannot update
    @Mapping(target = "updatedBy", ignore = true) // Set in service
    @Mapping(target = "updatedAt", ignore = true) // Set in entity @PreUpdate
    void updateEntityFromDto(ClaimSpecialCaseApplicationRequestDto dto, @MappingTarget ClaimSpecialCaseApplication entity);

    /**
     * Convert List of Entities to List of Response DTOs
     */
    List<ClaimSpecialCaseApplicationResponseDto> toResponseDtoList(List<ClaimSpecialCaseApplication> entities);

    /**
     * After mapping from Request DTO to Entity, set default values
     */
    @AfterMapping
    default void afterMappingToEntity(ClaimSpecialCaseApplicationRequestDto dto, @MappingTarget ClaimSpecialCaseApplication entity) {
        // Set default values if needed
        if (entity.getIsActive() == null) {
            entity.setIsActive("Y");
        }
        if (entity.getRequestedAmount() == null) {
            entity.setRequestedAmount(java.math.BigDecimal.ZERO);
        }
        if (entity.getTotalPensionAmount() == null) {
            entity.setTotalPensionAmount(java.math.BigDecimal.ZERO);
        }
        if (entity.getTotalForfeitedAmount() == null) {
            entity.setTotalForfeitedAmount(java.math.BigDecimal.ZERO);
        }
        if (entity.getEligibleClaimAmount() == null) {
            entity.setEligibleClaimAmount(java.math.BigDecimal.ZERO);
        }
        if (entity.getApprovedAmount() == null) {
            entity.setApprovedAmount(java.math.BigDecimal.ZERO);
        }
    }
}