package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.ReserveAccountMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReserveAccountMapper {

    // -------------------------------
    // CREATE
    // -------------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReserveAccountMaster toEntity(ReserveAccountRequestDto dto);

    // -------------------------------
    // RESPONSE
    // -------------------------------
    @Mapping(source = "accountType", target = "accountType")
    @Mapping(source = "schemeType", target = "schemeType")
    ReserveAccountResponseDto toResponseDto(ReserveAccountMaster entity);

    // -------------------------------
    // LIST RESPONSE
    // -------------------------------
    List<ReserveAccountResponseDto> toResponseDtoList(
            List<ReserveAccountMaster> entities
    );

    // -------------------------------
    // UPDATE
    // -------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            ReserveAccountRequestDto dto,
            @MappingTarget ReserveAccountMaster entity
    );
}