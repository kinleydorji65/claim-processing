package com.claim.claim_processing.rule.ruleGateWay.repositories.rule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.rule.SubClaimMapping;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubClaimMappingRepository extends JpaRepository<SubClaimMapping, Long> {

    boolean existsBySubClaimCodeIgnoreCase(String subClaimCode);

    boolean existsBySubClaimCodeIgnoreCaseAndIdNot(String subClaimCode, Long id);

    List<SubClaimMapping> findByRuleType_CodeIgnoreCase(String ruleCode);
    Optional<SubClaimMapping> findBySubClaimCodeIgnoreCase(String subClaimCode);
    Optional<SubClaimMapping> findByRuleType_IdAndCategorySchemeMapping_Id(Long ruleTypeId, Long categorySchemeMappingId);
}
