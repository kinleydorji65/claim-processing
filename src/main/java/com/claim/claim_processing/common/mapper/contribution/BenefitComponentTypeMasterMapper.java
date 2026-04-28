package com.claim.claim_processing.common.mapper.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentTypeMasterRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeMasterResponseDto;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BenefitComponentTypeMasterMapper {

    /**
     * Entity -> Response DTO
     */
    BenefitComponentTypeMasterResponseDto toDto(
            BenefitComponentTypeMaster entity
    );

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