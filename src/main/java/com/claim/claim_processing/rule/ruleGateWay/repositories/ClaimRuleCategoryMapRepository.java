package com.claim.claim_processing.rule.ruleGateWay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.claim.claim_processing.rule.ruleGateWay.entities.ClaimRuleCategoryMap;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClaimRuleCategoryMapRepository
                extends JpaRepository<ClaimRuleCategoryMap, Long> {

        List<ClaimRuleCategoryMap> findByRule_Id(Long ruleId);

        List<ClaimRuleCategoryMap> findByCondition_Id(Long conditionId);

        List<ClaimRuleCategoryMap> findByCategory_CategoryId(String categoryId);

        List<ClaimRuleCategoryMap> findByIsActive(String isActive);

        Optional<ClaimRuleCategoryMap> findByRule_IdAndCondition_IdAndCategory_CategoryId(
                        Long ruleId,
                        Long conditionId,
                        String categoryId);

        List<ClaimRuleCategoryMap> findByRule_IdAndCondition_Id(
                        Long ruleId,
                        Long conditionId);

        boolean existsByRule_IdAndCondition_IdAndCategory_CategoryId(
                        Long ruleId,
                        Long conditionId,
                        String categoryId);

        void deleteByRule_IdAndCategory_CategoryIdIn(Long ruleId, Set<String> categoriesToDelete);

        List<ClaimRuleCategoryMap> findByRule_IdAndCondition_IdAndCategory_CategoryIdIn(
                        Long ruleId,
                        Long conditionId,
                        Set<String> categoryIds);
}