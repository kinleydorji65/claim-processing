package com.claim.claim_processing.rule.formula.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.claim.claim_processing.rule.formula.dto.ClaimFormulaResponseDto;
import com.claim.claim_processing.rule.formula.entities.ClaimFormulaComponentMap;
import com.claim.claim_processing.rule.formula.entities.ClaimFormulaMaster;
import com.claim.claim_processing.rule.formula.entities.ClaimRuleFormulaMap;
import com.claim.claim_processing.rule.formula.mapper.FormulaMapper;
import com.claim.claim_processing.rule.formula.repositories.ClaimFormulaComponentMapRepository;
import com.claim.claim_processing.rule.formula.repositories.ClaimFormulaMasterRepository;
import com.claim.claim_processing.rule.formula.repositories.ClaimRuleFormulaMapRepository;
import com.claim.claim_processing.rule.formula.service.FormulaService;
import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleCategoryMap;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleCategoryMapRepository;
import com.claim.claim_processing.rule.ruleGateWay.repositories.ClaimRuleComponentMapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FormulaServiceImpl implements FormulaService {
    private final FormulaMapper formulaMapper;
    private final ClaimFormulaMasterRepository formulaRepository;
    private final ClaimRuleFormulaMapRepository mapRepository;
        private final ClaimRuleCategoryMapRepository claimRuleCategoryMapRepository;
    private final ClaimRuleComponentMapRepository componentMapRepository;
    private final ClaimFormulaComponentMapRepository formulaComponentMapRepository;

    private static final Map<String, String> CONSTRAINT_MESSAGES = Map.of(
            "UK_CLAIM_FORMULA_CODE_VERSION",
            "Formula code already exists with this version",

            "UK_FORMULA_VARIABLE_CODE",
            "Variable code already exists",

            "CK_CLAIM_FORMULA_ACTIVE",
            "Invalid active status");
    @Override
@Transactional(readOnly = true)
public ClaimFormulaResponseDto getBySubRuleId(Long subRuleId, Long conditionId, String categoryId) {

    ClaimRuleCategoryMap categoryMap =
            claimRuleCategoryMapRepository
                    .findByRule_IdAndCondition_IdAndCategory_CategoryId(
                            subRuleId,
                            conditionId,
                            categoryId)
                    .orElseThrow(() -> new RuntimeException(
                            "Category mapping not found"));

    ClaimRuleFormulaMap ruleFormulaMap =
            mapRepository.findByRuleCategoryMap_Id(categoryMap.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Formula mapping not found"));

    ClaimFormulaMaster formula = ruleFormulaMap.getFormula();

    List<ClaimRuleCategoryMap> categoryMaps =
            List.of(categoryMap);

    List<ClaimFormulaComponentMap> componentMaps =
            formulaComponentMapRepository.findByFormula_Id(formula.getId());

    return formulaMapper.toResponseDto(
            formula,
            categoryMaps,
            componentMaps);
}
    
}
