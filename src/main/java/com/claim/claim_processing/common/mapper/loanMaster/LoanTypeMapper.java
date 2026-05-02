package com.claim.claim_processing.common.mapper.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanTypeRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanTypeResponseDto;
import com.claim.claim_processing.common.entities.loanMaster.LoanTypeMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanTypeMapper {

    // 🔹 CREATE
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayOrder", ignore = true) // entity default if null
    @Mapping(target = "isActive", ignore = true)     // entity default if null
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LoanTypeMaster toEntity(LoanTypeRequestDto dto);

    // 🔹 ENTITY → RESPONSE
    LoanTypeResponseDto toResponseDto(LoanTypeMaster entity);

    List<LoanTypeResponseDto> toResponseDtoList(List<LoanTypeMaster> entities);

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            LoanTypeRequestDto dto,
            @MappingTarget LoanTypeMaster entity
    );
}