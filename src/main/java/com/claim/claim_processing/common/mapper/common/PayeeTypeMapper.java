package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.PayeeTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.PayeeTypeResponseDto;
import com.claim.claim_processing.common.entities.common.PayeeTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PayeeTypeMapper {

    // 🔹 Create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true) // handled by entity default
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PayeeTypeMaster toEntity(PayeeTypeRequestDto dto);

    // 🔹 Entity → Response
    PayeeTypeResponseDto toResponseDto(PayeeTypeMaster entity);

    List<PayeeTypeResponseDto> toResponseDtoList(List<PayeeTypeMaster> entities);

    // 🔹 Update (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(PayeeTypeRequestDto dto,
                             @MappingTarget PayeeTypeMaster entity);
}