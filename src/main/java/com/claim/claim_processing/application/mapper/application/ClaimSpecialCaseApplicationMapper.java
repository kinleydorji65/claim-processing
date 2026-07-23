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
    @Mapping(target = "caseReasonId", ignore = true) 
    @Mapping(target = "approvedBy", source = "approvedBy")
    @Mapping(target = "approvedDate", source = "approvedDate")
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
    @Mapping(target = "approvedBy", ignore = true) // Set during approval
    @Mapping(target = "approvedDate", ignore = true) // Set during approval
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
    @Mapping(target = "approvedBy", ignore = true) // Set during approval
    @Mapping(target = "approvedDate", ignore = true) // Set during approval
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

    @AfterMapping
    default void afterMappingToEntity(ClaimSpecialCaseApplicationRequestDto dto, @MappingTarget ClaimSpecialCaseApplication entity) {
        if (entity.getIsActive() == null) {
            entity.setIsActive("Y");
        }
    }
}