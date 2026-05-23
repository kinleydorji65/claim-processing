package com.claim.claim_processing.rule.ruleGateWay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleRefundTypeMap;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRuleRefundTypeMapRepository
                extends JpaRepository<ClaimRuleRefundTypeMap, Long> {

        List<ClaimRuleRefundTypeMap> findByRuleCategoryMap_Id(Long ruleCategoryMapId);

        List<ClaimRuleRefundTypeMap> findByRuleCategoryMap_IdAndIsActive(
                        Long ruleCategoryMapId,
                        String isActive);

        List<ClaimRuleRefundTypeMap> findByRefundType_Id(Long refundTypeId);

        Optional<ClaimRuleRefundTypeMap> findByRuleCategoryMap_IdAndRefundType_Id(
                        Long ruleCategoryMapId,
                        Long refundTypeId);

        boolean existsByRuleCategoryMap_IdAndRefundType_Id(
                        Long ruleCategoryMapId,
                        Long refundTypeId);

        boolean existsByRuleCategoryMap_IdAndRefundType_IdAndIdNot(
                        Long ruleCategoryMapId,
                        Long refundTypeId,
                        Long id);

        void deleteByRuleCategoryMap_Id(Long ruleCategoryMapId);
}
