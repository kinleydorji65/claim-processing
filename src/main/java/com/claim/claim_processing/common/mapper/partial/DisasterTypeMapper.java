package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.DisasterTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.DisasterTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.DisasterTypeUpdateDto;
import com.claim.claim_processing.common.entities.partial.DisasterTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DisasterTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    DisasterTypeMaster toEntity(DisasterTypeRequestDto dto);

    DisasterTypeResponseDto toResponseDto(DisasterTypeMaster entity);

    List<DisasterTypeResponseDto> toResponseDtoList(List<DisasterTypeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(DisasterTypeUpdateDto dto,
                             @MappingTarget DisasterTypeMaster entity);
}