package com.claim.claim_processing.rule.ruleGateWay.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimFormulaComponentMap;


@Repository
public interface ClaimFormulaComponentMapRepository
                extends JpaRepository<ClaimFormulaComponentMap, Long> {
        void deleteByFormula_Id(Long formulaId);

        List<ClaimFormulaComponentMap> findByFormula_Id(Long formulaId);
}
