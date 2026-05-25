package com.claim.claim_processing.common.repository.claim;

import com.claim.claim_processing.common.entities.claim.ClaimTypeRuleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimTypeRuleMapRepository extends JpaRepository<ClaimTypeRuleMap, Long> {

    List<ClaimTypeRuleMap> findByClaimTypeId(Long claimTypeId);

    List<ClaimTypeRuleMap> findByRuleTypeId(Long ruleTypeId);

    boolean existsByClaimTypeIdAndRuleTypeId(Long claimTypeId, Long ruleTypeId);
    Optional<ClaimTypeRuleMap> findByClaimType_IdAndRuleType_Id(Long claimTypeId, Long ruleTypeId);
    List<ClaimTypeRuleMap> findByClaimType_IdAndRuleType_IdIn(Long claimTypeId, List<Long> ruleTypeIds);
    List<ClaimTypeRuleMap> findByClaimType_Id(Long claimTypeId);

    @Query("SELECT DISTINCT ctrm FROM ClaimTypeRuleMap ctrm " +
           "LEFT JOIN FETCH ctrm.ruleType " +
           "WHERE ctrm.claimType.id = :claimTypeId")
    List<ClaimTypeRuleMap> findByClaimTypeIdWithRules(@Param("claimTypeId") Long claimTypeId);

    @Query("SELECT ctrm FROM ClaimTypeRuleMap ctrm " +
           "WHERE ctrm.claimType.id IN :claimTypeIds")
    List<ClaimTypeRuleMap> findByClaimTypeIdIn(@Param("claimTypeIds") List<Long> claimTypeIds);
}