package com.claim.claim_processing.rule.formula.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.formula.entities.ClaimRuleFormulaMap;


@Repository
public interface ClaimRuleFormulaMapRepository
                extends JpaRepository<ClaimRuleFormulaMap, Long> {
        void deleteByFormula_Id(Long formulaId);

        List<ClaimRuleFormulaMap> findByFormula_Id(Long formulaId);
        Optional<ClaimRuleFormulaMap> findByRuleCategoryMap_Id(Long ruleCategoryMapId);

}
