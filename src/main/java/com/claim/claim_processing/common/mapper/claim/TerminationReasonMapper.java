package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.entities.claim.TerminationReasonMaster;
import com.claim.claim_processing.common.DTO.request.claim.TerminationReasonCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.TerminationReasonResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.TerminationReasonUpdateRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TerminationReasonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", expression = "java('Y')")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TerminationReasonMaster toEntity(TerminationReasonCreateRequestDto dto);

    TerminationReasonResponseDto toResponseDto(TerminationReasonMaster entity);

    List<TerminationReasonResponseDto> toResponseDtoList(List<TerminationReasonMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(TerminationReasonUpdateRequestDto dto,
                             @MappingTarget TerminationReasonMaster entity);
}