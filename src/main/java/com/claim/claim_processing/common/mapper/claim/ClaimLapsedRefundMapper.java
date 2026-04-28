package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimLapsedRefundRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimLapsedRefundResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimLapsedRefundMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimLapsedRefundMapper {

    // -------------------------------
    // CREATE
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimLapsedRefundMaster toEntity(ClaimLapsedRefundRequestDto dto);

    // -------------------------------
    // RESPONSE
    // -------------------------------
    @Mapping(source = "claimCircumstance", target = "claimCircumstance")
    @Mapping(source = "cessationType", target = "cessationType")
    @Mapping(source = "schemeType", target = "schemeType")
    ClaimLapsedRefundResponseDto toResponseDto(ClaimLapsedRefundMaster entity);

    // -------------------------------
    // LIST RESPONSE
    // -------------------------------
    List<ClaimLapsedRefundResponseDto> toResponseDtoList(
            List<ClaimLapsedRefundMaster> entities
    );

    // -------------------------------
    // UPDATE
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claimCircumstance", ignore = true)
    @Mapping(target = "cessationType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(
            ClaimLapsedRefundRequestDto dto,
            @MappingTarget ClaimLapsedRefundMaster entity
    );
}