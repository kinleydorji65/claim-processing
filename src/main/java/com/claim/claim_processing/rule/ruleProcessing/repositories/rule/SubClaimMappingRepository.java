package com.claim.claim_processing.rule.ruleProcessing.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleProcessing.entities.rule.SubClaimMapping;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClaimMappingRepository extends JpaRepository<SubClaimMapping, Long> {

    boolean existsBySubClaimCodeIgnoreCase(String subClaimCode);

    boolean existsBySubClaimCodeIgnoreCaseAndIdNot(
            String subClaimCode,
            Long id
    );

    Optional<SubClaimMapping> findBySubClaimCodeIgnoreCase(
            String subClaimCode
    );

    List<SubClaimMapping> findByRuleType_CodeIgnoreCase(
            String ruleCode
    );
    List<SubClaimMapping> findByRuleType_CodeIgnoreCaseAndIsActive(
            String ruleCode,
            String status
    );

    Optional<SubClaimMapping> findFirstByRuleType_CodeIgnoreCase(
            String ruleCode
    );
    Optional<SubClaimMapping> findFirstBySubClaimCodeIgnoreCase(
            String subClaimCode
    );

    Optional<SubClaimMapping> findByRuleType_IdAndCategorySchemeMapping_Id(
            Long ruleTypeId,
            Long categorySchemeMappingId
    );
}
