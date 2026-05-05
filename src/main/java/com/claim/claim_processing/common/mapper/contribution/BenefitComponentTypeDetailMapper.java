package com.claim.claim_processing.common.mapper.contribution;

import com.claim.claim_processing.common.DTO.request.contribution.BenefitComponentDetailRequestDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentDetailResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.ComponentResponseDto;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeDetail;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;
import com.claim.claim_processing.common.entities.contribution.ComponentMaster;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BenefitComponentTypeDetailMapper {

    // =============================================
    // TO RESPONSE DTO
    // =============================================
    @Mapping(source = "benefitComponentType", target = "benefitComponentType", qualifiedByName = "mapBenefitComponentType")
    @Mapping(source = "component", target = "components", qualifiedByName = "mapComponent")
    BenefitComponentDetailResponseDto toResponseDto(BenefitComponentTypeDetail entity);

    List<BenefitComponentDetailResponseDto> toResponseDtoList(List<BenefitComponentTypeDetail> entities);

    // =============================================
    // TO ENTITY (CREATE)
    // =============================================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    BenefitComponentTypeDetail toEntity(BenefitComponentDetailRequestDto dto);

    // =============================================
    // UPDATE ENTITY
    // =============================================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(BenefitComponentDetailRequestDto dto,
                             @MappingTarget BenefitComponentTypeDetail entity);

    // =============================================
    // NAMED MAPPERS (FK HANDLING)
    // =============================================
    @Named("mapBenefitComponentType")
    static BenefitComponentTypeResponseDto mapBenefitComponentType(BenefitComponentTypeMaster entity) {
        if (entity == null) return null;

        return BenefitComponentTypeResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    @Named("mapComponent")
    static ComponentResponseDto mapComponent(ComponentMaster entity) {
        if (entity == null) return null;

        return ComponentResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}