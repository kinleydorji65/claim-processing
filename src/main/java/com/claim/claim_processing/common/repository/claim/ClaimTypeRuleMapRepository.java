package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimTypeRuleMapRepository extends JpaRepository<ClaimTypeRuleMap, Long> {

    List<ClaimTypeRuleMap> findByClaimTypeId(Long claimTypeId);

    List<ClaimTypeRuleMap> findByRuleTypeId(Long ruleTypeId);

    boolean existsByClaimTypeIdAndRuleTypeId(Long claimTypeId, Long ruleTypeId);
}