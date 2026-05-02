package com.claim.claim_processing.common.mapper.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanStatusRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanStatusResponseDto;
import com.claim.claim_processing.common.entities.loanMaster.LoanStatusMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanStatusMapper {

    // 🔹 CREATE MAPPING
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "displayOrder", ignore = true) // handled by entity default if null
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LoanStatusMaster toEntity(LoanStatusRequestDto dto);

    // 🔹 ENTITY → RESPONSE DTO
    LoanStatusResponseDto toResponseDto(LoanStatusMaster entity);

    List<LoanStatusResponseDto> toResponseDtoList(List<LoanStatusMaster> entities);

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true) // immutable field
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            LoanStatusRequestDto dto,
            @MappingTarget LoanStatusMaster entity
    );
}