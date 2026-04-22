package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.HousePurchaseTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.HousePurchaseTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.partial.HousePurchaseTypeUpdateDto;
import com.claim.claim_processing.common.entities.partial.HousePurchaseTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HousePurchaseTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    HousePurchaseTypeMaster toEntity(HousePurchaseTypeRequestDto dto);

    HousePurchaseTypeResponseDto toResponseDto(HousePurchaseTypeMaster entity);

    List<HousePurchaseTypeResponseDto> toResponseDtoList(List<HousePurchaseTypeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(HousePurchaseTypeUpdateDto dto,
                             @MappingTarget HousePurchaseTypeMaster entity);
}