package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimTypeMasterResponseDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.entities.claim.ClaimTypeMaster;
import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ClaimTypeMasterMapper {
    
    // ENTITY -> RESPONSE DTO
    @Mapping(target = "ruleTypes", expression = "java(mapRuleTypes(entity, mappings))")
    ClaimTypeMasterResponseDto toResponseDto(ClaimTypeMaster entity, @Context List<ClaimTypeRuleMap> mappings);

    default List<RuleTypeResponseDto> mapRuleTypes(ClaimTypeMaster entity, @Context List<ClaimTypeRuleMap> mappings) {
        return mappings.stream()
                .filter(map -> map.getClaimType().getId().equals(entity.getId()))
                .map(map -> RuleTypeResponseDto.builder()
                        .id(map.getRuleType().getId())
                        .code(map.getRuleType().getCode())
                        .name(map.getRuleType().getName())
                        .isActive(map.getRuleType().getIsActive())
                        .createdAt(map.getRuleType().getCreatedAt())
                        .createdBy(map.getRuleType().getCreatedBy())
                        .updatedAt(map.getRuleType().getUpdatedAt())
                        .updatedBy(map.getRuleType().getUpdatedBy())
                        .build())
                .toList();
    }

     // -----------------------------
     // ENTITY -> RESPONSE DTO (without rules)
     // -----------------------------
    // -----------------------------
    // REQUEST DTO -> ENTITY (CREATE)
    // -----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(source = "isActive", target = "isActive")
    ClaimTypeMaster toEntity(ClaimTypeMasterRequestDto dto);

    // -----------------------------
    // REQUEST DTO -> ENTITY (UPDATE)
    // -----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(source = "isActive", target = "isActive")
    void updateEntityFromDto(ClaimTypeMasterRequestDto dto, @MappingTarget ClaimTypeMaster entity);
    
    
}