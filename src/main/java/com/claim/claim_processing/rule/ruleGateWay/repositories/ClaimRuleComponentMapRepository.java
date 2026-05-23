package com.claim.claim_processing.rule.ruleGateWay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleComponentMap;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ClaimRuleComponentMapRepository extends JpaRepository<ClaimRuleComponentMap, Long> {

    List<ClaimRuleComponentMap> findByRuleCategoryMap_Id(Long ruleCategoryMapId);

    List<ClaimRuleComponentMap> findByRuleCategoryMap_IdAndIsActive(Long ruleCategoryMapId, String isActive);

    Optional<ClaimRuleComponentMap> findByRuleCategoryMap_IdAndComponent_Id(Long ruleCategoryMapId, Long componentId);

    boolean existsByRuleCategoryMap_IdAndComponent_Id(Long ruleCategoryMapId, Long componentId);

    void deleteByRuleCategoryMap_IdAndComponent_IdIn(Long ruleCategoryMapId, Set<Long> componentIds);

    boolean existsByRuleCategoryMap_IdAndComponent_IdAndIdNot(
            Long ruleCategoryMapId,
            Long componentId,
            Long id);

    void deleteByRuleCategoryMap_IdIn(List<Long> categoryMapIdsToDelete);
}