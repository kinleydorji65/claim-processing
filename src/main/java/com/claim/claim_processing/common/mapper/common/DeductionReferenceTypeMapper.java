package com.claim.claim_processing.common.mapper.common;

import com.claim.claim_processing.common.DTO.request.common.DeductionReferenceTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.common.DeductionReferenceTypeResponseDto;
import com.claim.claim_processing.common.entities.common.DeductionReferenceTypeMaster;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeductionReferenceTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    DeductionReferenceTypeMaster toEntity(DeductionReferenceTypeRequestDto dto);

    DeductionReferenceTypeResponseDto toResponseDto(DeductionReferenceTypeMaster entity);

    List<DeductionReferenceTypeResponseDto> toResponseDtoList(
            List<DeductionReferenceTypeMaster> entities
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            DeductionReferenceTypeRequestDto dto,
            @MappingTarget DeductionReferenceTypeMaster entity
    );
}