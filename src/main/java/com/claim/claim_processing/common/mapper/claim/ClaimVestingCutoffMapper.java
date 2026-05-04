package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingCutoffRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingCutoffResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimVestingCutoffMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimVestingCutoffMapper {

    // -----------------------------
    // ENTITY -> RESPONSE DTO
    // -----------------------------
    @Mapping(source = "isActive", target = "isActive")
    ClaimVestingCutoffResponseDto toResponseDto(ClaimVestingCutoffMaster entity);

    List<ClaimVestingCutoffResponseDto> toResponseDtoList(List<ClaimVestingCutoffMaster> entities);

    // -----------------------------
    // REQUEST DTO -> ENTITY (CREATE)
    // -----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "isActive", target = "isActive")
    ClaimVestingCutoffMaster toEntity(ClaimVestingCutoffRequestDto dto);

    // -----------------------------
    // REQUEST DTO -> ENTITY (UPDATE)
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(
            ClaimVestingCutoffRequestDto dto,
            @MappingTarget ClaimVestingCutoffMaster entity
    );
}