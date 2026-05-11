package com.claim.claim_processing.common.mapper.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;

import java.util.List;

import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BenefitComponentTypeMasterMapper {

    /**
     * Entity -> Response DTO
     */
    BenefitComponentTypeMasterResponseDto toResponseDto(
            BenefitComponentTypeMaster entity, 
            List<BenefitComponentTypeDetail> mappings);

    default List<ComponentResponseDto> mapComponents(BenefitComponentTypeMaster entity, @Context List<BenefitComponentTypeDetail> mappings) {
        return mappings.stream()
                .filter(map -> map.getBenefitComponentType().getId().equals(entity.getId()))
                .map(map -> ComponentResponseDto.builder()
                        .id(map.getComponent().getId())
                        .code(map.getComponent().getCode())
                        .name(map.getComponent().getName())
                        .componentType(map.getComponent().getComponentType())
                        .isActive(map.getComponent().getIsActive())
                        .build())
                 .toList();
    }
    /**
     * Request DTO -> Entity
     * Ignore auto-managed fields.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BenefitComponentTypeMaster toEntity(
            BenefitComponentTypeMasterRequestDto dto
    );

    /**
     * Update existing entity from Request DTO
     * Null values will be ignored.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(
            BenefitComponentTypeMasterRequestDto dto,
            @MappingTarget BenefitComponentTypeMaster entity
    );
}