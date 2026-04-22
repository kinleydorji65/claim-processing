package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.BusinessTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.BusinessTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.BusinessTypeUpdateDto;
import com.claim.claim_processing.common.entities.partial.BusinessTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BusinessTypeMaster toEntity(BusinessTypeRequestDto dto);

    BusinessTypeResponseDto toResponseDto(BusinessTypeMaster entity);

    List<BusinessTypeResponseDto> toResponseDtoList(List<BusinessTypeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(BusinessTypeUpdateDto dto,
                             @MappingTarget BusinessTypeMaster entity);
}