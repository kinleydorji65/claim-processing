package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClaimVestingRuleMasterMapper {

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================
    @Mapping(source = "category", target = "category")
    @Mapping(source = "cutoff", target = "cutoff")
    @Mapping(source = "refund", target = "refund")
    @Mapping(source = "ruleType", target = "ruleType")
    ClaimVestingRuleResponseDto toDto(ClaimVestingRuleMaster entity);

    List<ClaimVestingRuleResponseDto> toDto(List<ClaimVestingRuleMaster> entities);

    // =========================
    // REQUEST DTO → ENTITY
    // (FKs handled in service)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cutoff", ignore = true)
    @Mapping(target = "refund", ignore = true)
    @Mapping(target = "ruleType", ignore = true)

    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ClaimVestingRuleMaster toEntity(ClaimVestingRuleRequestDto dto);

    // =========================
    // PATCH UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cutoff", ignore = true)
    @Mapping(target = "refund", ignore = true)
    @Mapping(target = "ruleType", ignore = true)

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(
            ClaimVestingRuleRequestDto dto,
            @MappingTarget ClaimVestingRuleMaster entity
    );
}