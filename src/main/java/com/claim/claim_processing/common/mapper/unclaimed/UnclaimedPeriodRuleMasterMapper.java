package com.claim.claim_processing.common.mapper.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedPeriodRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedPeriodRuleResponseDto;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedPeriodRuleMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnclaimedPeriodRuleMasterMapper {

    // =========================
    // Entity -> Response DTO
    // =========================
    UnclaimedPeriodRuleResponseDto toResponseDto(
            UnclaimedPeriodRuleMaster entity
    );

    List<UnclaimedPeriodRuleResponseDto> toResponseDtoList(
            List<UnclaimedPeriodRuleMaster> entities
    );

    // =========================
    // Request DTO -> Entity (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnclaimedPeriodRuleMaster toEntity(
            UnclaimedPeriodRuleRequestDto dto
    );

    // =========================
    // PATCH UPDATE
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget UnclaimedPeriodRuleMaster entity,
            UnclaimedPeriodRuleRequestDto dto
    );
}