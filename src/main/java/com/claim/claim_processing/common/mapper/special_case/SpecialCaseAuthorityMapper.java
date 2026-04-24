package com.claim.claim_processing.common.mapper.special_case;

import com.claim.claim_processing.common.DTO.request.special_case.SpecialCaseAuthorityRequestDto;
import com.claim.claim_processing.common.DTO.response.specialCase.SpecialCaseAuthorityResponseDto;
import com.claim.claim_processing.common.DTO.update.special_case.SpecialCaseAuthorityUpdateRequestDto;
import com.claim.claim_processing.common.entities.special_case.SpecialCaseRefundAuthorityMaster;
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