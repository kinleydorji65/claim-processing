package com.claim.claim_processing.common.mapper.unclaimed;

import com.claim.claim_processing.common.DTO.request.unclaimed.UnclaimedInterestFreezeRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.unclaimed.UnclaimedInterestFreezeRuleResponseDto;
import com.claim.claim_processing.common.entities.unclaimedMaster.UnclaimedInterestFreezeRuleMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterestFreezeRuleMapper {

    // ================= ENTITY → RESPONSE =================
    UnclaimedInterestFreezeRuleResponseDto toResponseDto(
            UnclaimedInterestFreezeRuleMaster entity
    );

    List<UnclaimedInterestFreezeRuleResponseDto> toResponseDtoList(
            List<UnclaimedInterestFreezeRuleMaster> entities
    );

    // ================= REQUEST → ENTITY (CREATE) =================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    UnclaimedInterestFreezeRuleMaster toEntity(
            UnclaimedInterestFreezeRuleRequestDto dto
    );

    // ================= PATCH UPDATE =================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(
            @MappingTarget UnclaimedInterestFreezeRuleMaster entity,
            UnclaimedInterestFreezeRuleRequestDto dto
    );
}