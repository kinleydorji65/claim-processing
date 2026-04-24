package com.claim.claim_processing.common.mapper.special_case;

import com.claim.claim_processing.common.DTO.request.specialCase.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.specialCase.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.entities.specialCase.SpecialCaseRefundAuthorityMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialCaseAuthorityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SpecialCaseRefundAuthorityMaster toEntity(SpecialCaseAuthorityRequestDto dto);

    SpecialCaseAuthorityResponseDto toResponseDto(SpecialCaseRefundAuthorityMaster entity);

    List<SpecialCaseAuthorityResponseDto> toResponseDtoList(List<SpecialCaseRefundAuthorityMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(SpecialCaseAuthorityUpdateRequestDto dto,
                             @MappingTarget SpecialCaseRefundAuthorityMaster entity);
}