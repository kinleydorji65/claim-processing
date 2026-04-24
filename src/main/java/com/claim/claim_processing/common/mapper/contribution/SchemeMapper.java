package com.claim.claim_processing.common.mapper.contribution;

import com.claim.claim_processing.common.entities.contribution.SchemeMaster;
import com.claim.claim_processing.common.DTO.request.contribution.SchemeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.SchemeResponseDto;
import com.claim.claim_processing.common.DTO.update.contribution.SchemeUpdateRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SchemeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SchemeMaster toEntity(SchemeCreateRequestDto dto);

    SchemeResponseDto toResponseDto(SchemeMaster entity);

    List<SchemeResponseDto> toResponseDtoList(List<SchemeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(SchemeUpdateRequestDto dto, @MappingTarget SchemeMaster entity);
}