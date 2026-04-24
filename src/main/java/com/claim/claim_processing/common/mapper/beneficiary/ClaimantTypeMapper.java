package com.claim.claim_processing.common.mapper.beneficiary;

import com.claim.claim_processing.common.entities.beneficiary_master.ClaimantTypeMaster;
import com.claim.claim_processing.common.DTO.request.beneficiary.ClaimantTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.beneficiary.ClaimantTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.beneficiary.ClaimantTypeUpdateRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimantTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ClaimantTypeMaster toEntity(ClaimantTypeCreateRequestDto dto);

    ClaimantTypeResponseDto toResponseDto(ClaimantTypeMaster entity);

    List<ClaimantTypeResponseDto> toResponseDtoList(List<ClaimantTypeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(ClaimantTypeUpdateRequestDto dto, @MappingTarget ClaimantTypeMaster entity);
}