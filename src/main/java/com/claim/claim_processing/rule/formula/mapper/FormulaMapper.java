package com.claim.claim_processing.rule.formula.mapper;


import java.util.List;

import org.springframework.stereotype.Component;

import com.claim.claim_processing.common.repository.contribution.ComponentMasterRepository;
import com.claim.claim_processing.rule.formula.dto.ClaimFormulaResponseDto;
import com.claim.claim_processing.rule.formula.dto.FormulaComponentMapResponseDto;
import com.claim.claim_processing.rule.formula.dto.RuleFormulaMapResponseDto;
import com.claim.claim_processing.rule.formula.entities.ClaimFormulaComponentMap;
import com.claim.claim_processing.rule.formula.entities.ClaimFormulaMaster;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleCategoryMap;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FormulaMapper {
        private final ComponentMasterRepository componentMasterRepository;
    

public ClaimFormulaResponseDto toResponseDto(ClaimFormulaMaster entity, List<ClaimRuleCategoryMap> categoryMaps, List<ClaimFormulaComponentMap> componentMaps) {

    if (entity == null) {

        return null;
    }
    List<RuleFormulaMapResponseDto> ruleFormulaMaps = categoryMaps.stream().map(map -> {
        RuleFormulaMapResponseDto mapDto = RuleFormulaMapResponseDto.builder()
                .id(map.getId())
                .formulaRuleId(map.getRule().getId())
                .conditionId(map.getCondition().getId())
                .categoryId(map.getCategory().getCategoryId())
                .createdAt(map.getCreatedAt())
                .createdBy(map.getCreatedBy())
                .updatedAt(map.getUpdatedAt())
                .updatedBy(map.getUpdatedBy())
                .isActive(map.getIsActive())
                .build();
        return mapDto;
    }).toList();
    List<FormulaComponentMapResponseDto> componentMapDtos = componentMaps != null ? componentMaps.stream().map(map -> {
        FormulaComponentMapResponseDto mapDto = FormulaComponentMapResponseDto.builder()
                .id(map.getId())
                .componentId(map.getComponent().getId())
                .componentName(getComponentName(map.getComponent().getId()))
                .variableCode(map.getVariableCode())
                .sourceType(map.getSourceType())
                .isRequired(map.getIsRequired())
                .createdAt(map.getCreatedAt())
                .createdBy(map.getCreatedBy())
                .updatedAt(map.getUpdatedAt())
                .updatedBy(map.getUpdatedBy())
                .isActive(map.getIsActive())
                .build();
        return mapDto;
    }).toList() : null;
    ClaimFormulaResponseDto responseDto = ClaimFormulaResponseDto.builder()
        .id(entity.getId())
        .formulaCode(entity.getFormulaCode())
        .formulaName(entity.getFormulaName())
        .description(entity.getDescription())
        .expressionText(entity.getExpressionText())
        .outputVariableCode(entity.getOutputVariableCode())
        .returnType(entity.getReturnType())
        .versionNo(entity.getVersionNo())
        .effectiveFrom(entity.getEffectiveFrom())
        .effectiveTo(entity.getEffectiveTo())
        .isActive(entity.getIsActive())
        .createdBy(entity.getCreatedBy())
        .updatedBy(entity.getUpdatedBy())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .ruleFormulaMaps(ruleFormulaMaps)
        .formulaComponents(componentMapDtos)
        .build();
        return responseDto;
}

private String getComponentName(Long componentId) {
    return componentMasterRepository.findById(componentId)
            .map(component -> component.getName())
            .orElse(null);
}

}