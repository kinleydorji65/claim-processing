package com.claim.claim_processing.common.mapper.partial;

import com.claim.claim_processing.common.DTO.request.partial.PartialWithdrawalBenefitMapRequestDto;
import com.claim.claim_processing.common.DTO.response.partial.PartialWithdrawalBenefitMapResponseDto;
import com.claim.claim_processing.common.entities.partial.PartialWithdrawalBenefitMap;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartialWithdrawalBenefitMapMapper {

    PartialWithdrawalBenefitMapResponseDto toDto(PartialWithdrawalBenefitMap entity);

    List<PartialWithdrawalBenefitMapResponseDto> toDtoList(List<PartialWithdrawalBenefitMap> entities);

    @Mapping(target = "accumulation", ignore = true)
    @Mapping(target = "benefitComponent", ignore = true)
    PartialWithdrawalBenefitMap toEntity(PartialWithdrawalBenefitMapRequestDto dto);

    @Mapping(target = "accumulation", ignore = true)
    @Mapping(target = "benefitComponent", ignore = true)
    void updateEntityFromDto(PartialWithdrawalBenefitMapRequestDto dto,
                             @MappingTarget PartialWithdrawalBenefitMap entity);
}