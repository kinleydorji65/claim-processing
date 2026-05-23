package com.claim.claim_processing.rule.ruleGateWay.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleFormulaMap;


@Repository
public interface ClaimRuleFormulaMapRepository
                extends JpaRepository<ClaimRuleFormulaMap, Long> {
        void deleteByFormula_Id(Long formulaId);

        List<ClaimRuleFormulaMap> findByFormula_Id(Long formulaId);

}
