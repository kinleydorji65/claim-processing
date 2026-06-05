package com.claim.claim_processing.rule.ruleProcessing.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleProcessing.entities.rule.ClaimComponentMapping;

import java.util.Optional;

@Repository
public interface ClaimComponentMappingRepository
                extends JpaRepository<ClaimComponentMapping, Long> {

        boolean existsByComponentMappingCodeIgnoreCase(String componentMappingCode);

        boolean existsByComponentMappingCodeIgnoreCaseAndIdNot(
                        String componentMappingCode,
                        Long id);

        Optional<ClaimComponentMapping> findByComponentMappingCodeIgnoreCase(
                        String componentMappingCode);
}
