package com.claim.claim_processing.common.mapper.loanMaster;

import com.claim.claim_processing.common.DTO.request.loanMaster.LoanAdjustmentPriorityRequestDto;
import com.claim.claim_processing.common.DTO.response.loanMaster.LoanAdjustmentPriorityResponseDto;
import com.claim.claim_processing.common.entities.loanMaster.LoanAdjustmentPriorityMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {LoanTypeMapper.class}
)
public interface LoanAdjustmentPriorityMapper {

    // 🔹 CREATE
    // loanType handled in service via FK lookup
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    LoanAdjustmentPriorityMaster toEntity(
            LoanAdjustmentPriorityRequestDto dto
    );

    // 🔹 ENTITY → RESPONSE
    LoanAdjustmentPriorityResponseDto toResponseDto(
            LoanAdjustmentPriorityMaster entity
    );

    List<LoanAdjustmentPriorityResponseDto> toResponseDtoList(
            List<LoanAdjustmentPriorityMaster> entities
    );

    // 🔹 UPDATE (PATCH style)
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loanType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntityFromDto(
            LoanAdjustmentPriorityRequestDto dto,
            @MappingTarget LoanAdjustmentPriorityMaster entity
    );
}