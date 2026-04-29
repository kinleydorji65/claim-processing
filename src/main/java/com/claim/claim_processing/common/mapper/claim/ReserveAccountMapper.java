package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ReserveAccountRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ReserveAccountResponseDto;
import com.claim.claim_processing.common.entities.claim.ReserveAccountMaster;
import com.claim.claim_processing.common.mapper.contribution.SchemeTypeMapper;

import java.util.List;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AccountTypeMapper.class, SchemeTypeMapper.class})
public interface ReserveAccountMapper {

    // ENTITY → RESPONSE DTO
    @Mapping(source = "accountType", target = "accountType")
    @Mapping(source = "schemeType", target = "schemeType")
    ReserveAccountResponseDto toResponseDto(ReserveAccountMaster entity);

    // REQUEST DTO → ENTITY (CREATE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReserveAccountMaster toEntity(ReserveAccountRequestDto dto);

    // UPDATE EXISTING ENTITY
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountType", ignore = true)
    @Mapping(target = "schemeType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(ReserveAccountRequestDto dto, @MappingTarget ReserveAccountMaster entity);

    List<ReserveAccountResponseDto> toResponseDtoList(
            List<ReserveAccountMaster> entities
    );
}