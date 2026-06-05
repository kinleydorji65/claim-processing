package com.claim.claim_processing.rule.ruleProcessing.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleProcessing.entities.rule.ClaimComponentExpressionMapping;

import java.util.List;

@Repository
public interface ClaimComponentExpressionMappingRepository
                extends JpaRepository<ClaimComponentExpressionMapping, Long> {

        List<ClaimComponentExpressionMapping> findByComponentMapping_ComponentMappingCodeIgnoreCase(
                        String componentMappingCode);

        boolean existsByComponentMapping_ComponentMappingCodeIgnoreCaseAndExpressionIgnoreCase(
                        String componentMappingCode,
                        String expression);

        boolean existsByComponentMapping_ComponentMappingCodeIgnoreCaseAndExpressionIgnoreCaseAndIdNot(
                        String componentMappingCode,
                        String expression,
                        Long id);
}
