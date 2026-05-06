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

    // =========================
    // ENTITY → RESPONSE DTO
    // =========================
    @Mapping(source = "benefitComponentType", target = "benefitComponentType")
    @Mapping(source = "component", target = "components")
    BenefitComponentDetailResponseDto toResponseDto(BenefitComponentTypeDetail entity);

    List<BenefitComponentDetailResponseDto> toResponseDtoList(List<BenefitComponentTypeDetail> entities);

    // =========================
    // DTO → ENTITY (CREATE)
    // =========================
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    BenefitComponentTypeDetail toEntity(BenefitComponentDetailRequestDto dto);

    // =========================
    // UPDATE ENTITY
    // =========================
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "benefitComponentType", ignore = true)
    @Mapping(target = "component", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDto(BenefitComponentDetailRequestDto dto,
                             @MappingTarget BenefitComponentTypeDetail entity);

    // =========================
    // FK MAPPERS (AUTO USED BY MAPSTRUCT)
    // =========================

    default BenefitComponentTypeResponseDto map(BenefitComponentTypeMaster entity) {
        if (entity == null) return null;

        return BenefitComponentTypeResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    default ComponentResponseDto map(ComponentMaster entity) {
        if (entity == null) return null;

        return ComponentResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}