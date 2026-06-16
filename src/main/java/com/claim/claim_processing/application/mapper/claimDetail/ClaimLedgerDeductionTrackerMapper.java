package com.claim.claim_processing.application.mapper.claimDetail;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.claim.claim_processing.application.DTO.request.claimDetail.ClaimLedgerDeductionTrackerRequestDto;
import com.claim.claim_processing.application.entity.claimDetail.ClaimLedgerDeductionTracker;

@Mapper(componentModel = "spring")
public interface ClaimLedgerDeductionTrackerMapper {

    @Mapping(target = "id", ignore = true)
    // FK
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    // Audit
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    // Completion
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "completedBy", ignore = true)
    ClaimLedgerDeductionTracker toEntity(
            ClaimLedgerDeductionTrackerRequestDto dto
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimDetail", ignore = true)
    @Mapping(target = "claimType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "completedBy", ignore = true)
    void updateEntity(
            ClaimLedgerDeductionTrackerRequestDto dto,
            @MappingTarget ClaimLedgerDeductionTracker entity
    );
}
