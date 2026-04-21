package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.entities.claim.AccountTypeMaster;
import com.claim.claim_processing.common.DTO.request.claim.AccountTypeCreateRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.AccountTypeResponseDto;
import com.claim.claim_processing.common.DTO.update.claim.AccountTypeUpdateRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "Y")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    AccountTypeMaster toEntity(AccountTypeCreateRequestDto dto);

    AccountTypeResponseDto toResponseDto(AccountTypeMaster entity);

    List<AccountTypeResponseDto> toResponseDtoList(List<AccountTypeMaster> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(AccountTypeUpdateRequestDto dto, @MappingTarget AccountTypeMaster entity);
}