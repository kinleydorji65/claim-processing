package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalRuleResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalRuleMaster;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PartialWithdrawalRuleMapper {

    // -----------------------
    // ENTITY → RESPONSE DTO
    // -----------------------
    @Mapping(source = "category.categoryId", target = "category.categoryId")
    @Mapping(source = "reason.id", target = "reason.id")
    PartialWithdrawalRuleResponseDto toResponseDto(PartialWithdrawalRuleMaster entity);

    // -----------------------
    // REQUEST DTO → ENTITY
    // -----------------------
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    PartialWithdrawalRuleMaster toEntity(PartialWithdrawalRuleRequestDto dto);

    // -----------------------
    // PATCH UPDATE SUPPORT
    // -----------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(
            PartialWithdrawalRuleRequestDto dto,
            @MappingTarget PartialWithdrawalRuleMaster entity
    );
}