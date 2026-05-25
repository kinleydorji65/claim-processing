package com.claim.claim_processing.rule.formula.service;

import com.claim.claim_processing.rule.formula.dto.ClaimFormulaResponseDto;


public interface FormulaService {
    ClaimFormulaResponseDto getBySubRuleId(Long subRuleId, Long conditionId, String categoryId);
}
