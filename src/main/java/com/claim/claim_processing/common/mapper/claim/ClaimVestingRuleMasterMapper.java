package com.claim.claim_processing.common.mapper.claim;

import com.claim.claim_processing.common.DTO.request.claim.ClaimVestingRuleRequestDto;
import com.claim.claim_processing.common.DTO.response.claim.ClaimVestingRuleResponseDto;
import com.claim.claim_processing.common.DTO.response.claim.VestingRefundTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.common.RuleTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.contribution.BenefitComponentTypeResponseDto;
import com.claim.claim_processing.common.DTO.response.others.agency.agencyRelated.AgencyCategoryDTO;
import com.claim.claim_processing.common.entities.claim.ClaimVestingRuleMaster;
import com.claim.claim_processing.common.entities.contribution.BenefitComponentTypeMaster;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ClaimVestingRuleMasterMapper {

        // =========================
        // ENTITY → RESPONSE DTO
        // =========================
        @Mapping(source = "category", target = "category")
        @Mapping(source = "ruleType", target = "ruleType")
        public abstract ClaimVestingRuleResponseDto toResponseDto(ClaimVestingRuleMaster entity);

        @Mapping(target = "category", ignore = true)
        @Mapping(target = "ruleType", ignore = true)
        @Mapping(target = "refundType", ignore = true)
        public abstract ClaimVestingRuleResponseDto toResponseDto(
                        ClaimVestingRuleMaster entity,
                        List<BenefitComponentTypeMaster> benefitComponentTypes);

        @AfterMapping
        protected void setOtherMappers(
                        ClaimVestingRuleMaster entity,
                        List<BenefitComponentTypeMaster> benefitComponentTypes,
                        @MappingTarget ClaimVestingRuleResponseDto dto) {
                dto.setCategory(
                                entity.getCategory() != null ? categoryToResponseDto(entity) : null);

                dto.setRuleType(
                                entity.getRuleType() != null ? ruleTypeToResponseDto(entity) : null);

                VestingRefundTypeResponseDto refundTypeDto = entity.getRefundType() != null
                                ? refundTypeToResponseDto(entity)
                                : null;

                if (refundTypeDto != null) {
                        refundTypeDto.setBenefitComponentTypes(
                                        benefitComponentTypes == null
                                                        ? List.of()
                                                        : benefitComponentTypes.stream()
                                                                        .map(this::benefitComponentTypeToResponseDto)
                                                                        .toList());
                }

                dto.setRefundType(refundTypeDto);
        }

        protected BenefitComponentTypeResponseDto benefitComponentTypeToResponseDto(
                        BenefitComponentTypeMaster entity) {
                if (entity == null) {
                        return null;
                }

                return BenefitComponentTypeResponseDto.builder()
                                .id(entity.getId())
                                .code(entity.getCode())
                                .name(entity.getName())
                                .isActive(entity.getIsActive())
                                .build();
        }

        public AgencyCategoryDTO categoryToResponseDto(ClaimVestingRuleMaster entity) {
                return AgencyCategoryDTO.builder()
                                .categoryId(entity.getCategory().getCategoryId())
                                .categoryName(entity.getCategory().getCategoryName())
                                .build();
        }

        public RuleTypeResponseDto ruleTypeToResponseDto(ClaimVestingRuleMaster entity) {
                return RuleTypeResponseDto.builder()
                                .id(entity.getRuleType().getId())
                                .name(entity.getRuleType().getName())
                                .code(entity.getRuleType().getCode())
                                .isActive(entity.getRuleType().getIsActive())
                                .build();
        }

        public VestingRefundTypeResponseDto refundTypeToResponseDto(ClaimVestingRuleMaster entity) {
                return VestingRefundTypeResponseDto.builder()
                                .id(entity.getRefundType().getId())
                                .name(entity.getRefundType().getName())
                                .code(entity.getRefundType().getCode())
                                .isActive(entity.getRefundType().getIsActive())
                                .build();
        }

        public abstract List<ClaimVestingRuleResponseDto> toResponseDto(List<ClaimVestingRuleMaster> entities);

        // =========================
        // REQUEST DTO → ENTITY
        // =========================
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "category", ignore = true)
        @Mapping(target = "refundType", ignore = true)
        @Mapping(target = "ruleType", ignore = true)

        @Mapping(target = "isActive", ignore = true)
        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        @Mapping(target = "createdBy", ignore = true)
        @Mapping(target = "updatedBy", ignore = true)
        public abstract ClaimVestingRuleMaster toEntity(ClaimVestingRuleRequestDto dto);

        // =========================
        // PATCH UPDATE
        // =========================
        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "category", ignore = true)
        @Mapping(target = "refundType", ignore = true)
        @Mapping(target = "ruleType", ignore = true)

        @Mapping(target = "createdAt", ignore = true)
        @Mapping(target = "createdBy", ignore = true)
        @Mapping(target = "isActive", ignore = true)
        public abstract void updateEntityFromDto(
                        ClaimVestingRuleRequestDto dto,
                        @MappingTarget ClaimVestingRuleMaster entity);
}