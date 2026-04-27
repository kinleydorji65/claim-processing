package com.claim.claim_processing.common.mapper.specialCase;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SpecialCaseAuthorityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract SpecialCaseRefundAuthorityMaster toEntity(SpecialCaseAuthorityRequestDto dto);

    public abstract SpecialCaseAuthorityResponseDto toResponseDto(SpecialCaseRefundAuthorityMaster entity);

    public abstract List<SpecialCaseAuthorityResponseDto> toResponseDtoList(List<SpecialCaseRefundAuthorityMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateEntityFromDto(SpecialCaseAuthorityUpdateRequestDto dto,
                             @MappingTarget SpecialCaseRefundAuthorityMaster entity);
}